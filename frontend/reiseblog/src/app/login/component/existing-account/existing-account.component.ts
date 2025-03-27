import { Component, signal } from '@angular/core';
import { User } from '../../../models/user';
import { LoginService } from '../../login.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-existing-account',
  imports: [FormsModule],
  templateUrl: './existing-account.component.html',
  styleUrl: './existing-account.component.scss'
})
export class ExistingAccountComponent {
  users = signal<User[]>([]);
  username = signal<string>('');
  password = signal<string>('');

  constructor(private loginService:LoginService) {
    this.loadUsers();
  }

  public async loadUsers() {
    this.users.set(await this.loginService.getUsers());
  }

  public login() {
    this.loginService.getUserFromLogin(this.username(), this.password()).then(user => {
      if (user) {
        const userAsUser:User = (user as any)[0];
        console.log('User logged in:', userAsUser);
        this.loginService.user = userAsUser;
      } else {
        alert('Login failed');
      }
    });
  }
}
