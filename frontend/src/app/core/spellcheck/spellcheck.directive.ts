import { DOCUMENT } from '@angular/common';
import { AfterViewInit, Directive, ElementRef, OnDestroy, effect, inject } from '@angular/core';
import Typo from 'typo-js';
import { LocaleService } from '../i18n/locale.service';
import { translateKey } from '../i18n/translate.pipe';
import { SpellcheckService } from './spellcheck.service';

const WORD_PATTERN = /[\p{L}'-]+/gu;

/**
 * Overlay (`position: fixed`, texto transparente, só o sublinhado visível)
 * por cima do campo real — nunca toca o campo em si, então digitação, colar
 * e undo/redo continuam 100% nativos.
 */
@Directive({
  selector: 'textarea[appSpellcheck], input[appSpellcheck]',
})
export class SpellcheckDirective implements AfterViewInit, OnDestroy {
  private readonly el = inject(ElementRef<HTMLTextAreaElement | HTMLInputElement>).nativeElement;
  private readonly spellcheckService = inject(SpellcheckService);
  private readonly localeService = inject(LocaleService);
  private readonly document = inject(DOCUMENT);

  private overlay: HTMLDivElement | null = null;
  private popover: HTMLDivElement | null = null;
  private typo: Typo | null = null;
  private resizeObserver: ResizeObserver | null = null;
  private debounceHandle: ReturnType<typeof setTimeout> | undefined;
  private readonly ignored = new Set<string>();

  private readonly onInput = () => this.scheduleHighlight();
  private readonly onScroll = () => this.syncPosition();
  private readonly onClick = (event: MouseEvent) => this.handleClick(event);
  private readonly onWindowChange = () => this.syncPosition();
  private readonly onDocumentClick = (event: MouseEvent) => {
    if (this.popover && !this.popover.contains(event.target as Node) && event.target !== this.el) {
      this.closePopover();
    }
  };

  constructor() {
    effect(() => {
      const locale = this.localeService.locale();
      this.spellcheckService.getChecker(locale).subscribe({
        next: (typo) => {
          this.typo = typo;
          this.ignored.clear();
          this.scheduleHighlight();
        },
        error: () => {
          this.typo = null;
        },
      });
    });
  }

  ngAfterViewInit(): void {
    this.overlay = this.document.createElement('div');
    this.overlay.className = 'spellcheck-overlay';
    this.overlay.setAttribute('aria-hidden', 'true');
    this.document.body.appendChild(this.overlay);
    this.copyStyles();
    this.syncPosition();

    this.el.addEventListener('input', this.onInput);
    this.el.addEventListener('scroll', this.onScroll);
    this.el.addEventListener('click', this.onClick);
    window.addEventListener('resize', this.onWindowChange);
    window.addEventListener('scroll', this.onWindowChange, true);
    this.resizeObserver = new ResizeObserver(() => this.syncPosition());
    this.resizeObserver.observe(this.el);
  }

  ngOnDestroy(): void {
    this.el.removeEventListener('input', this.onInput);
    this.el.removeEventListener('scroll', this.onScroll);
    this.el.removeEventListener('click', this.onClick);
    window.removeEventListener('resize', this.onWindowChange);
    window.removeEventListener('scroll', this.onWindowChange, true);
    this.resizeObserver?.disconnect();
    clearTimeout(this.debounceHandle);
    this.overlay?.remove();
    this.closePopover();
  }

  private copyStyles(): void {
    if (!this.overlay) {
      return;
    }
    const cs = getComputedStyle(this.el);
    const props = [
      'fontFamily',
      'fontSize',
      'fontWeight',
      'lineHeight',
      'letterSpacing',
      'paddingTop',
      'paddingRight',
      'paddingBottom',
      'paddingLeft',
      'borderTopWidth',
      'borderRightWidth',
      'borderBottomWidth',
      'borderLeftWidth',
      'borderStyle',
      'borderColor',
      'boxSizing',
      'textAlign',
      'textIndent',
    ] as const;
    for (const prop of props) {
      this.overlay.style[prop] = cs[prop];
    }
    this.overlay.style.borderColor = 'transparent';
    this.overlay.style.whiteSpace = this.el.tagName === 'TEXTAREA' ? 'pre-wrap' : 'pre';
    this.overlay.style.wordWrap = 'break-word';
    this.overlay.style.overflow = 'hidden';
    this.overlay.style.color = 'transparent';
    this.overlay.style.pointerEvents = 'none';
    this.overlay.style.position = 'fixed';
    this.overlay.style.zIndex = '2';
  }

  private syncPosition(): void {
    if (!this.overlay) {
      return;
    }
    const rect = this.el.getBoundingClientRect();
    this.overlay.style.left = `${rect.left}px`;
    this.overlay.style.top = `${rect.top}px`;
    this.overlay.style.width = `${rect.width}px`;
    this.overlay.style.height = `${rect.height}px`;
    this.overlay.scrollTop = this.el.scrollTop;
    this.overlay.scrollLeft = this.el.scrollLeft;
  }

  private scheduleHighlight(): void {
    clearTimeout(this.debounceHandle);
    this.debounceHandle = setTimeout(() => this.highlight(), 400);
  }

  private highlight(): void {
    if (!this.overlay || !this.typo) {
      return;
    }
    const value = this.el.value;
    let lastIndex = 0;
    let html = '';
    let match: RegExpExecArray | null;
    WORD_PATTERN.lastIndex = 0;
    while ((match = WORD_PATTERN.exec(value))) {
      const word = match[0];
      html += escapeHtml(value.slice(lastIndex, match.index));
      html += this.isMisspelled(word) ? `<mark class="spellcheck-error">${escapeHtml(word)}</mark>` : escapeHtml(word);
      lastIndex = match.index + word.length;
    }
    html += escapeHtml(value.slice(lastIndex));
    this.overlay.innerHTML = `${html}&nbsp;`;
    this.syncPosition();
  }

  private isMisspelled(word: string): boolean {
    if (!this.typo) {
      return false;
    }
    const clean = word.replace(/^[-']+|[-']+$/g, '');
    if (clean.length < 2 || this.ignored.has(clean.toLowerCase())) {
      return false;
    }
    return !this.typo.check(clean) && !this.typo.check(word);
  }

  private handleClick(event: MouseEvent): void {
    if (!this.typo) {
      return;
    }
    const { clientX, clientY } = event;
    // espera o navegador posicionar o cursor de texto antes de ler selectionStart
    setTimeout(() => {
      const found = this.findWordAtCaret();
      if (found && this.isMisspelled(found.word)) {
        this.openPopover(found.word, found.start, found.end, clientX, clientY);
      } else {
        this.closePopover();
      }
    }, 0);
  }

  private findWordAtCaret(): { word: string; start: number; end: number } | null {
    const caret = this.el.selectionStart ?? 0;
    const value = this.el.value;
    WORD_PATTERN.lastIndex = 0;
    let match: RegExpExecArray | null;
    while ((match = WORD_PATTERN.exec(value))) {
      const start = match.index;
      const end = start + match[0].length;
      if (caret >= start && caret <= end) {
        return { word: match[0], start, end };
      }
    }
    return null;
  }

  private openPopover(word: string, start: number, end: number, x: number, y: number): void {
    this.closePopover();
    if (!this.typo) {
      return;
    }
    const locale = this.localeService.locale();
    const suggestions = this.typo.suggest(word, 5);

    const popover = this.document.createElement('div');
    popover.className = 'spellcheck-popover';

    if (suggestions.length === 0) {
      const empty = this.document.createElement('p');
      empty.className = 'spellcheck-popover__empty';
      empty.textContent = translateKey(locale, 'spellcheck.noSuggestions');
      popover.appendChild(empty);
    } else {
      for (const suggestion of suggestions) {
        const button = this.document.createElement('button');
        button.type = 'button';
        button.className = 'spellcheck-popover__suggestion';
        button.textContent = suggestion;
        button.addEventListener('click', (clickEvent) => {
          clickEvent.stopPropagation();
          this.applySuggestion(suggestion, start, end);
        });
        popover.appendChild(button);
      }
    }

    const ignoreButton = this.document.createElement('button');
    ignoreButton.type = 'button';
    ignoreButton.className = 'spellcheck-popover__ignore';
    ignoreButton.textContent = translateKey(locale, 'spellcheck.ignore');
    ignoreButton.addEventListener('click', (clickEvent) => {
      clickEvent.stopPropagation();
      this.ignored.add(word.replace(/^[-']+|[-']+$/g, '').toLowerCase());
      this.closePopover();
      this.highlight();
    });
    popover.appendChild(ignoreButton);

    this.document.body.appendChild(popover);
    this.popover = popover;

    popover.style.left = `${x}px`;
    popover.style.top = `${y + 16}px`;
    const rect = popover.getBoundingClientRect();
    if (rect.right > window.innerWidth) {
      popover.style.left = `${Math.max(8, window.innerWidth - rect.width - 8)}px`;
    }
    if (rect.bottom > window.innerHeight) {
      popover.style.top = `${Math.max(8, y - rect.height - 8)}px`;
    }

    setTimeout(() => this.document.addEventListener('click', this.onDocumentClick), 0);
  }

  private closePopover(): void {
    if (this.popover) {
      this.popover.remove();
      this.popover = null;
      this.document.removeEventListener('click', this.onDocumentClick);
    }
  }

  private applySuggestion(suggestion: string, start: number, end: number): void {
    const value = this.el.value;
    this.el.value = value.slice(0, start) + suggestion + value.slice(end);
    this.el.dispatchEvent(new Event('input', { bubbles: true }));
    const caretPos = start + suggestion.length;
    this.el.setSelectionRange(caretPos, caretPos);
    this.el.focus();
    this.closePopover();
    this.scheduleHighlight();
  }
}

function escapeHtml(text: string): string {
  return text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}
