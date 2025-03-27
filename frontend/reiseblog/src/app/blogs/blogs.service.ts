import { Injectable } from '@angular/core';
import { BackendService } from '../backend.service';
import { Blog } from '../models/blog';
import { BlogComment } from '../models/blog.comment';

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

  public async getAllCommentsOfBlog(blog:Blog):Promise<BlogComment[]|null> {  
    const response = await this.backendService.httpGetRequest('/blogs/' + blog.id + '/comments');
    if (response) {
      return response;
    }
    return null;
  }

  public async addBlogImpression(blog:Blog):Promise<boolean> {
    const response = await this.backendService.httpPatchRequest('/blogs/' + blog.id + '/impressions', blog);
    if (!response) {
      return true;
    }
    return false;
  }
}
