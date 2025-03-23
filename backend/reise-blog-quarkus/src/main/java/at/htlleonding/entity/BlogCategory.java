package at.htlleonding.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "BlogCategories")
public class BlogCategory {
    private ObjectId id;
    private String category;
}