import { Component, ElementRef, OnDestroy, inject, signal } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { SplitText } from 'gsap/SplitText';
import { createMagneticHover } from '../core/animation/magnetic';
import { LocaleService } from '../core/i18n/locale.service';
import { LocaleToggleComponent } from '../core/i18n/locale-toggle/locale-toggle';
import { TranslatePipe } from '../core/i18n/translate.pipe';
import { en } from '../core/i18n/translations/en';
import { pt } from '../core/i18n/translations/pt';
import { LogoComponent } from '../core/ui/logo/logo';
import { ThemeToggleComponent } from '../core/ui/theme-toggle/theme-toggle';
import { PublicPortfolioApiService } from './public-portfolio-api.service';
import { ProjectStatus, PublicPortfolio, PublicSkill, SkillCategory } from './public-portfolio.model';

gsap.registerPlugin(ScrollTrigger, SplitText);

const CATEGORY_LABELS: Record<SkillCategory, string> = {
  BACKEND: 'Backend',
  FRONTEND: 'Frontend',
  DATABASE: 'Banco de Dados',
  CLOUD: 'Cloud',
  DEVOPS: 'DevOps',
  TOOLS: 'Ferramentas',
  OTHER: 'Outro',
};

const CATEGORY_ORDER: SkillCategory[] = ['BACKEND', 'FRONTEND', 'DATABASE', 'CLOUD', 'DEVOPS', 'TOOLS', 'OTHER'];

const STATUS_LABELS: Record<ProjectStatus, string> = {
  IN_PROGRESS: 'Em andamento',
  COMPLETED: 'Concluído',
  ARCHIVED: 'Arquivado',
};

export interface SkillGroup {
  category: SkillCategory;
  label: string;
  skills: PublicSkill[];
}

@Component({
  selector: 'app-public-portfolio',
  imports: [LogoComponent, ThemeToggleComponent, LocaleToggleComponent, TranslatePipe],
  templateUrl: './public-portfolio.html',
  styleUrl: './public-portfolio.css',
})
export class PublicPortfolioComponent implements OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly api = inject(PublicPortfolioApiService);
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);
  private readonly elementRef = inject(ElementRef<HTMLElement>);
  private readonly localeService = inject(LocaleService);

  private readonly scrollTriggers: ScrollTrigger[] = [];
  private readonly magneticCleanups: (() => void)[] = [];
  private splitHeroName: SplitText | null = null;
  private sectionObserver: IntersectionObserver | null = null;
  private heroSpotlightCleanup: (() => void) | null = null;
  private readonly reduceMotion =
    typeof window !== 'undefined' && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  protected readonly portfolio = signal<PublicPortfolio | null>(null);
  protected readonly loading = signal(true);
  protected readonly notFound = signal(false);
  protected readonly linkCopied = signal(false);
  protected readonly lightboxOpen = signal(false);

  private readonly onLightboxKeydown = (event: KeyboardEvent) => {
    if (event.key === 'Escape') {
      this.closeLightbox();
    }
  };

  constructor() {
    const username = this.route.snapshot.paramMap.get('username')!;
    this.api.getByUsername(username).subscribe({
      next: (data) => {
        this.portfolio.set(data);
        this.loading.set(false);
        this.updateMetaTags(data);
        requestAnimationFrame(() => requestAnimationFrame(() => this.initAnimations()));
      },
      error: () => {
        this.notFound.set(true);
        this.loading.set(false);
        this.title.setTitle('Portfólio não encontrado · DevPortfolio');
      },
    });
  }

  ngOnDestroy(): void {
    this.scrollTriggers.forEach((trigger) => trigger.kill());
    this.magneticCleanups.forEach((cleanup) => cleanup());
    this.splitHeroName?.revert();
    this.sectionObserver?.disconnect();
    document.removeEventListener('keydown', this.onLightboxKeydown);
    this.heroSpotlightCleanup?.();
  }

  /**
   * Animações de entrada (hero) e de rolagem (seções/cards). Se o usuário
   * pediu menos movimento (prefers-reduced-motion), pulamos tudo — o conteúdo
   * já é totalmente visível por padrão via CSS, então nada quebra.
   */
  private initAnimations(): void {
    const root = this.elementRef.nativeElement;

    this.setupActiveNav(root);

    if (this.reduceMotion) {
      this.setupMagneticHover(root);
      return;
    }

    this.setupHeroSpotlight(root);

    const hero = root.querySelector('.hero');
    const heroName = hero?.querySelector('.hero__name');
    const heroReveals: Element[] = [];
    hero?.querySelectorAll('.reveal').forEach((el: Element) => {
      if (!el.classList.contains('hero__name')) {
        heroReveals.push(el);
      }
    });

    const timeline = gsap.timeline();

    // Nome em destaque: revelado palavra por palavra, mesmo tratamento
    // cinematográfico do título da landing — é o elemento mais importante
    // do herói, merece mais que um fade genérico.
    if (heroName) {
      this.splitHeroName = SplitText.create(heroName, { type: 'words', mask: 'words' });
      gsap.set(this.splitHeroName.words, { yPercent: 110, opacity: 0 });
      timeline.to(this.splitHeroName.words, {
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
        { opacity: 1, y: 0, duration: 0.7, stagger: 0.1, ease: 'expo.out' },
        heroName ? '-=0.55' : 0,
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

    const sectionReveals = root.querySelectorAll('main.page .reveal');
    sectionReveals.forEach((element: Element) => {
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
    const magneticTargets = root.querySelectorAll('.hero__share .btn-primary, .hero__cta .btn');
    magneticTargets.forEach((target) =>
      this.magneticCleanups.push(createMagneticHover(target as HTMLElement, 0.3)),
    );
  }

  /**
   * Brilho suave que segue o cursor no herói — só aparece no hover, então não
   * compete com o conteúdo em telas touch (que não têm :hover de verdade).
   */
  private setupHeroSpotlight(root: HTMLElement): void {
    const hero = root.querySelector('.hero') as HTMLElement | null;
    if (!hero) {
      return;
    }
    const onMouseMove = (event: MouseEvent) => {
      const rect = hero.getBoundingClientRect();
      hero.style.setProperty('--spot-x', `${((event.clientX - rect.left) / rect.width) * 100}%`);
      hero.style.setProperty('--spot-y', `${((event.clientY - rect.top) / rect.height) * 100}%`);
    };
    hero.addEventListener('mousemove', onMouseMove);
    this.heroSpotlightCleanup = () => hero.removeEventListener('mousemove', onMouseMove);
  }

  /**
   * Destaca no menu de navegação a seção que está visível na tela, com um
   * indicador que desliza até o link ativo — mesma linguagem visual do switch
   * de idioma (um elemento que "desliza" para mostrar o estado atual).
   */
  private setupActiveNav(root: HTMLElement): void {
    const nav = root.querySelector('.anchor-nav');
    const indicator = nav?.querySelector('.anchor-nav__indicator');
    const links: HTMLAnchorElement[] = [];
    nav?.querySelectorAll('a[href^="#"]').forEach((el) => links.push(el as HTMLAnchorElement));
    const sections: HTMLElement[] = [];
    links.forEach((link) => {
      const section = root.querySelector(link.getAttribute('href')!);
      if (section) {
        sections.push(section as HTMLElement);
      }
    });
    if (!nav || !indicator || sections.length === 0) {
      return;
    }

    const moveIndicatorTo = (link: HTMLAnchorElement) => {
      links.forEach((candidate) => candidate.classList.toggle('is-active', candidate === link));
      const targetX = link.offsetLeft;
      const targetWidth = link.getBoundingClientRect().width;
      if (this.reduceMotion) {
        gsap.set(indicator, { opacity: 1, x: targetX, width: targetWidth });
        return;
      }
      gsap.to(indicator, { opacity: 1, x: targetX, width: targetWidth, duration: 0.4, ease: 'power2.out' });
    };

    this.sectionObserver = new IntersectionObserver(
      (entries) => {
        const visible = entries.filter((entry) => entry.isIntersecting);
        if (visible.length === 0) {
          return;
        }
        const topMost = visible.reduce((closest, entry) =>
          entry.boundingClientRect.top < closest.boundingClientRect.top ? entry : closest,
        );
        const link = links.find((candidate) => candidate.getAttribute('href') === `#${topMost.target.id}`);
        if (link) {
          moveIndicatorTo(link);
        }
      },
      { rootMargin: '-20% 0px -70% 0px' },
    );
    sections.forEach((section) => this.sectionObserver!.observe(section));
  }

  private updateMetaTags(data: PublicPortfolio): void {
    const displayName = data.profile.fullName ?? this.route.snapshot.paramMap.get('username')!;
    const dictionary = this.localeService.locale() === 'en' ? en : pt;
    const headlineOrBio = data.profile.headline ?? data.profile.bio;
    const description = headlineOrBio
      ? `${dictionary.publicPortfolio.ogDescriptionPrefix} ${displayName}: ${headlineOrBio}. ${dictionary.publicPortfolio.ogDescriptionSuffix}`
      : `${dictionary.publicPortfolio.ogDescriptionDefault} ${displayName} ${dictionary.publicPortfolio.ogDescriptionDefaultSuffix}`;
    this.title.setTitle(`${displayName} · DevPortfolio`);
    this.meta.updateTag({ name: 'description', content: description });
    this.meta.updateTag({ property: 'og:title', content: displayName });
    this.meta.updateTag({ property: 'og:description', content: description });
    if (data.profile.photoUrl) {
      this.meta.updateTag({ property: 'og:image', content: data.profile.photoUrl });
    }
  }

  protected formatPeriod(startDate: string, endDate: string | null, current: boolean): string {
    const start = this.formatDate(startDate);
    if (current) {
      const dictionary = this.localeService.locale() === 'en' ? en : pt;
      return `${start} — ${dictionary.publicPortfolio.periodCurrent}`;
    }
    return endDate ? `${start} — ${this.formatDate(endDate)}` : start;
  }

  protected formatDate(date: string): string {
    const [year, month] = date.split('-');
    return `${month}/${year}`;
  }

  protected statusLabel(status: ProjectStatus): string {
    return STATUS_LABELS[status];
  }

  protected whatsappContactUrl(data: PublicPortfolio): string | null {
    const digits = data.profile.phone?.replace(/\D/g, '');
    if (!digits) {
      return null;
    }
    const dictionary = this.localeService.locale() === 'en' ? en : pt;
    const message = dictionary.publicPortfolio.contactWhatsappMessage;
    return `https://wa.me/${digits}?text=${encodeURIComponent(message)}`;
  }

  protected contactEmailUrl(email: string): string {
    const dictionary = this.localeService.locale() === 'en' ? en : pt;
    return `mailto:${email}?subject=${encodeURIComponent(dictionary.publicPortfolio.contactEmailSubject)}`;
  }

  protected openLightbox(): void {
    this.lightboxOpen.set(true);
    document.addEventListener('keydown', this.onLightboxKeydown);
    requestAnimationFrame(() => {
      const overlay = this.elementRef.nativeElement.querySelector('.lightbox');
      const image = this.elementRef.nativeElement.querySelector('.lightbox__image');
      if (!overlay || !image) {
        return;
      }
      (overlay as HTMLElement).focus();
      if (this.reduceMotion) {
        gsap.set([overlay, image], { opacity: 1, scale: 1 });
        return;
      }
      gsap.set(overlay, { opacity: 0 });
      gsap.set(image, { opacity: 0, scale: 0.92 });
      gsap
        .timeline()
        .to(overlay, { opacity: 1, duration: 0.25, ease: 'power2.out' })
        .to(image, { opacity: 1, scale: 1, duration: 0.35, ease: 'back.out(1.6)' }, '-=0.15');
    });
  }

  protected closeLightbox(): void {
    this.lightboxOpen.set(false);
    document.removeEventListener('keydown', this.onLightboxKeydown);
  }

  protected onLightboxBackdropClick(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.closeLightbox();
    }
  }

  protected hasAnyContent(data: PublicPortfolio): boolean {
    return (
      data.experiences.length > 0 ||
      data.projects.length > 0 ||
      data.skills.length > 0 ||
      data.educations.length > 0 ||
      data.certifications.length > 0
    );
  }

  protected skillGroups(skills: PublicSkill[]): SkillGroup[] {
    return CATEGORY_ORDER.map((category) => ({
      category,
      label: CATEGORY_LABELS[category],
      skills: skills.filter((skill) => skill.category === category),
    })).filter((group) => group.skills.length > 0);
  }

  protected copyLink(): void {
    navigator.clipboard.writeText(window.location.href).then(() => {
      this.linkCopied.set(true);
      setTimeout(() => this.linkCopied.set(false), 2000);
    });
  }

  private shareText(data: PublicPortfolio): string {
    const dictionary = this.localeService.locale() === 'en' ? en : pt;
    const invite = dictionary.publicPortfolio.shareInvite;
    return data.profile.headline ? `${invite} — ${data.profile.headline}` : invite;
  }

  protected linkedInShareUrl(): string {
    return `https://www.linkedin.com/sharing/share-offsite/?url=${encodeURIComponent(window.location.href)}`;
  }

  protected whatsappShareUrl(data: PublicPortfolio): string {
    const text = `${this.shareText(data)} ${window.location.href}`;
    return `https://api.whatsapp.com/send?text=${encodeURIComponent(text)}`;
  }

  protected facebookShareUrl(): string {
    return `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(window.location.href)}`;
  }

  protected twitterShareUrl(data: PublicPortfolio): string {
    const params = new URLSearchParams({ url: window.location.href, text: this.shareText(data) });
    return `https://twitter.com/intent/tweet?${params.toString()}`;
  }
}
