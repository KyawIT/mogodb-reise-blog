package at.htlleonding.repository;

import at.htlleonding.entity.BlogComment;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.List;

@ApplicationScoped
public class BlogCommentRepository implements PanacheMongoRepository<BlogComment> {

    public List<BlogComment> findCommentsByEntry(ObjectId blogEntryId) {
        return find(
                "{'blogEntryId': ?1} sort({'creationDate': -1})",
                blogEntryId
        )
                .page(0, 3)
                .list();
    }

    public List<BlogComment> findAllCommentsByEntry(ObjectId blogEntryId) {
        return find(
                "{'blogEntryId': ?1} sort({'creationDate': -1})",
                blogEntryId
        )
                .list();
    }
}
