import { Component, signal } from '@angular/core';
import { Blog } from '../../../models/blog';
import { BlogsService } from '../../blogs.service';
import { BlogComment } from '../../../models/blog.comment';

@Component({
  selector: 'app-blogs',
  imports: [],
  templateUrl: './blogs.component.html',
  styleUrl: './blogs.component.scss'
})
export class BlogsComponent {
  public blogs = signal<Blog[]>([]);
  public shownComments = signal<Blog[]>([]);

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

  public async showMoreComments(blog:Blog) {
    const comments:BlogComment[]|null = await this.blogsService.getAllCommentsOfBlog(blog);

    if (comments === null) {
      alert('Failed to load comments');
      return;
    }
    this.shownComments.set(this.shownComments().concat(blog));
    this.blogs.set(this.blogs().map(b => {
      if (b.id === blog.id) {
        b.comments = comments;
      }
      return b;
    }));
  }
}
