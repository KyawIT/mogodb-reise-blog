import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoginComponent } from './login/component/login.component';
import { LoginService } from './login/login.service';

@Component({
    selector: 'app-root',
    imports: [RouterOutlet, LoginComponent],
    standalone: true,
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss'
})
export class AppComponent {
  constructor(public loginService:LoginService) { }
  
}
