import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GithubImportResult, GithubRepo, GithubStatus } from './github.model';

@Injectable({ providedIn: 'root' })
export class GithubApiService {
  private readonly http = inject(HttpClient);

  status(): Observable<GithubStatus> {
    return this.http.get<GithubStatus>('/api/v1/github/status');
  }

  repositories(): Observable<GithubRepo[]> {
    return this.http.get<GithubRepo[]>('/api/v1/github/repositories');
  }

  importRepositories(fullNames: string[]): Observable<GithubImportResult> {
    return this.http.post<GithubImportResult>('/api/v1/github/import', { fullNames });
  }

  disconnect(): Observable<void> {
    return this.http.delete<void>('/api/v1/github/connection');
  }

  connectUrl(): string {
    return '/api/v1/github/connect';
  }
}
