import { Component, signal } from '@angular/core';
import { Blog } from '../../../models/blog';
import { BlogsService } from '../../blogs.service';
import { BlogComment } from '../../../models/blog.comment';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-blogs',
  imports: [FormsModule, CommonModule],
  templateUrl: './blogs.component.html',
  styleUrl: './blogs.component.scss'
})
export class BlogsComponent {
  public blogs = signal<Blog[]>([]);
  public shownComments = signal<Blog[]>([]);
  public shownNewCommentFields = signal<{blog:Blog, comment:BlogComment}[]>([]);

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

  public addBlogImpression(blog:Blog) {
    this.blogsService.addBlogImpression(blog).then(success => {
      if (success) {
        this.blogs.set(this.blogs().map(b => {
          if (b.id === blog.id) {
            b.impressionCount++;
          }
          return b;
        }));
        console.log('Blog impression added');
      } else {
        alert('Failed to add blog impression');
      }
    });
  }

  public showNewCommentField(blog:Blog) {
    if (this.blogCommentFieldsShown(blog)) {
      this.shownNewCommentFields.set(this.shownNewCommentFields()
        .filter(c => c.blog.id !== blog.id));
      return;
    }

    this.shownNewCommentFields.set(this.shownNewCommentFields()
      .concat({blog: blog, comment: {authorUsername: '', content: '', creationDate: new Date(), id: ''}}));
  }

  public blogCommentFieldsShown(blog:Blog) {
    return this.shownNewCommentFields().find(c => c.blog.id === blog.id) !== undefined;
  }
  public getCommentField(blog:Blog):BlogComment {
    return this.shownNewCommentFields().find(c => c.blog.id === blog.id)!.comment;
  }

  public async addBlogComment(blog:Blog) {
    const comment:BlogComment = this.shownNewCommentFields()
      .find(c => c.blog.id === blog.id)!.comment;

    this.blogs.set(this.blogs().map(b => {
      if (b.id === blog.id) {
        b.comments.push(comment);
      }
      return b;
    }));

    await this.blogsService.addBlogComment(blog, comment).then(success => {
      if (success) {
        this.blogs.set([]);
        this.shownComments.set([]);
        this.shownNewCommentFields.set([]);

        this.loadBlogs();

        console.log('Blog comment added');
      } else {
        alert('Failed to add blog comment');
      }
    });
  }
}
