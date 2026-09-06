import { AfterViewInit, Component, ElementRef, OnDestroy, effect, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { SplitText } from 'gsap/SplitText';
import { createMagneticHover } from '../core/animation/magnetic';
import { LocaleService } from '../core/i18n/locale.service';
import { translateKey, TranslatePipe } from '../core/i18n/translate.pipe';
import { LocaleToggleComponent } from '../core/i18n/locale-toggle/locale-toggle';
import { LogoComponent } from '../core/ui/logo/logo';
import { ThemeToggleComponent } from '../core/ui/theme-toggle/theme-toggle';

gsap.registerPlugin(ScrollTrigger, SplitText);

@Component({
  selector: 'app-landing',
  imports: [RouterLink, LogoComponent, ThemeToggleComponent, LocaleToggleComponent, TranslatePipe],
  templateUrl: './landing.html',
  styleUrl: './landing.css',
})
export class LandingComponent implements AfterViewInit, OnDestroy {
  protected readonly githubUrl = 'https://github.com/ferrazsergio/devportfolio';

  private readonly elementRef = inject(ElementRef<HTMLElement>);
  private readonly localeService = inject(LocaleService);
  private readonly scrollTriggers: ScrollTrigger[] = [];
  private readonly magneticCleanups: (() => void)[] = [];
  private splitHeroTitle: SplitText | null = null;
  private heroReady = false;
  private readonly reduceMotion =
    typeof window !== 'undefined' && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  constructor() {
    // O SplitText substitui o nó de texto que o Angular usa pra interpolar
    // `{{ ... | translate }}` por spans próprios — depois disso, uma troca de
    // idioma nunca mais chegaria na tela sozinha. Por isso, ao trocar, a gente
    // reverte o split e recria com o texto (já traduzido) lido direto do dicionário.
    effect(() => {
      const locale = this.localeService.locale();
      if (this.heroReady) {
        this.resplitHeroTitle(locale);
      }
    });
  }

  ngAfterViewInit(): void {
    requestAnimationFrame(() =>
      requestAnimationFrame(() => {
        this.initAnimations();
        this.heroReady = true;
      }),
    );
  }

  ngOnDestroy(): void {
    this.scrollTriggers.forEach((trigger) => trigger.kill());
    this.magneticCleanups.forEach((cleanup) => cleanup());
    this.splitHeroTitle?.revert();
  }

  /**
   * Animações de entrada (hero) e de rolagem (seções). Se o usuário pediu
   * menos movimento (prefers-reduced-motion), pulamos tudo — o conteúdo já é
   * totalmente visível por padrão via CSS, então nada quebra.
   */
  private initAnimations(): void {
    const root = this.elementRef.nativeElement;

    if (this.reduceMotion) {
      this.setupMagneticHover(root);
      return;
    }

    const hero = root.querySelector('.hero');
    const heroTitle = hero?.querySelector('h1');
    const heroReveals: Element[] = [];
    hero?.querySelectorAll('.reveal').forEach((el: Element) => {
      if (el.tagName !== 'H1') {
        heroReveals.push(el);
      }
    });

    const timeline = gsap.timeline();

    // Título em destaque: revelado palavra por palavra (não a frase inteira de
    // uma vez), efeito de "leitura cinematográfica" em vez do fade genérico —
    // é o elemento mais importante da página, merece um tratamento à parte.
    if (heroTitle) {
      this.splitHeroTitle = SplitText.create(heroTitle, { type: 'words', mask: 'words' });
      gsap.set(this.splitHeroTitle.words, { yPercent: 110, opacity: 0 });
      timeline.to(this.splitHeroTitle.words, {
        yPercent: 0,
        opacity: 1,
        duration: 0.9,
        stagger: 0.06,
        ease: 'expo.out',
      });
    }

    if (heroReveals.length > 0) {
      gsap.set(heroReveals, { opacity: 0, y: 24 });
      timeline.to(
        heroReveals,
        { opacity: 1, y: 0, duration: 0.7, stagger: 0.12, ease: 'expo.out' },
        heroTitle ? '-=0.5' : 0,
      );
    }

    const blobOne = root.querySelector('.hero__blob--one');
    const blobTwo = root.querySelector('.hero__blob--two');
    if (blobOne) {
      gsap.to(blobOne, { x: 30, y: 20, duration: 8, repeat: -1, yoyo: true, ease: 'sine.inOut' });
    }
    if (blobTwo) {
      gsap.to(blobTwo, { x: -20, y: -30, duration: 10, repeat: -1, yoyo: true, ease: 'sine.inOut' });
    }

    const sectionReveals = root.querySelectorAll('main .reveal');
    sectionReveals.forEach((element: Element) => {
      if (element.closest('.hero')) {
        return;
      }
      gsap.set(element, { opacity: 0, y: 32, scale: 0.97, filter: 'blur(6px)' });
      const trigger = ScrollTrigger.create({
        trigger: element,
        start: 'top 88%',
        once: true,
        onEnter: () =>
          gsap.to(element, {
            opacity: 1,
            y: 0,
            scale: 1,
            filter: 'blur(0px)',
            duration: 0.9,
            ease: 'expo.out',
          }),
      });
      this.scrollTriggers.push(trigger);
    });

    this.setupMagneticHover(root);
  }

  private setupMagneticHover(root: HTMLElement): void {
    const magneticTargets = root.querySelectorAll('.hero__actions .btn, .landing-nav__links .btn-primary');
    magneticTargets.forEach((target) =>
      this.magneticCleanups.push(createMagneticHover(target as HTMLElement, 0.3)),
    );
  }

  private resplitHeroTitle(locale: 'pt' | 'en'): void {
    const heroTitle = this.elementRef.nativeElement.querySelector('.hero h1');
    if (!heroTitle) {
      return;
    }
    this.splitHeroTitle?.revert();
    heroTitle.textContent = translateKey(locale, 'landing.heroTitle');

    if (this.reduceMotion) {
      return;
    }
    this.splitHeroTitle = SplitText.create(heroTitle, { type: 'words', mask: 'words' });
    gsap.fromTo(
      this.splitHeroTitle.words,
      { yPercent: 60, opacity: 0 },
      { yPercent: 0, opacity: 1, duration: 0.45, stagger: 0.02, ease: 'power2.out' },
    );
  }
}
