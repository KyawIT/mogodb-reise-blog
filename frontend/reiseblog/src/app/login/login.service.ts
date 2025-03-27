import { Injectable } from '@angular/core';
import { User } from '../models/user';
import { BackendService } from '../backend.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  public user:User|null = null;

  constructor(private backendService:BackendService) { }

  public async createUser(user:User):Promise<User|null> {
    const response = await this.backendService.httpPostRequest('/user', user);
    if (response) {
      this.user = response;
      return response;
    }
    return null;
  }
}
