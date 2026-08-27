import { Component, ElementRef, OnDestroy, inject, signal } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { LogoComponent } from '../core/ui/logo/logo';
import { PublicPortfolioApiService } from './public-portfolio-api.service';
import { ProjectStatus, PublicPortfolio, PublicSkill, SkillCategory } from './public-portfolio.model';

gsap.registerPlugin(ScrollTrigger);

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
  imports: [LogoComponent],
  templateUrl: './public-portfolio.html',
  styleUrl: './public-portfolio.css',
})
export class PublicPortfolioComponent implements OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly api = inject(PublicPortfolioApiService);
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);
  private readonly elementRef = inject(ElementRef<HTMLElement>);

  private readonly scrollTriggers: ScrollTrigger[] = [];
  private readonly reduceMotion =
    typeof window !== 'undefined' && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  protected readonly portfolio = signal<PublicPortfolio | null>(null);
  protected readonly loading = signal(true);
  protected readonly notFound = signal(false);
  protected readonly linkCopied = signal(false);

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
  }

  /**
   * Animações de entrada (hero) e de rolagem (seções/cards). Se o usuário
   * pediu menos movimento (prefers-reduced-motion), pulamos tudo — o conteúdo
   * já é totalmente visível por padrão via CSS, então nada quebra.
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

    const sectionReveals = root.querySelectorAll('main.page .reveal');
    sectionReveals.forEach((element: Element) => {
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

  private updateMetaTags(data: PublicPortfolio): void {
    const displayName = data.profile.fullName ?? this.route.snapshot.paramMap.get('username')!;
    const description = data.profile.headline ?? data.profile.bio ?? 'Portfólio de desenvolvedor(a) no DevPortfolio.';
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
      return `${start} — atual`;
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
    const name = data.profile.fullName ?? '';
    return data.profile.headline ? `${name} · ${data.profile.headline}` : name;
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
