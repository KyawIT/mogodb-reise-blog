package at.htlleonding.repository;

import at.htlleonding.entity.BlogCategory;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BlogCategoryRepository implements PanacheMongoRepository<BlogCategory> {
}
