import { Component, signal, Signal } from '@angular/core';
import { LoginService } from '../../login.service';
import { User } from '../../../models/user';
import { UserNew } from '../../../models/user.new';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-new-account',
  imports: [FormsModule],
  templateUrl: './new-account.component.html',
  styleUrl: './new-account.component.scss'
})
export class NewAccountComponent {
  public user = signal<UserNew>({
    username: '',
    password: '',
    email: '',
    firstName: '',
    lastName: ''
  });
  public showPassword = signal<boolean>(false);

  constructor(private loginService:LoginService) {
    
  }

  public async createUser() {
    const user = await this.loginService.createUser(this.user());
    if (user) {
      console.log('User created:', user);
      this.loginService.user = user;
    } else {
      alert('User creation failed');
    }
  }
}
