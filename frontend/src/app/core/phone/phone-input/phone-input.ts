import { Component, forwardRef, inject, signal } from '@angular/core';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import type { CountryCode } from 'libphonenumber-js';
import { LocaleService } from '../../i18n/locale.service';
import { TranslatePipe } from '../../i18n/translate.pipe';

type PhoneModule = typeof import('libphonenumber-js/min');

const DEFAULT_COUNTRY_BY_LOCALE: Record<string, CountryCode> = { pt: 'BR', en: 'US' };

/**
 * Garante que o valor salvo é sempre um E.164 válido (`+5511912345678`),
 * formato exigido pelo botão de WhatsApp do portfólio público.
 * `libphonenumber-js` é importada sob demanda pra não pesar o bundle
 * principal do site (landing/portfólio público nunca precisam dela).
 */
@Component({
  selector: 'app-phone-input',
  imports: [FormsModule, TranslatePipe],
  templateUrl: './phone-input.html',
  styleUrl: './phone-input.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PhoneInputComponent),
      multi: true,
    },
  ],
})
export class PhoneInputComponent implements ControlValueAccessor {
  protected readonly localeService = inject(LocaleService);

  protected readonly countries = signal<CountryCode[]>([]);
  protected readonly country = signal<CountryCode | ''>('');
  protected readonly nationalNumber = signal('');

  private phoneModule: PhoneModule | null = null;
  private pendingValue: string | null = null;
  private onChange: (value: string) => void = () => undefined;
  private onTouched: () => void = () => undefined;

  constructor() {
    import('libphonenumber-js/min').then((module) => {
      this.phoneModule = module;
      this.countries.set([...module.getCountries()].sort());
      if (this.pendingValue !== null) {
        this.applyValue(this.pendingValue);
      } else if (!this.country()) {
        this.country.set(DEFAULT_COUNTRY_BY_LOCALE[this.localeService.locale()] ?? 'US');
      }
    });
  }

  protected countryName(code: CountryCode | ''): string {
    if (!code) {
      return '';
    }
    try {
      return new Intl.DisplayNames([this.localeService.locale()], { type: 'region' }).of(code) ?? code;
    } catch {
      return code;
    }
  }

  protected callingCode(code: CountryCode | ''): string {
    return this.phoneModule && code ? `+${this.phoneModule.getCountryCallingCode(code)}` : '';
  }

  writeValue(value: string | null): void {
    if (this.phoneModule) {
      this.applyValue(value);
    } else {
      this.pendingValue = value;
    }
  }

  private applyValue(value: string | null): void {
    if (!value || !this.phoneModule) {
      return;
    }
    const parsed = this.phoneModule.parsePhoneNumberFromString(value);
    if (parsed?.country) {
      this.country.set(parsed.country);
      this.nationalNumber.set(parsed.formatNational());
    } else {
      this.nationalNumber.set(value);
    }
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  protected onCountryChange(code: CountryCode): void {
    this.country.set(code);
    this.emit();
  }

  protected onNumberInput(value: string): void {
    const country = this.country();
    if (this.phoneModule && country) {
      this.nationalNumber.set(new this.phoneModule.AsYouType(country).input(value));
    } else {
      this.nationalNumber.set(value);
    }
    this.emit();
  }

  private emit(): void {
    this.onTouched();
    const country = this.country();
    if (!this.phoneModule || !country || !this.nationalNumber()) {
      return;
    }
    const parsed = this.phoneModule.parsePhoneNumberFromString(this.nationalNumber(), country);
    if (parsed) {
      this.onChange(parsed.number);
    }
  }
}
