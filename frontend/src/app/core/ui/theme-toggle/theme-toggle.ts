import { Component, inject } from '@angular/core';
import { ThemeService } from '../../theme/theme.service';

@Component({
  selector: 'app-theme-toggle',
  imports: [],
  templateUrl: './theme-toggle.html',
  styleUrl: './theme-toggle.css',
})
export class ThemeToggleComponent {
  protected readonly themeService = inject(ThemeService);
}
