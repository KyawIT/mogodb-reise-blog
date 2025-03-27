import { BlogComment } from "./blog.comment";

export interface Blog {
    id:string,
    title:string,
    description:string,
    impressionCount:number,
    commentsAllowed:boolean,
    creationDate:Date,
    content:string[],
    categoryName:string,
    comments:BlogComment[]
}
