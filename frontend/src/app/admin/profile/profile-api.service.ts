import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Profile, UpdateProfileRequest } from './profile.model';

@Injectable({ providedIn: 'root' })
export class ProfileApiService {
  private readonly http = inject(HttpClient);

  get(): Observable<Profile> {
    return this.http.get<Profile>('/api/v1/profile');
  }

  update(request: UpdateProfileRequest): Observable<Profile> {
    return this.http.put<Profile>('/api/v1/profile', request);
  }
}
