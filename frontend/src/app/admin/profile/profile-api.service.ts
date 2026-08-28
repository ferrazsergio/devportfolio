import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { Profile, UpdateProfileRequest } from './profile.model';

@Injectable({ providedIn: 'root' })
export class ProfileApiService {
  private readonly http = inject(HttpClient);

  /** Cache compartilhado — permite que o menu de usuário reflita a foto sem reload. */
  readonly profile = signal<Profile | null>(null);

  get(): Observable<Profile> {
    return this.http.get<Profile>('/api/v1/profile').pipe(tap((profile) => this.profile.set(profile)));
  }

  update(request: UpdateProfileRequest): Observable<Profile> {
    return this.http
      .put<Profile>('/api/v1/profile', request)
      .pipe(tap((profile) => this.profile.set(profile)));
  }

  uploadPhoto(file: File): Observable<Profile> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http
      .post<Profile>('/api/v1/profile/photo', formData)
      .pipe(tap((profile) => this.profile.set(profile)));
  }

  removePhoto(): Observable<Profile> {
    return this.http
      .delete<Profile>('/api/v1/profile/photo')
      .pipe(tap((profile) => this.profile.set(profile)));
  }
}
