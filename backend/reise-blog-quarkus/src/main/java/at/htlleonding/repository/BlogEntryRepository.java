package at.htlleonding.repository;

import at.htlleonding.entity.BlogEntry;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BlogEntryRepository implements PanacheMongoRepository<BlogEntry> {
}
