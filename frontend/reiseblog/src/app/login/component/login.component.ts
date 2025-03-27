import { Component, signal } from '@angular/core';
import { NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { NewAccountComponent } from "./new-account/new-account.component";
import { ExistingAccountComponent } from './existing-account/existing-account.component';

@Component({
  selector: 'app-login',
  imports: [NgbCollapseModule, NewAccountComponent, ExistingAccountComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  public isSignupCollapsed = signal<boolean>(true);
  public isLoginCollapsed = signal<boolean>(true);
}
