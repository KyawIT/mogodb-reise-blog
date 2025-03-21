package at.htlleonding.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "BlogComments")
public class BlogComment {
    @BsonId
    public ObjectId id;
    public BlogUser author;
    public Date creationDate;
    public ObjectId blogEntryId;
    public String content;
}
