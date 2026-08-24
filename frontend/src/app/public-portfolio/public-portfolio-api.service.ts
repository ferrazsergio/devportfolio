import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { PublicPortfolio } from './public-portfolio.model';

@Injectable({ providedIn: 'root' })
export class PublicPortfolioApiService {
  private readonly http = inject(HttpClient);

  getByUsername(username: string): Observable<PublicPortfolio> {
    return this.http.get<PublicPortfolio>(`/api/v1/public/${username}`);
  }
}
