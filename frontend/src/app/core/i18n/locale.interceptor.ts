import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { LocaleService } from './locale.service';

/** Anexa Accept-Language em toda request, pra mensagens de validação/erro do backend baterem com o idioma da UI. */
export const localeInterceptor: HttpInterceptorFn = (req, next) => {
  const locale = inject(LocaleService).locale();
  return next(req.clone({ setHeaders: { 'Accept-Language': locale } }));
};
