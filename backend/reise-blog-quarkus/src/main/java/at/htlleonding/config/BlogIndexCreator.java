package at.htlleonding.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class BlogIndexCreator {

    @Inject
    MongoClient mongoClient; // Inject the MongoDB client

    @PostConstruct
    public void createUniqueIndexes() {
        // 1) Hole die Datenbank
        MongoDatabase database = mongoClient.getDatabase("blogdb@localhost");

        // 2) Erstelle einen eindeutigen Index in der BlogUsers-Collection auf das Feld `username`
        database.getCollection("BlogUsers")
                .createIndex(
                        Indexes.ascending("username"),
                        new IndexOptions().unique(true)
                );
        System.out.println("✅ Unique index auf 'username' in BlogUsers erstellt.");

        database.getCollection("BlogEntries")
                .createIndex(
                        Indexes.compoundIndex(
                                Indexes.ascending("title"),
                                Indexes.ascending("author.username")
                        ),
                        new IndexOptions().unique(true)
                );
        System.out.println("✅ Unique compound index auf (title, username) in BlogEntries erstellt.");
    }
}

