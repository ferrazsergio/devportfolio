import { Component, forwardRef, inject, signal } from '@angular/core';
import { FormsModule, NG_VALUE_ACCESSOR, ControlValueAccessor } from '@angular/forms';
import { LocaleService } from '../../i18n/locale.service';
import { TranslatePipe } from '../../i18n/translate.pipe';
import { GeoService } from '../geo.service';

let nextInstanceId = 0;

/**
 * Um valor já salvo (string livre antiga) só aparece como dica — nunca
 * tentamos adivinhar o país/cidade a partir dele, pra não mostrar uma
 * seleção errada com confiança falsa.
 */
@Component({
  selector: 'app-location-input',
  imports: [FormsModule, TranslatePipe],
  templateUrl: './location-input.html',
  styleUrl: './location-input.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => LocationInputComponent),
      multi: true,
    },
  ],
})
export class LocationInputComponent implements ControlValueAccessor {
  private readonly geoService = inject(GeoService);
  protected readonly localeService = inject(LocaleService);
  protected readonly instanceId = `location-city-suggestions-${nextInstanceId++}`;

  protected readonly countryCodes = signal<string[]>([]);
  protected readonly states = signal<string[]>([]);
  protected readonly cities = signal<string[]>([]);
  protected readonly selectedCountry = signal('');
  protected readonly selectedState = signal('');
  protected readonly cityValue = signal('');
  protected readonly rawValue = signal<string | null>(null);

  private statesData: Record<string, { id: string; name: string }[]> | null = null;
  private onChange: (value: string) => void = () => undefined;
  private onTouched: () => void = () => undefined;

  constructor() {
    this.geoService.getCountryCodes().subscribe((codes) => this.countryCodes.set(codes));
  }

  protected countryName(code: string): string {
    if (!code) {
      return '';
    }
    try {
      return new Intl.DisplayNames([this.localeService.locale()], { type: 'region' }).of(code) ?? code;
    } catch {
      return code;
    }
  }

  writeValue(value: string | null): void {
    this.rawValue.set(value ?? null);
    this.selectedCountry.set('');
    this.selectedState.set('');
    this.cityValue.set('');
    this.states.set([]);
    this.cities.set([]);
    this.statesData = null;
  }

  registerOnChange(fn: (value: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  protected onCountryChange(code: string): void {
    this.selectedCountry.set(code);
    this.selectedState.set('');
    this.cityValue.set('');
    this.cities.set([]);
    this.states.set([]);
    this.statesData = null;
    if (code) {
      this.geoService.getCountryData(code).subscribe((data) => {
        this.statesData = data.states;
        this.states.set(Object.keys(data.states).sort());
      });
    }
    this.emit();
  }

  protected onStateChange(state: string): void {
    this.selectedState.set(state);
    this.cityValue.set('');
    const cities = this.statesData?.[state] ?? [];
    this.cities.set([...cities].map((city) => city.name).sort());
    this.emit();
  }

  protected onCityInput(value: string): void {
    this.cityValue.set(value);
    this.emit();
  }

  private emit(): void {
    this.onTouched();
    const parts = [this.cityValue(), this.selectedState(), this.countryName(this.selectedCountry())].filter(
      (part) => part.length > 0,
    );
    if (parts.length > 0) {
      this.onChange(parts.join(', '));
    }
  }
}
