import { Component, signal } from '@angular/core';
import { Blog } from '../../../models/blog';
import { BlogsService } from '../../blogs.service';

@Component({
  selector: 'app-blogs',
  imports: [],
  templateUrl: './blogs.component.html',
  styleUrl: './blogs.component.scss'
})
export class BlogsComponent {
  public blogs = signal<Blog[]>([]);

  constructor(private blogsService:BlogsService) {
    this.loadBlogs();
  }

  public async loadBlogs() {
    await this.blogsService.getBlogs().then(blogs => {
      if (blogs) {
        this.blogs.set(blogs);
        console.log('Blogs loaded:', blogs);
      } else {
        alert('Failed to load blogs');
      }
    });
  }
}
