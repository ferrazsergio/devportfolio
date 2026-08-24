import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Experience, ExperienceRequest } from './experience.model';

@Injectable({ providedIn: 'root' })
export class ExperienceApiService {
  private readonly http = inject(HttpClient);

  list(): Observable<Experience[]> {
    return this.http.get<Experience[]>('/api/v1/experiences');
  }

  create(request: ExperienceRequest): Observable<Experience> {
    return this.http.post<Experience>('/api/v1/experiences', request);
  }

  update(id: string, request: ExperienceRequest): Observable<Experience> {
    return this.http.put<Experience>(`/api/v1/experiences/${id}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`/api/v1/experiences/${id}`);
  }

  reorder(orderedIds: string[]): Observable<void> {
    return this.http.patch<void>('/api/v1/experiences/reorder', { orderedIds });
  }
}
