package at.htlleonding.repository;

import at.htlleonding.entity.BlogComment;
import at.htlleonding.entity.BlogEntry;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class BlogEntryRepository implements PanacheMongoRepository<BlogEntry> {
    public List<BlogComment> findAllCommentsByEntry(ObjectId blogEntryId) {
        // 1. BlogEntry laden
        BlogEntry entry = findById(blogEntryId);
        if (entry == null) {
            // Kein Eintrag -> Keine Kommentare
            return Collections.emptyList();
        }
        // 2. Alle embedded Kommentare zurückgeben
        return entry.blockComments; // Kann schon gefüllt oder leer sein
    }

    public List<BlogComment> findNewestCommentsByEntry(ObjectId blogEntryId) {
        BlogEntry entry = findById(blogEntryId);
        if (entry == null) {
            return Collections.emptyList();
        }
        // 3. Java-Sortierung nach creationDate absteigend
        List<BlogComment> sorted = new ArrayList<>(entry.blockComments);
        sorted.sort((c1, c2) -> c2.creationDate.compareTo(c1.creationDate));

        // 4. Nur die ersten 3 zurückgeben
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
