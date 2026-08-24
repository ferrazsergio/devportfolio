import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PublicPortfolioApiService } from './public-portfolio-api.service';
import { PublicPortfolio } from './public-portfolio.model';

@Component({
  selector: 'app-public-portfolio',
  templateUrl: './public-portfolio.html',
  styleUrl: './public-portfolio.css',
})
export class PublicPortfolioComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly api = inject(PublicPortfolioApiService);

  protected readonly portfolio = signal<PublicPortfolio | null>(null);
  protected readonly loading = signal(true);
  protected readonly notFound = signal(false);

  constructor() {
    const username = this.route.snapshot.paramMap.get('username')!;
    this.api.getByUsername(username).subscribe({
      next: (data) => {
        this.portfolio.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.notFound.set(true);
        this.loading.set(false);
      },
    });
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
}
