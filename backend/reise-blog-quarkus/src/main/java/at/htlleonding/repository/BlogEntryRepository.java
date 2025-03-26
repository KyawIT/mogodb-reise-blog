package at.htlleonding.repository;

import at.htlleonding.entity.BlogComment;
import at.htlleonding.entity.BlogEntry;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class BlogEntryRepository implements PanacheMongoRepository<BlogEntry> {

    void onStart(@Observes StartupEvent ev) {
        mongoCollection().createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending("title"),
                        Indexes.ascending("author.username")
                ),
                new IndexOptions().unique(true)
        );
        System.out.println("Unique compound index on (title + author.username) created successfully.");
    }

    public List<BlogComment> findAllCommentsByEntry(ObjectId blogEntryId) {
        BlogEntry entry = findById(blogEntryId);
        if (entry == null) {
            return Collections.emptyList();
        }
        return entry.blockComments;
    }

    public List<BlogComment> findNewestCommentsByEntry(ObjectId blogEntryId) {
        BlogEntry entry = findById(blogEntryId);
        if (entry == null) {
            return Collections.emptyList();
        }
        List<BlogComment> sorted = new ArrayList<>(entry.blockComments);
        sorted.sort((c1, c2) -> c2.creationDate.compareTo(c1.creationDate));

        if (sorted.size() > 3) {
            return sorted.subList(0, 3);
        } else {
            return sorted;
        }
    }

    public BlogComment findCommentByIdInEmbedded(ObjectId commentId) {
        List<BlogEntry> allEntries = listAll();

        for (BlogEntry entry : allEntries) {
            if (entry.blockComments != null) {
                for (BlogComment c : entry.blockComments) {
                    if (commentId.equals(c.id)) {
                        return c;
                    }
                }
            }
        }
        return null;
    }
}
