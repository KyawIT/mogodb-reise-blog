import { Injectable } from '@angular/core';
import { User } from '../models/user';
import { BackendService } from '../backend.service';
import { UserNew } from '../models/user.new';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  public user:User|null = null;

  constructor(private backendService:BackendService) { }

  public async createUser(user:UserNew):Promise<User|null> {
    const response = await this.backendService.httpPostRequest('/user', user);
    if (response) {
      this.user = response;
      return response;
    }
    return null;
  }

  public async getUsers():Promise<User[]> {
    const response = await this.backendService.httpGetRequest('/user');
    if (response) {
      return response;
    }
    return [];
  }

  public async getUserFromLogin(username:string, password:string):Promise<User|null> {
    const response = await this.backendService.httpGetRequest('/user/login?password=' + password + '&username=' + username);
    if (response) {
      this.user = response;
      return response;
    }
    return null;
  }
}
