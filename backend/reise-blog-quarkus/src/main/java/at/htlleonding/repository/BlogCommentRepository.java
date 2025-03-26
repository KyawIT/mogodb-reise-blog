package at.htlleonding.repository;

import at.htlleonding.entity.BlogComment;
import at.htlleonding.entity.BlogEntry;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class BlogCommentRepository implements PanacheMongoRepository<BlogComment> {
}
