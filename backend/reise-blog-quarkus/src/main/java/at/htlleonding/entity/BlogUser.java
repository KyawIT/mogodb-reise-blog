package at.htlleonding.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "BlogUsers")
public class BlogUser {
    @BsonId
    public ObjectId id;
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
}
