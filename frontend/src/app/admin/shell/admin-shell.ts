import { AfterViewInit, Component, ElementRef, OnDestroy, inject } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import gsap from 'gsap';
import { Subscription, filter } from 'rxjs';
import { LogoComponent } from '../../core/ui/logo/logo';
import { ThemeToggleComponent } from '../../core/ui/theme-toggle/theme-toggle';
import { LocaleToggleComponent } from '../../core/i18n/locale-toggle/locale-toggle';
import { TranslatePipe } from '../../core/i18n/translate.pipe';
import { UserMenuComponent } from './user-menu/user-menu';

@Component({
  selector: 'app-admin-shell',
  imports: [
    RouterLink,
    RouterLinkActive,
    RouterOutlet,
    LogoComponent,
    ThemeToggleComponent,
    LocaleToggleComponent,
    UserMenuComponent,
    TranslatePipe,
  ],
  templateUrl: './admin-shell.html',
  styleUrl: './admin-shell.css',
})
export class AdminShellComponent implements AfterViewInit, OnDestroy {
  private readonly elementRef = inject(ElementRef<HTMLElement>);
  private readonly router = inject(Router);
  private readonly reduceMotion =
    typeof window !== 'undefined' && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  private routerSub: Subscription | null = null;
  private readonly onResize = () => this.moveIndicator(false);

  ngAfterViewInit(): void {
    requestAnimationFrame(() => this.moveIndicator(false));
    this.routerSub = this.router.events
      .pipe(filter((event): event is NavigationEnd => event instanceof NavigationEnd))
      .subscribe(() => requestAnimationFrame(() => this.moveIndicator(true)));
    window.addEventListener('resize', this.onResize);
  }

  ngOnDestroy(): void {
    this.routerSub?.unsubscribe();
    window.removeEventListener('resize', this.onResize);
  }

  /**
   * Indicador que desliza até o link ativo do menu — mesma linguagem visual
   * do switch de idioma e do indicador de seção do portfólio público.
   */
  private moveIndicator(animate: boolean): void {
    const nav = this.elementRef.nativeElement.querySelector('.sidebar__nav');
    const indicator = nav?.querySelector('.sidebar__nav-indicator');
    const active = nav?.querySelector('a.active');
    if (!nav || !indicator || !active) {
      return;
    }
    // Calcula x/y e largura/altura juntos: no layout de coluna (desktop) só o Y
    // muda entre os links; no layout de linha (sidebar vira topo em telas
    // estreitas) só o X muda — computar os dois cobre ambos sem caso especial.
    const activeRect = (active as HTMLElement).getBoundingClientRect();
    const navRect = (nav as HTMLElement).getBoundingClientRect();
    const targetX = activeRect.left - navRect.left;
    const targetY = activeRect.top - navRect.top;
    const targetWidth = activeRect.width;
    const targetHeight = activeRect.height;

    if (animate && !this.reduceMotion) {
      gsap.to(indicator, {
        x: targetX,
        y: targetY,
        width: targetWidth,
        height: targetHeight,
        opacity: 1,
        duration: 0.4,
        ease: 'power2.out',
      });
    } else {
      gsap.set(indicator, { x: targetX, y: targetY, width: targetWidth, height: targetHeight, opacity: 1 });
    }
  }
}
