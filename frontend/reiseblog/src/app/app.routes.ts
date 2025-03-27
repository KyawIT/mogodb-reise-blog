import { Routes } from '@angular/router';
import { BlogsComponent } from './blogs/components/blogs/blogs.component';

export const routes: Routes = [
    {path: '', redirectTo: '/blogs', pathMatch: 'full'},
    {path: 'blogs', component: BlogsComponent},
];