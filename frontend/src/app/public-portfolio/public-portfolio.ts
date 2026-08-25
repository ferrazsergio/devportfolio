import { Component, inject, signal } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { LogoComponent } from '../core/ui/logo/logo';
import { PublicPortfolioApiService } from './public-portfolio-api.service';
import { ProjectStatus, PublicPortfolio, PublicSkill, SkillCategory } from './public-portfolio.model';

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
export class PublicPortfolioComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly api = inject(PublicPortfolioApiService);
  private readonly title = inject(Title);
  private readonly meta = inject(Meta);

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
      },
      error: () => {
        this.notFound.set(true);
        this.loading.set(false);
        this.title.setTitle('Portfólio não encontrado · DevPortfolio');
      },
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
}
