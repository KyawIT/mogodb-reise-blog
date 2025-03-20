package at.htlleonding.controller;

import at.htlleonding.entity.BlogComment;
import at.htlleonding.entity.BlogEntry;
import at.htlleonding.entity.DTOs.BlogCommentDTO;
import at.htlleonding.entity.DTOs.BlogEntryDTO;
import at.htlleonding.repository.BlogCommentRepository;
import at.htlleonding.repository.BlogEntryRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/blogs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlogEntryResource {

    @Inject
    BlogEntryRepository blogEntryRepository;

    @Inject
    BlogCommentRepository blogCommentRepository;

    // ------------------------------------------------
    // A) GET /blogs : Alle BlogEntries mit JEWEILS den ersten 3 Kommentaren
    // ------------------------------------------------
    @GET
    public List<BlogEntryDTO> getAllBlogs() {
        List<BlogEntry> all = blogEntryRepository.listAll();
        List<BlogEntryDTO> dtos = new ArrayList<>();
        for (BlogEntry entry : all) {
            dtos.add(toBlogEntryDto(entry, /*limitComments=*/true));
        }
        return dtos;
    }

    // ------------------------------------------------
    // B) GET /blogs/{id} : Einen bestimmten BlogEntry
    //    ebenfalls mit NUR den ersten 3 Kommentaren
    // ------------------------------------------------
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

    // ------------------------------------------------
    // C) GET /blogs/{id}/comments : "Show more" => ALLE Kommentare
    // ------------------------------------------------
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

        List<BlogComment> allComments = blogCommentRepository.findAllCommentsByEntry(objectId);

        List<BlogCommentDTO> commentDtos = new ArrayList<>();
        for (BlogComment bc : allComments) {
            commentDtos.add(toCommentDto(bc));
        }

        return Response.ok(commentDtos).build();
    }

    // ------------------------------------------------
    // D) PATCH /blogs/{id}/impressions : Erhöht impressionCount um +1
    // ------------------------------------------------
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

    // ------------------------------------------------
    // E) PUT /blogs/{id} : BlogEntry updaten + aktuelles Datum zu editDates hinzufügen
    // ------------------------------------------------
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

        entry.title = dto.title;
        entry.description = dto.description;
        entry.impressionCount = dto.impressionCount;

        if (entry.editDates == null) {
            entry.editDates = new ArrayList<>();
        }
        entry.editDates.add(new Date());

        blogEntryRepository.update(entry);
        return Response.ok("Blog updated").build();
    }

    // ------------------------------------------------
    // F) GET /comments/{commentId} : Einen Kommentar abrufen
    // ------------------------------------------------
    @GET
    @Path("/comments/{commentId}")
    public Response getComment(@PathParam("commentId") String commentIdHex) {
        ObjectId commentId = toObjectIdOrNull(commentIdHex);
        if (commentId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        BlogComment bc = blogCommentRepository.findById(commentId);
        if (bc == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        BlogCommentDTO dto = toCommentDto(bc);
        return Response.ok(dto).build();
    }

    // ------------------------------------------------
    // G) PUT /comments/{commentId} : Kommentartext ändern
    // ------------------------------------------------
    @PUT
    @Path("/comments/{commentId}")
    public Response updateComment(@PathParam("commentId") String commentIdHex, BlogCommentDTO dto) {
        ObjectId commentId = toObjectIdOrNull(commentIdHex);
        if (commentId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        BlogComment comment = blogCommentRepository.findById(commentId);
        if (comment == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        comment.content = dto.content;
        blogCommentRepository.update(comment);

        return Response.ok("Comment updated").build();
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
        dto.categoryName = (entry.category != null) ? entry.category.category : null;

        List<BlogComment> comments;
        if (limitComments) {
            comments = blogCommentRepository.findNewestCommentsByEntry(entry.id);
        } else {
            comments = blogCommentRepository.findAllCommentsByEntry(entry.id);
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
