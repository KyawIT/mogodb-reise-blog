package at.htlleonding.repository;

import at.htlleonding.entity.BlogComment;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BlogCommentRepository implements PanacheMongoRepository<BlogComment> {
}
