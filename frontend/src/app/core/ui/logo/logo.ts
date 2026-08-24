import { Component, input } from '@angular/core';

@Component({
  selector: 'app-logo',
  templateUrl: './logo.html',
  styleUrl: './logo.css',
})
export class LogoComponent {
  readonly showWordmark = input(true);
}
