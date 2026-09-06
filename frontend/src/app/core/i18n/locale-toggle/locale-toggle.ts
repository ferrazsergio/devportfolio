import { Component, inject } from '@angular/core';
import { LocaleService } from '../locale.service';
import { TranslatePipe } from '../translate.pipe';

@Component({
  selector: 'app-locale-toggle',
  imports: [TranslatePipe],
  templateUrl: './locale-toggle.html',
  styleUrl: './locale-toggle.css',
})
export class LocaleToggleComponent {
  protected readonly localeService = inject(LocaleService);
}
