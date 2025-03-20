package at.htlleonding.entity;

import io.quarkus.arc.All;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@MongoEntity(collection = "BlogCategories")
public class BlogCategory {
    public String category;
}
