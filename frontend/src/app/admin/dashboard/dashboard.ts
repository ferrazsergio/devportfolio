import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ProfileApiService } from '../profile/profile-api.service';
import { PortfolioApiService } from './portfolio-api.service';
import { Portfolio } from './portfolio.model';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class DashboardComponent {
  private readonly portfolioApi = inject(PortfolioApiService);
  private readonly profileApi = inject(ProfileApiService);

  protected readonly loading = signal(true);
  protected readonly portfolio = signal<Portfolio | null>(null);
  protected readonly username = signal<string | null>(null);
  protected readonly updating = signal(false);

  constructor() {
    this.portfolioApi.get().subscribe((portfolio) => {
      this.portfolio.set(portfolio);
      this.loading.set(false);
    });
    this.profileApi.get().subscribe((profile) => this.username.set(profile.username));
  }

  protected togglePublished(): void {
    const current = this.portfolio();
    if (!current) {
      return;
    }
    const nextStatus = current.status === 'PUBLISHED' ? 'DRAFT' : 'PUBLISHED';
    this.updating.set(true);
    this.portfolioApi.updateStatus(nextStatus).subscribe((updated) => {
      this.portfolio.set(updated);
      this.updating.set(false);
    });
  }
}
