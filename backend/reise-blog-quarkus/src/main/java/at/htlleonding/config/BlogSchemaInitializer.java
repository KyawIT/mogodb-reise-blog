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
@Priority(1)  // Wird zuerst ausgeführt
public class BlogSchemaInitializer {

    private final MongoClient mongoClient;
    private static final int MAX_RETRIES = 10;
    private static final int WAIT_TIME_SECONDS = 5;
    private final JsonSchema jsonSchema;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BlogSchemaInitializer(MongoClient mongoClient) {
        this.mongoClient = mongoClient;

        // 1) JSON-Schema für com.networknt.schema laden
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("blogEntrySchema.json")) {
            if (is == null) {
                throw new RuntimeException("❌ Schema-Datei blogEntrySchema.json nicht gefunden!");
            }
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            this.jsonSchema = factory.getSchema(is);
        } catch (Exception e) {
            throw new RuntimeException("❌ Fehler beim Laden des Schemas: " + e.getMessage(), e);
        }
    }

    void onStart(@Observes StartupEvent event) {
        System.out.println("🔄 BlogSchemaInitializer wird ausgeführt...");

        // 2) Auf MongoDB warten
        MongoDatabase database = waitForDatabase("blogdb@localhost");
        if (database == null) {
            System.out.println("❌ MongoDB konnte nicht erreicht werden. Starte Quarkus nicht.");
            return;
        }

        System.out.println("✅ Verbindung zu MongoDB erfolgreich!");

        // 3) Collections erstellen (falls nicht vorhanden)
        createCollectionIfNotExists(database, "BlogEntries");
        createCollectionIfNotExists(database, "BlogUsers");
        createCollectionIfNotExists(database, "BlogCategories");
        createCollectionIfNotExists(database, "BlogComments");

        // 4) Schema anwenden
        applySchemaToBlogEntries(database);

        // 5) Optional: Beispiel-Test
        testValidation();
    }

    /**
     * Wartet wiederholt, bis MongoDB erreichbar ist (Ping).
     */
    private MongoDatabase waitForDatabase(String databaseName) {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                MongoDatabase database = mongoClient.getDatabase(databaseName);
                database.runCommand(new Document("ping", 1)); // kleiner Ping
                return database;
            } catch (Exception e) {
                attempts++;
                System.out.println("⏳ MongoDB nicht verfügbar. Warte " + WAIT_TIME_SECONDS + " Sekunden...");
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

    /**
     * Erstellt eine Collection nur, wenn sie noch nicht existiert.
     */
    private void createCollectionIfNotExists(MongoDatabase database, String collectionName) {
        List<String> collections = database.listCollectionNames().into(new java.util.ArrayList<>());
        if (!collections.contains(collectionName)) {
            database.createCollection(collectionName);
            System.out.println("✅ Collection '" + collectionName + "' wurde erstellt.");
        } else {
            System.out.println("✅ Collection '" + collectionName + "' existiert bereits.");
        }
    }

    /**
     * Lädt die JSON-Datei "blogEntrySchema.json" erneut, extrahiert das Feld "validator"
     * (das "$jsonSchema" enthält) und setzt es als Validator für die Collection "BlogEntries".
     */
    private void applySchemaToBlogEntries(MongoDatabase database) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("blogEntrySchema.json")) {
            if (is == null) {
                System.out.println("❌ Konnte blogEntrySchema.json nicht finden!");
                return;
            }

            // Das gesamte JSON laden
            Document topLevelDoc = Document.parse(new String(is.readAllBytes()));
            // Das Feld "validator" extrahieren
            Document validatorDoc = topLevelDoc.get("validator", Document.class);
            if (validatorDoc == null) {
                System.out.println("❌ JSON-Datei enthält kein 'validator'-Feld!");
                return;
            }

            // Schema per collMod setzen
            database.runCommand(
                    new Document("collMod", "BlogEntries")
                            .append("validator", validatorDoc)  // => enthält "$jsonSchema"
                            .append("validationLevel", "strict")
                            .append("validationAction", "error")
            );

            System.out.println("✅ Schema auf 'BlogEntries' angewendet!");
        } catch (Exception e) {
            System.out.println("❌ Fehler beim Anwenden des Schemas: " + e.getMessage());
        }
    }

    /**
     * Validiert einen Blog-Entry gegen das lokal geladene com.networknt-JsonSchema.
     * Wirf eine Exception, wenn das Dokument nicht gültig ist.
     */
    public void validateBlogEntry(JsonNode blogEntry) {
        Set<ValidationMessage> errors = jsonSchema.validate(blogEntry);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("🚨 Ungültige Daten: " + errors);
        }
        System.out.println("✅ Blog Entry ist gültig: " + blogEntry);
    }

    /**
     * Testet einmal ein Minimalobjekt, um zu zeigen, dass die Validierung funktioniert.
     */
    private void testValidation() {
        JsonNode testEntry = objectMapper.createObjectNode()
                .put("title", "Test Blog")
                .put("description", "Das ist ein Test-Eintrag.")
                .put("impressionCount", 100)
                .put("commentsAllowed", true);

        try {
            validateBlogEntry(testEntry);
            System.out.println("✅ Test-Validierung erfolgreich!");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Test-Validierung fehlgeschlagen: " + e.getMessage());
        }
    }
}
