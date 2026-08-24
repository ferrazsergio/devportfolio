import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { SocialLink, SocialLinkRequest } from './social-link.model';

@Injectable({ providedIn: 'root' })
export class SocialLinkApiService {
  private readonly http = inject(HttpClient);

  list(): Observable<SocialLink[]> {
    return this.http.get<SocialLink[]>('/api/v1/social-links');
  }

  create(request: SocialLinkRequest): Observable<SocialLink> {
    return this.http.post<SocialLink>('/api/v1/social-links', request);
  }

  update(id: string, request: SocialLinkRequest): Observable<SocialLink> {
    return this.http.put<SocialLink>(`/api/v1/social-links/${id}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`/api/v1/social-links/${id}`);
  }
}
