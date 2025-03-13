package at.htlleonding.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "BlogComments")
public class BlogComment extends PanacheMongoEntity {
}
