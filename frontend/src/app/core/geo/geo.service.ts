import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';

export interface CountryGeoData {
  states: Record<string, { id: string; name: string }[]>;
}

/** Busca o arquivo de um país só quando ele é selecionado — o dataset completo passa de 3MB. */
@Injectable({ providedIn: 'root' })
export class GeoService {
  private readonly http = inject(HttpClient);
  private countryCodes$: Observable<string[]> | null = null;
  private readonly countryData = new Map<string, Observable<CountryGeoData>>();

  getCountryCodes(): Observable<string[]> {
    if (!this.countryCodes$) {
      this.countryCodes$ = this.http.get<string[]>('/geo/countries.json').pipe(shareReplay(1));
    }
    return this.countryCodes$;
  }

  getCountryData(code: string): Observable<CountryGeoData> {
    let data$ = this.countryData.get(code);
    if (!data$) {
      data$ = this.http.get<CountryGeoData>(`/geo/by-country/${code}.json`).pipe(shareReplay(1));
      this.countryData.set(code, data$);
    }
    return data$;
  }
}
