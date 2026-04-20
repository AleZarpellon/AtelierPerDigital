import { Component } from '@angular/core';
import { Router } from '@angular/router';

/**
 * Componente home del nuovo modulo — rimpiazza con la tua schermata principale.
 */
@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
  imports: [],
})
export class HomeComponent {
  constructor(private router: Router) {}

  goToLauncher() {
    this.router.navigateByUrl('/launcher');
  }
}
