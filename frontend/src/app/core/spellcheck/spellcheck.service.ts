import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import Typo from 'typo-js';
import { Observable, forkJoin, map, shareReplay } from 'rxjs';
import { Locale } from '../i18n/locale.service';

const DICTIONARY_CODE: Record<Locale, string> = { pt: 'pt_BR', en: 'en_US' };

/**
 * Carrega os dicionários Hunspell (pt_BR/en_US) sob demanda — só quando um campo
 * com correção ortográfica é realmente usado — e mantém uma instância por idioma
 * em cache (o `.dic` do pt_BR sozinho tem ~4,5MB, não faz sentido buscar de novo
 * nem carregar os dois de uma vez sem necessidade).
 */
@Injectable({ providedIn: 'root' })
export class SpellcheckService {
  private readonly http = inject(HttpClient);
  private readonly checkers = new Map<Locale, Observable<Typo>>();

  getChecker(locale: Locale): Observable<Typo> {
    let checker$ = this.checkers.get(locale);
    if (!checker$) {
      const code = DICTIONARY_CODE[locale];
      checker$ = forkJoin({
        affData: this.http.get(`/dictionaries/${code}.aff`, { responseType: 'text' }),
        wordsData: this.http.get(`/dictionaries/${code}.dic`, { responseType: 'text' }),
      }).pipe(
        map(({ affData, wordsData }) => new Typo(code, affData, wordsData)),
        shareReplay(1),
      );
      this.checkers.set(locale, checker$);
    }
    return checker$;
  }
}
