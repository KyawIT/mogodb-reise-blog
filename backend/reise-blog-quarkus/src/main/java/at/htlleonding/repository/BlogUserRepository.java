package at.htlleonding.repository;

import at.htlleonding.entity.BlogUser;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BlogUserRepository implements PanacheMongoRepository<BlogUser> {
}
