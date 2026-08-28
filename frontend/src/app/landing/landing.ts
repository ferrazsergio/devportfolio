import { AfterViewInit, Component, ElementRef, OnDestroy, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { LogoComponent } from '../core/ui/logo/logo';
import { ThemeToggleComponent } from '../core/ui/theme-toggle/theme-toggle';

gsap.registerPlugin(ScrollTrigger);

@Component({
  selector: 'app-landing',
  imports: [RouterLink, LogoComponent, ThemeToggleComponent],
  templateUrl: './landing.html',
  styleUrl: './landing.css',
})
export class LandingComponent implements AfterViewInit, OnDestroy {
  protected readonly githubUrl = 'https://github.com/ferrazsergio/devportfolio';

  private readonly elementRef = inject(ElementRef<HTMLElement>);
  private readonly scrollTriggers: ScrollTrigger[] = [];
  private readonly reduceMotion =
    typeof window !== 'undefined' && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  ngAfterViewInit(): void {
    requestAnimationFrame(() => requestAnimationFrame(() => this.initAnimations()));
  }

  ngOnDestroy(): void {
    this.scrollTriggers.forEach((trigger) => trigger.kill());
  }

  /**
   * Animações de entrada (hero) e de rolagem (seções). Se o usuário pediu
   * menos movimento (prefers-reduced-motion), pulamos tudo — o conteúdo já é
   * totalmente visível por padrão via CSS, então nada quebra.
   */
  private initAnimations(): void {
    if (this.reduceMotion) {
      return;
    }
    const root = this.elementRef.nativeElement;
    const hero = root.querySelector('.hero');
    const heroReveals = hero?.querySelectorAll('.reveal');
    if (heroReveals && heroReveals.length > 0) {
      gsap.set(heroReveals, { opacity: 0, y: 24 });
      gsap.to(heroReveals, { opacity: 1, y: 0, duration: 0.7, stagger: 0.1, ease: 'power2.out' });
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
      gsap.set(element, { opacity: 0, y: 24 });
      const trigger = ScrollTrigger.create({
        trigger: element,
        start: 'top 88%',
        once: true,
        onEnter: () =>
          gsap.to(element, { opacity: 1, y: 0, duration: 0.6, ease: 'power2.out' }),
      });
      this.scrollTriggers.push(trigger);
    });
  }
}
