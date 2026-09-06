import { Component, ElementRef, inject } from '@angular/core';
import gsap from 'gsap';
import { LocaleService } from '../locale.service';
import { TranslatePipe } from '../translate.pipe';

@Component({
  selector: 'app-locale-toggle',
  imports: [TranslatePipe],
  templateUrl: './locale-toggle.html',
  styleUrl: './locale-toggle.css',
})
export class LocaleToggleComponent {
  protected readonly localeService = inject(LocaleService);
  private readonly elementRef = inject(ElementRef<HTMLElement>);
  private readonly reduceMotion =
    typeof window !== 'undefined' && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  protected onToggleClick(): void {
    // A troca de idioma é sempre síncrona — a animação é só um efeito visual
    // por cima, nunca pode atrasar a funcionalidade real.
    this.localeService.toggle();

    if (this.reduceMotion) {
      return;
    }
    requestAnimationFrame(() => {
      const label = this.elementRef.nativeElement.querySelector('.locale-toggle__label');
      const button = this.elementRef.nativeElement.querySelector('.locale-toggle');
      if (!label || !button) {
        return;
      }
      gsap
        .timeline()
        .fromTo(
          label,
          { rotateX: -90, opacity: 0 },
          { rotateX: 0, opacity: 1, duration: 0.45, ease: 'back.out(3)' },
        )
        .fromTo(
          button,
          { boxShadow: '0 0 0 0 var(--color-primary-600)' },
          {
            boxShadow: '0 0 0 6px rgba(24, 119, 242, 0)',
            duration: 0.6,
            ease: 'power2.out',
          },
          0,
        );
    });
  }
}
