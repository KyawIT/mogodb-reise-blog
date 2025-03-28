package at.htlleonding.controller;

import at.htlleonding.entity.BlogComment;
import at.htlleonding.entity.BlogEntry;
import at.htlleonding.entity.BlogUser;
import at.htlleonding.entity.DTOs.BlogCommentDTO;
import at.htlleonding.entity.DTOs.BlogEntryDTO;
import at.htlleonding.entity.DTOs.BlogUserDTO;
import at.htlleonding.repository.BlogCategoryRepository;
import at.htlleonding.repository.BlogEntryRepository;
import at.htlleonding.repository.BlogUserRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/api/blogs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlogEntryResource {

    @Inject
    BlogEntryRepository blogEntryRepository;

    @Inject
    BlogUserRepository blogUserRepository;

    @Inject
    BlogCategoryRepository blogCategoryRepository;

    // GET: Alle BlogEntries mit JEWEILS den ersten 3 Kommentaren
    @GET
    public List<BlogEntryDTO> getAllBlogs() {
        List<BlogEntry> all = blogEntryRepository.listAll();
        List<BlogEntryDTO> dtos = new ArrayList<>();
        for (BlogEntry entry : all) {
            dtos.add(toBlogEntryDto(entry, /*limitComments=*/true));
        }
        return dtos;
    }

    //GET: Einen bestimmten BlogEntry ebenfalls mit NUR den ersten 3 Kommentaren
    @GET
    @Path("/{id}")
    public Response getBlog(@PathParam("id") String idHex) {
        ObjectId objectId = toObjectIdOrNull(idHex);
        if (objectId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid ObjectId: " + idHex)
                    .build();
        }
        BlogEntry entry = blogEntryRepository.findById(objectId);
        if (entry == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("BlogEntry not found for id " + idHex)
                    .build();
        }
        BlogEntryDTO dto = toBlogEntryDto(entry, true);
        return Response.ok(dto).build();
    }

    // C) GET: "Show more" => ALLE Kommentare eines Blogs
    @GET
    @Path("/{id}/comments")
    public Response getAllCommentsOfBlog(@PathParam("id") String idHex) {
        ObjectId objectId = toObjectIdOrNull(idHex);
        if (objectId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        BlogEntry entry = blogEntryRepository.findById(objectId);
        if (entry == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<BlogComment> allComments = blogEntryRepository.findAllCommentsByEntry(objectId);

        List<BlogCommentDTO> commentDtos = new ArrayList<>();
        for (BlogComment bc : allComments) {
            commentDtos.add(toCommentDto(bc));
        }

        return Response.ok(commentDtos).build();
    }

    //PATCH: Erhöht impressionCount um +1
    @PATCH
    @Path("/{id}/impressions")
    public Response patchImpressions(@PathParam("id") String idHex) {
        ObjectId objectId = toObjectIdOrNull(idHex);
        if (objectId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        BlogEntry entry = blogEntryRepository.findById(objectId);
        if (entry == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        entry.impressionCount++;
        blogEntryRepository.update(entry);
        return Response.noContent().build();
        // 204: erfolgreich, kein Content
    }

    //PUT: BlogEntry updaten + aktuelles Datum zu editDates hinzufügen
    @PUT
    @Path("/{id}")
    public Response updateBlogEntry(@PathParam("id") String idHex, BlogEntryDTO dto) {
        ObjectId objectId = toObjectIdOrNull(idHex);
        if (objectId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        BlogEntry entry = blogEntryRepository.findById(objectId);
        if (entry == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (dto.title != null && !dto.title.trim().isEmpty()) {
            entry.title = dto.title;
        }

        if (dto.description != null && !dto.description.trim().isEmpty()) {
            entry.description = dto.description;
        }

        if (dto.content != null && !dto.content.isEmpty()) {
            entry.content = dto.content.stream().toList();
        }

        entry.editDates.add(new Date());

        blogEntryRepository.update(entry);
        return Response.ok("Blog updated").build();
    }

    //GET: Einen Kommentar abrufen
    @GET
    @Path("/comments/{commentId}")
    public Response getComment(@PathParam("commentId") String commentIdHex) {
        ObjectId commentId = toObjectIdOrNull(commentIdHex);
        if (commentId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        BlogComment bc = blogEntryRepository.findCommentByIdInEmbedded(commentId);
        if (bc == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        BlogCommentDTO dto = toCommentDto(bc);
        return Response.ok(dto).build();
    }

    //PUT: Kommentartext ändern
    @PUT
    @Path("/comments/{commentId}")
    public Response updateComment(@PathParam("commentId") String commentIdHex, BlogCommentDTO dto) {
        ObjectId commentId = toObjectIdOrNull(commentIdHex);
        if (commentId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<BlogEntry> allEntries = blogEntryRepository.listAll();

        boolean found = false;
        for (BlogEntry entry : allEntries) {
            if (entry.blockComments != null) {
                for (BlogComment comment : entry.blockComments) {
                    if (commentId.equals(comment.id)) {
                        comment.content = dto.content;
                        blogEntryRepository.update(entry);
                        found = true;
                        break;
                    }
                }
            }
            if (found) {
                break;
            }
        }
        if (!found) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Comment not found for id " + commentIdHex)
                    .build();
        }
        return Response.ok("Comment updated").build();
    }

    // POST: Einen neuen Kommentar hinzufügen
    @POST
    @Path("/comments")
    public Response addComment(@QueryParam("entryId") String entryIdHex, BlogCommentDTO dto) {
        ObjectId entryId = toObjectIdOrNull(entryIdHex);
        if (entryId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid entry ID").build();
        }

        BlogEntry entry = blogEntryRepository.findById(entryId);
        if (entry == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Blog entry not found for id " + entryIdHex).build();
        }

        BlogComment newComment = new BlogComment();
        newComment.id = new ObjectId(); // Generate a new ObjectId for the comment
        newComment.content = dto.content;
        newComment.creationDate = new Date();
        newComment.blogEntryId = entry.id;
        List<BlogUser> users = blogUserRepository.find("username = ?1", dto.authorUsername).list();
        if (users.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
        newComment.author = users.get(0);

        if (entry.blockComments == null) {
            entry.blockComments = new ArrayList<>();
        }
        entry.blockComments.add(newComment);

        blogEntryRepository.update(entry);

        return Response.status(Response.Status.CREATED).entity("Comment added").build();
    }

    // POST: Einen neuen Blog Entry erstellen
    @POST
    public Response createBlogEntry(BlogEntryDTO dto, ObjectId userId) {
        if (dto == null || dto.title == null || dto.title.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Title is required").build();
        }

        BlogEntry entry = new BlogEntry();
        entry.title = dto.title;
        entry.author = blogUserRepository.find("id =? 1", userId).stream().toList().getFirst();
        entry.description = dto.description;
        entry.impressionCount = dto.impressionCount;
        entry.commentsAllowed = dto.commentsAllowed;
        entry.editDates = List.of(new Date());
        entry.content = (dto.content != null)
                ? new ArrayList<>(dto.content)
                : new ArrayList<>();
        entry.blockComments = new ArrayList<>();
        entry.category = blogCategoryRepository.find("category =? 1", dto.categoryName).stream().toList().getFirst();
        blogEntryRepository.persist(entry);
        return Response.status(Response.Status.CREATED).entity("Blog entry created").build();
    }

    // POST: Einen Kommentar zu einem Blog hinzufügen jedoch muss commentsAllowed = true sein
    @POST
    @Path("/{id}/comments")
    public Response addCommentToBlog(@PathParam("id") String idHex, BlogCommentDTO commentDto, ObjectId userId) {
        ObjectId objectId = toObjectIdOrNull(idHex);
        if (objectId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid blog ID").build();
        }

        BlogEntry entry = blogEntryRepository.findById(objectId);
        if (entry == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Blog entry not found").build();
        }

        if (!entry.commentsAllowed) {
            return Response.status(Response.Status.FORBIDDEN).entity("Comments are not allowed for this blog").build();
        }

        if (commentDto == null || commentDto.content == null || commentDto.content.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Comment content is required").build();
        }

        BlogComment comment = new BlogComment();
        comment.content = commentDto.content;
        comment.creationDate = new Date();
        comment.author = blogUserRepository.find("id =? 1", userId).stream().toList().getFirst();
        comment.blogEntryId = entry.id;

        if (entry.blockComments == null) {
            entry.blockComments = new ArrayList<>();
        }
        entry.blockComments.add(comment);

        blogEntryRepository.update(entry);
        return Response.status(Response.Status.CREATED).entity("Comment added to blog").build();
    }

    //DELETE: Löscht einen BlogEntry
    @DELETE
    @Path("/{id}")
    public Response deleteBlog(@PathParam("id") String idHex) {
        ObjectId objectId = toObjectIdOrNull(idHex);
        if (objectId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid blog ID: " + idHex)
                    .build();
        }

        BlogEntry entry = blogEntryRepository.findById(objectId);
        if (entry == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("BlogEntry not found for id " + idHex)
                    .build();
        }

        blogEntryRepository.delete(entry);
        return Response.noContent().build();
    }

    //DELETE: Löscht einen Kommentar eines EntryBlogs
    @DELETE
    @Path("/{blogId}/comments/{commentId}")
    public Response deleteComment(
            @PathParam("blogId") String blogIdHex,
            @PathParam("commentId") String commentIdHex
    ) {
        ObjectId blogObjectId = toObjectIdOrNull(blogIdHex);
        if (blogObjectId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid blog ID").build();
        }

        BlogEntry entry = blogEntryRepository.findById(blogObjectId);
        if (entry == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Blog entry not found").build();
        }

        ObjectId commentObjectId = toObjectIdOrNull(commentIdHex);
        if (commentObjectId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid comment ID").build();
        }

        boolean removed = false;
        if (entry.blockComments != null) {
            removed = entry.blockComments.removeIf(c -> commentObjectId.equals(c.id));
        }

        if (!removed) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Comment not found for id " + commentIdHex)
                    .build();
        }

        blogEntryRepository.update(entry);
        return Response.noContent().build();
    }



    private ObjectId toObjectIdOrNull(String idHex) {
        try {
            return new ObjectId(idHex);
        } catch (Exception e) {
            return null;
        }
    }

    private BlogEntryDTO toBlogEntryDto(BlogEntry entry, boolean limitComments) {
        BlogEntryDTO dto = new BlogEntryDTO();
        dto.id = entry.id.toHexString();
        dto.title = entry.title;
        dto.description = entry.description;
        dto.impressionCount = entry.impressionCount;
        dto.categoryName = (entry.category != null) ? entry.category.getCategory() : null;
        dto.commentsAllowed = entry.commentsAllowed;
        dto.creationDate = entry.editDates.getFirst();
        dto.content = new ArrayList<>(entry.content).subList(0,1);

        List<BlogComment> comments;
        if (limitComments) {
            comments = blogEntryRepository.findNewestCommentsByEntry(entry.id);
        } else {
            comments = blogEntryRepository.findAllCommentsByEntry(entry.id);
        }

        List<BlogCommentDTO> cdtos = new ArrayList<>();
        for (BlogComment bc : comments) {
            cdtos.add(toCommentDto(bc));
        }
        dto.comments = cdtos;

        return dto;
    }

    private BlogCommentDTO toCommentDto(BlogComment bc) {
        BlogCommentDTO dto = new BlogCommentDTO();
        dto.id = bc.id.toHexString();
        dto.authorUsername = (bc.author != null) ? bc.author.username : null;
        dto.creationDate = bc.creationDate;
        dto.content = bc.content;
        return dto;
    }
}
