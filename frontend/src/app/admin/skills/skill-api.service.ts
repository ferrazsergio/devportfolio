import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Skill, SkillCategory } from './skill.model';

export interface SkillRequest {
  name: string;
  category: SkillCategory;
}

@Injectable({ providedIn: 'root' })
export class SkillApiService {
  private readonly http = inject(HttpClient);

  list(): Observable<Skill[]> {
    return this.http.get<Skill[]>('/api/v1/skills');
  }

  create(request: SkillRequest): Observable<Skill> {
    return this.http.post<Skill>('/api/v1/skills', request);
  }

  update(id: string, request: SkillRequest): Observable<Skill> {
    return this.http.put<Skill>(`/api/v1/skills/${id}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`/api/v1/skills/${id}`);
  }
}
