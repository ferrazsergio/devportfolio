import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Education, EducationRequest } from './education.model';

@Injectable({ providedIn: 'root' })
export class EducationApiService {
  private readonly http = inject(HttpClient);

  list(): Observable<Education[]> {
    return this.http.get<Education[]>('/api/v1/educations');
  }

  create(request: EducationRequest): Observable<Education> {
    return this.http.post<Education>('/api/v1/educations', request);
  }

  update(id: string, request: EducationRequest): Observable<Education> {
    return this.http.put<Education>(`/api/v1/educations/${id}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`/api/v1/educations/${id}`);
  }
}
