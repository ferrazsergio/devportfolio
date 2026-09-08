import { Pipe, PipeTransform, inject } from '@angular/core';
import { Locale, LocaleService } from './locale.service';
import { en } from './translations/en';
import { pt } from './translations/pt';

type Dictionary = typeof pt;

const DICTIONARIES: Record<'pt' | 'en', Dictionary> = { pt, en };

/** Lookup síncrono pro TS ler uma tradução fora do template, sem passar pelo pipe. */
export function translateKey(locale: Locale, key: string): string {
  const dictionary = DICTIONARIES[locale];
  const value = key.split('.').reduce<unknown>((node, segment) => {
    return node && typeof node === 'object' ? (node as Record<string, unknown>)[segment] : undefined;
  }, dictionary);
  return typeof value === 'string' ? value : key;
}

/**
 * `pure: false` de propósito — precisa reavaliar quando o signal do LocaleService
 * muda, e um pipe puro do Angular só reavalia quando a própria chave (input) muda.
 */
@Pipe({ name: 'translate', pure: false })
export class TranslatePipe implements PipeTransform {
  private readonly localeService = inject(LocaleService);

  transform(key: string): string {
    return translateKey(this.localeService.locale(), key);
  }
}
