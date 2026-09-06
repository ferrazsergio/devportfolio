import { Component, ElementRef, inject } from '@angular/core';
import gsap from 'gsap';
import { ThemeService } from '../../theme/theme.service';
import { TranslatePipe } from '../../i18n/translate.pipe';

@Component({
  selector: 'app-theme-toggle',
  imports: [TranslatePipe],
  templateUrl: './theme-toggle.html',
  styleUrl: './theme-toggle.css',
})
export class ThemeToggleComponent {
  protected readonly themeService = inject(ThemeService);
  private readonly elementRef = inject(ElementRef<HTMLElement>);
  private readonly reduceMotion =
    typeof window !== 'undefined' && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  protected onToggleClick(): void {
    // A troca de tema em si é sempre síncrona e imediata — a animação abaixo é
    // só um efeito visual em cima dela, nunca pode bloquear ou atrasar a
    // funcionalidade real (ex.: se o GSAP travar por qualquer motivo).
    this.themeService.toggle();

    if (this.reduceMotion) {
      return;
    }
    // Espera o Angular re-renderizar o ícone novo (o signal do tema já mudou
    // acima, mas o DOM só reflete isso no próximo ciclo) antes de animá-lo.
    requestAnimationFrame(() => {
      const icon = this.elementRef.nativeElement.querySelector('.theme-toggle__icon');
      if (!icon) {
        return;
      }
      gsap.fromTo(
        icon,
        { rotate: -90, scale: 0.4, opacity: 0 },
        { rotate: 0, scale: 1, opacity: 1, duration: 0.4, ease: 'back.out(2.5)' },
      );
    });
  }
}
