package at.htlleonding.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.Date;

@MongoEntity(collection = "BlogComments")
public class BlogComment {
    @BsonId
    public ObjectId id;
    public BlogUser author;
    public Date creationDate;
    public ObjectId blogEntryId;
    public String content;

    public BlogComment() {
        this.id = new ObjectId();
    }

    public BlogComment(ObjectId id, BlogUser author, Date creationDate, ObjectId blogEntryId, String content) {
        this.id = (id != null) ? id : new ObjectId();
        this.author = author;
        this.creationDate = creationDate;
        this.blogEntryId = blogEntryId;
        this.content = content;
    }
}
