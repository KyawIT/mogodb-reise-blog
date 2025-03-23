package at.htlleonding.controller;

import at.htlleonding.entity.BlogCategory;
import at.htlleonding.repository.BlogCategoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.List;

@Path("/api/category")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class BlogCategoryResource {

    @Inject
    BlogCategoryRepository blogCategoryRepository;

    // GET all categories
    @GET
    public Response getAllCategories() {
        List<BlogCategory> categories = blogCategoryRepository.listAll();
        if (categories.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(categories).build();
    }

    // GET a single category by ID
    @GET
    @Path("/{id}")
    public Response getCategoryById(@PathParam("id") ObjectId id) {
        BlogCategory category = blogCategoryRepository.findById(id);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(category).build();
    }

    // POST (Create) a new category
    @POST
    public Response createCategory(BlogCategory category) {
        if (category == null || category.getCategory() == null || category.getCategory().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Category name is required").build();
        }
        blogCategoryRepository.persist(category);
        return Response.status(Response.Status.CREATED).entity(category).build();
    }

    // PUT (Update) an existing category
    @PUT
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") ObjectId id, BlogCategory updatedCategory) {
        BlogCategory existingCategory = blogCategoryRepository.findById(id);
        if (existingCategory == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (updatedCategory.getCategory() != null && !updatedCategory.getCategory().isEmpty()) {
            existingCategory.setCategory(updatedCategory.getCategory());
        }
        blogCategoryRepository.update(existingCategory);
        return Response.ok(existingCategory).build();
    }

    // DELETE a category by ID
    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") ObjectId id) {
        BlogCategory category = blogCategoryRepository.findById(id);
        if (category == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        blogCategoryRepository.delete(category);
        return Response.noContent().build();
    }
}