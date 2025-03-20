package at.htlleonding.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "BlogUsers")
public class BlogUser {
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
}
