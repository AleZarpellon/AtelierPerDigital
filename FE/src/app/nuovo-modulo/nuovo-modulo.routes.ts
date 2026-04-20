/**
 * Template per un nuovo modulo.
 * Copia questa cartella, rinominala e registra la rotta in app.routes.ts:
 *
 *   {
 *     path: 'nome-modulo',
 *     loadChildren: () =>
 *       import('./nome-modulo/nome-modulo.routes').then((m) => m.routes),
 *   }
 *
 * Poi aggiungi la card corrispondente in launcher.component.ts.
 */
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./view/home/home.component').then((m) => m.HomeComponent),
  },
];
