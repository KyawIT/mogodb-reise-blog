package at.htlleonding.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.bson.Document;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Startup
@Priority(1)
public class BlogSchemaInitializer {

    private final MongoClient mongoClient;
    private static final int MAX_RETRIES = 10;
    private static final int WAIT_TIME_SECONDS = 5;
    private final JsonSchema jsonSchema;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BlogSchemaInitializer(MongoClient mongoClient) {
        this.mongoClient = mongoClient;

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("blogEntrySchema.json")) {
            if (is == null) {
                throw new RuntimeException("Schema-Datei blogEntrySchema.json nicht gefunden!");
            }
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            this.jsonSchema = factory.getSchema(is);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Laden des Schemas: " + e.getMessage(), e);
        }
    }

    void onStart(@Observes StartupEvent event) {
        System.out.println("BlogSchemaInitializer wird ausgeführt...");

        MongoDatabase database = waitForDatabase("blogdb@localhost");
        if (database == null) {
            System.out.println("MongoDB konnte nicht erreicht werden. Starte Quarkus nicht.");
            return;
        }

        System.out.println("Verbindung zu MongoDB erfolgreich!");

        createCollectionIfNotExists(database, "BlogEntries");
        createCollectionIfNotExists(database, "BlogUsers");
        createCollectionIfNotExists(database, "BlogCategories");

        applySchemaToBlogEntries(database);

        testValidation();
    }

    private MongoDatabase waitForDatabase(String databaseName) {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                MongoDatabase database = mongoClient.getDatabase(databaseName);
                database.runCommand(new Document("ping", 1));
                return database;
            } catch (Exception e) {
                attempts++;
                System.out.println("MongoDB nicht verfügbar. Warte " + WAIT_TIME_SECONDS + " Sekunden...");
                try {
                    TimeUnit.SECONDS.sleep(WAIT_TIME_SECONDS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        return null;
    }

    private void createCollectionIfNotExists(MongoDatabase database, String collectionName) {
        List<String> collections = database.listCollectionNames().into(new java.util.ArrayList<>());
        if (!collections.contains(collectionName)) {
            database.createCollection(collectionName);
            System.out.println("Collection '" + collectionName + "' wurde erstellt.");
        } else {
            System.out.println("Collection '" + collectionName + "' existiert bereits.");
        }
    }

    private void applySchemaToBlogEntries(MongoDatabase database) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("blogEntrySchema.json")) {
            if (is == null) {
                System.out.println("Konnte blogEntrySchema.json nicht finden!");
                return;
            }

            Document topLevelDoc = Document.parse(new String(is.readAllBytes()));
            Document validatorDoc = topLevelDoc.get("validator", Document.class);
            if (validatorDoc == null) {
                System.out.println("JSON-Datei enthält kein 'validator'-Feld!");
                return;
            }

            database.runCommand(
                    new Document("collMod", "BlogEntries")
                            .append("validator", validatorDoc)
                            .append("validationLevel", "strict")
                            .append("validationAction", "error")
            );

            System.out.println("Schema auf 'BlogEntries' angewendet!");
        } catch (Exception e) {
            System.out.println("Fehler beim Anwenden des Schemas: " + e.getMessage());
        }
    }

    public void validateBlogEntry(JsonNode blogEntry) {
        Set<ValidationMessage> errors = jsonSchema.validate(blogEntry);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Ungültige Daten: " + errors);
        }
        System.out.println("Blog Entry ist gültig: " + blogEntry);
    }


    private void testValidation() {
        JsonNode testEntry = objectMapper.createObjectNode()
                .put("title", "Test Blog")
                .put("description", "Das ist ein Test-Eintrag.")
                .put("impressionCount", 0)
                .put("commentsAllowed", true);

        try {
            validateBlogEntry(testEntry);
            System.out.println("Test-Validierung erfolgreich!");
        } catch (IllegalArgumentException e) {
            System.out.println("Test-Validierung fehlgeschlagen: " + e.getMessage());
        }
    }
}
