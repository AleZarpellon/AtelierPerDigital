import { Component, computed, inject, signal } from '@angular/core';
import { HeaderComponent } from './gestione-stipendio/view/header/header.component';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { SpinnerService } from './shared/spinner/spinner.service';
import { ToastModule } from 'primeng/toast';
import { ErrorBoundaryComponent } from './shared/error/error-boundary.component';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    HeaderComponent,
    RouterOutlet,
    ProgressSpinnerModule,
    ToastModule,
    ErrorBoundaryComponent,
  ],
  templateUrl: 'app.html',
})
export class AppComponent {
  spinnerService = inject(SpinnerService);
  private router = inject(Router);
  currentUrl = signal(this.router.url);
  showHeader = computed(() => this.currentUrl().startsWith('/gestione-stipendio'));

  constructor() {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentUrl.set(event.urlAfterRedirects);
      });
  }
}
