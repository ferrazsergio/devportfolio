import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, catchError, of, tap } from 'rxjs';
import { User } from './user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly currentUserSignal = signal<User | null>(null);
  private readonly checkedSignal = signal(false);

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly checked = this.checkedSignal.asReadonly();
  readonly isAuthenticated = computed(() => this.currentUserSignal() !== null);

  login(email: string, password: string): Observable<User> {
    return this.http.post<User>('/api/v1/auth/login', { email, password }).pipe(
      tap((user) => {
        this.currentUserSignal.set(user);
        this.checkedSignal.set(true);
      }),
    );
  }

  register(name: string, email: string, password: string): Observable<User> {
    return this.http.post<User>('/api/v1/auth/register', { name, email, password });
  }

  logout(): Observable<void> {
    return this.http.post<void>('/api/v1/auth/logout', {}).pipe(
      tap(() => this.currentUserSignal.set(null)),
    );
  }

  /** Chamado pelo guard uma vez por sessão do navegador para saber se já existe cookie de sessão válido. */
  fetchCurrentUser(): Observable<User | null> {
    return this.http.get<User>('/api/v1/auth/me').pipe(
      tap((user) => {
        this.currentUserSignal.set(user);
        this.checkedSignal.set(true);
      }),
      catchError(() => {
        this.currentUserSignal.set(null);
        this.checkedSignal.set(true);
        return of(null);
      }),
    );
  }
}
