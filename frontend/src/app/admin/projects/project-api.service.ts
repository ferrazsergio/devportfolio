import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Project, ProjectRequest } from './project.model';

@Injectable({ providedIn: 'root' })
export class ProjectApiService {
  private readonly http = inject(HttpClient);

  list(): Observable<Project[]> {
    return this.http.get<Project[]>('/api/v1/projects');
  }

  create(request: ProjectRequest): Observable<Project> {
    return this.http.post<Project>('/api/v1/projects', request);
  }

  update(id: string, request: ProjectRequest): Observable<Project> {
    return this.http.put<Project>(`/api/v1/projects/${id}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`/api/v1/projects/${id}`);
  }

  reorder(orderedIds: string[]): Observable<void> {
    return this.http.patch<void>('/api/v1/projects/reorder', { orderedIds });
  }
}
