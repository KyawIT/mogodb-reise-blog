package at.htlleonding.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "BlogUsers")
public class BlogUser {
    public String username;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
}
