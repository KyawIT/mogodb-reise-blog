import { Injectable } from '@angular/core';
import { BackendService } from '../backend.service';
import { Blog } from '../models/blog';

@Injectable({
  providedIn: 'root'
})
export class BlogsService {

  constructor(private backendService:BackendService) { }

  public async getBlogs():Promise<Blog[]|null> {
    const response = await this.backendService.httpGetRequest('/blogs');
    if (response) {
      return response;
    }
    return null;
  }
}
