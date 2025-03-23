package at.htlleonding.config;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

@Singleton
public class BlogUserIndexCreator {

    @Inject
    MongoClient mongoClient; // Inject the MongoDB client

    @PostConstruct
    public void createUniqueUsernameIndex() {
        MongoDatabase database = mongoClient.getDatabase("blogdb@localhost"); // Replace with your database name
        database.getCollection("BlogUsers") // Replace with your collection name
                .createIndex(
                        Indexes.ascending("username"), // Create an index on the `username` field
                        new IndexOptions().unique(true) // Make the index unique
                );
        System.out.println("Unique index on 'username' created successfully.");
    }
}