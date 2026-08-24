import { Routes } from '@angular/router';
import { PublicPortfolioComponent } from './public-portfolio/public-portfolio';

export const routes: Routes = [
  // Rotas fixas (login, admin) precisam ser cadastradas ANTES de ':username' nas
  // próximas fases, para não colidirem com o segmento coringa da página pública.
  { path: ':username', component: PublicPortfolioComponent },
];
