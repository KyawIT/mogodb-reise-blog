package at.htlleonding.repository;

import at.htlleonding.entity.BlogUser;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class BlogUserRepository implements PanacheMongoRepository<BlogUser> {
    // Create a unique index on the `username` field
    void onStart(@Observes StartupEvent ev) {
        mongoCollection().createIndex(
                Indexes.ascending("username"), // Create an index on the `username` field
                new IndexOptions().unique(true) // Make the index unique
        );
        System.out.println("Unique index on 'username' created successfully.");
    }
}
