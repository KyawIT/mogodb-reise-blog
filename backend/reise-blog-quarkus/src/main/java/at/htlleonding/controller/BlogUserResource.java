package at.htlleonding.controller;

import at.htlleonding.entity.BlogUser;
import at.htlleonding.entity.DTOs.BlogUserDTO;
import at.htlleonding.repository.BlogUserRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Optional;

@Path("/api/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class BlogUserResource {

    @Inject
    BlogUserRepository blogUserRepository;

    // GET all users
    @GET
    public Response getAllUsers() {
        List<BlogUser> users = blogUserRepository.listAll();
        if (users.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No users found")
                    .build();
        }
        return Response.ok(users).build();
    }

    // GET all users where username and password match
    @GET
    @Path("/login")
    public Response getUsersByCredentials(
            @QueryParam("username") String username,
            @QueryParam("password") String password) {
        if (username == null || password == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Username and password are required")
                    .build();
        }

        List<BlogUser> users = blogUserRepository.find("username = ?1 and password = ?2", username, password).list();
        if (users.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No users found with the given credentials")
                    .build();
        }
        return Response.ok(users).build();
    }

    // GET all users sorted ascending by username
    @GET
    @Path("/sorted")
    public Response getAllUsersSortedByUsername() {
        // Use the correct MongoDB sorting syntax
        List<BlogUser> users = blogUserRepository.listAll(Sort.by("username", Sort.Direction.Ascending));
        if (users.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No users found")
                    .build();
        }
        return Response.ok(users).build();
    }

    // GET a single user by ID
    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") ObjectId id) {
        BlogUser user = blogUserRepository.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }
        return Response.ok(user).build();
    }

    // POST (Create) a new user
    @POST
    public Response createUser(BlogUserDTO userDto) {
        if (userDto == null || userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Username is required")
                    .build();
        }

        BlogUser user = new BlogUser();
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword()); // In a real application, hash the password

        blogUserRepository.persist(user);
        return Response.status(Response.Status.CREATED)
                .entity(user)
                .build();
    }

    // PUT (Update) an existing user
    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") ObjectId id, BlogUserDTO userDto) {
        if (userDto == null || userDto.getUsername() == null || userDto.getUsername().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Username is required")
                    .build();
        }

        BlogUser existingUser = blogUserRepository.findById(id);
        if (existingUser == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }

        existingUser.setUsername(userDto.getUsername());
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPassword(userDto.getPassword()); // In a real application, hash the password

        blogUserRepository.update(existingUser);
        return Response.ok(existingUser).build();
    }

    // DELETE a user by ID
    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") ObjectId id) {
        BlogUser user = blogUserRepository.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }

        blogUserRepository.delete(user);
        return Response.noContent().build();
    }


}