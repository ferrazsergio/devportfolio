import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Certification, CertificationRequest } from './certification.model';

@Injectable({ providedIn: 'root' })
export class CertificationApiService {
  private readonly http = inject(HttpClient);

  list(): Observable<Certification[]> {
    return this.http.get<Certification[]>('/api/v1/certifications');
  }

  create(request: CertificationRequest): Observable<Certification> {
    return this.http.post<Certification>('/api/v1/certifications', request);
  }

  update(id: string, request: CertificationRequest): Observable<Certification> {
    return this.http.put<Certification>(`/api/v1/certifications/${id}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`/api/v1/certifications/${id}`);
  }
}
