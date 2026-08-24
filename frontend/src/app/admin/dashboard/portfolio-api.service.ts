import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Portfolio, PortfolioStatus } from './portfolio.model';

@Injectable({ providedIn: 'root' })
export class PortfolioApiService {
  private readonly http = inject(HttpClient);

  get(): Observable<Portfolio> {
    return this.http.get<Portfolio>('/api/v1/portfolio');
  }

  updateStatus(status: PortfolioStatus): Observable<Portfolio> {
    return this.http.patch<Portfolio>('/api/v1/portfolio/status', { status });
  }
}
