import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.checked()) {
    return auth.isAuthenticated() || router.createUrlTree(['/login']);
  }

  return auth.fetchCurrentUser().pipe(map(() => auth.isAuthenticated() || router.createUrlTree(['/login'])));
};
