package at.htlleonding.controller;

import at.htlleonding.entity.BlogCategory;
import at.htlleonding.repository.BlogCategoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/category")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BlogCategoryResource {
    @Inject
    BlogCategoryRepository blogCategoryRepository;

    @GET
    public Response getAllCategories() {
        List<BlogCategory> categories = blogCategoryRepository.listAll();
        if (categories.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(categories).build();
    }
}
