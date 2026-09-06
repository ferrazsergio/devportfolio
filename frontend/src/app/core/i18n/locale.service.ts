import { Injectable, signal } from '@angular/core';

export type Locale = 'pt' | 'en';

const STORAGE_KEY = 'devportfolio:locale';

@Injectable({ providedIn: 'root' })
export class LocaleService {
  readonly locale = signal<Locale>(this.readInitialLocale());

  toggle(): void {
    this.setLocale(this.locale() === 'pt' ? 'en' : 'pt');
  }

  setLocale(locale: Locale): void {
    this.locale.set(locale);
    document.documentElement.lang = locale === 'en' ? 'en' : 'pt-BR';
    try {
      localStorage.setItem(STORAGE_KEY, locale);
    } catch {
      // localStorage indisponível — idioma aplicado só nesta sessão
    }
  }

  private readInitialLocale(): Locale {
    return document.documentElement.lang.startsWith('en') ? 'en' : 'pt';
  }
}
