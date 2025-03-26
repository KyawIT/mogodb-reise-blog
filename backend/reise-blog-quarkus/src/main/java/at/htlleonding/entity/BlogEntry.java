package at.htlleonding.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "BlogEntries")
public class BlogEntry {

    @BsonId
    public ObjectId id;
    public String title;
    public BlogUser author;
    public String description;
    public List<Date> editDates;
    public int impressionCount;
    public boolean commentsAllowed;
    public List<String> content;
    public List<BlogComment> blockComments;
    public BlogCategory category;
}
