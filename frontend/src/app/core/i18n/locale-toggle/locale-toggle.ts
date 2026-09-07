import { AfterViewInit, Component, ElementRef, effect, inject } from '@angular/core';
import gsap from 'gsap';
import { Locale, LocaleService } from '../locale.service';
import { TranslatePipe } from '../translate.pipe';

/**
 * Switch segmentado (estilo iOS) em vez de um botão único que troca de rótulo —
 * mostra os dois idiomas o tempo todo (mais informação, menos "flip seco") e o
 * indicador desliza com uma leve compressão + mola ao trocar.
 */
@Component({
  selector: 'app-locale-toggle',
  imports: [TranslatePipe],
  templateUrl: './locale-toggle.html',
  styleUrl: './locale-toggle.css',
})
export class LocaleToggleComponent implements AfterViewInit {
  protected readonly localeService = inject(LocaleService);
  private readonly elementRef = inject(ElementRef<HTMLElement>);
  private readonly reduceMotion =
    typeof window !== 'undefined' && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  private ready = false;

  constructor() {
    effect(() => {
      const locale = this.localeService.locale();
      if (this.ready) {
        this.slideThumb(locale);
      }
    });
  }

  ngAfterViewInit(): void {
    const thumb = this.elementRef.nativeElement.querySelector('.locale-switch__thumb');
    if (thumb) {
      gsap.set(thumb, { xPercent: this.localeService.locale() === 'en' ? 100 : 0 });
    }
    this.ready = true;
  }

  protected select(locale: Locale): void {
    // A troca de idioma é sempre síncrona — a animação é só um efeito visual
    // por cima, nunca pode atrasar a funcionalidade real.
    this.localeService.setLocale(locale);
  }

  private slideThumb(locale: Locale): void {
    const thumb = this.elementRef.nativeElement.querySelector('.locale-switch__thumb');
    if (!thumb) {
      return;
    }
    const xPercent = locale === 'en' ? 100 : 0;
    if (this.reduceMotion) {
      gsap.set(thumb, { xPercent });
      return;
    }
    gsap
      .timeline()
      .to(thumb, { scaleY: 0.8, duration: 0.12, ease: 'power2.out' })
      .to(thumb, { xPercent, scaleY: 1, duration: 0.5, ease: 'back.out(1.8)' }, '-=0.04');
  }
}
