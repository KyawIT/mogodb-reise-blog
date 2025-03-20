package at.htlleonding.config;

import at.htlleonding.entity.BlogCategory;
import at.htlleonding.entity.BlogComment;
import at.htlleonding.entity.BlogEntry;
import at.htlleonding.entity.BlogUser;
import at.htlleonding.repository.BlogCategoryRepository;
import at.htlleonding.repository.BlogEntryRepository;
import at.htlleonding.repository.BlogUserRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class InitBean {

    @Inject
    BlogCategoryRepository blogCategoryRepository;

    @Inject
    BlogUserRepository blogUserRepository;

    @Inject
    BlogEntryRepository blogEntryRepository;

    private static final Logger LOGGER = Logger.getLogger(String.valueOf(InitBean.class));

    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("🔄 Füge Beispiel-Daten in MongoDB ein...");

        if (blogUserRepository.count() == 0) {
            initializeBlogUsers();
        }
        if (blogCategoryRepository.count() == 0) {
            initializeBlogCategories();
        }
        if (blogEntryRepository.count() == 0) {
            initializeBlogEntries();
        }

        addEmbeddedComments();
    }

    private void initializeBlogUsers() {
        blogUserRepository.persist(new BlogUser("john_doe", "John", "Doe", "john.doe@example.com", "password123"));
        blogUserRepository.persist(new BlogUser("jane_doe", "Jane", "Doe", "jane.doe@example.com", "password456"));
        blogUserRepository.persist(new BlogUser("max_mustermann", "Max", "Mustermann", "max@example.com", "test123"));
        blogUserRepository.persist(new BlogUser("alice_smith", "Alice", "Smith", "alice.smith@example.com", "securePass"));
        LOGGER.info("✅ 4 BlogUsers wurden hinzugefügt.");
    }

    private void initializeBlogCategories() {
        blogCategoryRepository.persist(new BlogCategory("Technology"));
        blogCategoryRepository.persist(new BlogCategory("Travel"));
        blogCategoryRepository.persist(new BlogCategory("Food"));
        blogCategoryRepository.persist(new BlogCategory("Fitness"));
        blogCategoryRepository.persist(new BlogCategory("Lifestyle"));
        LOGGER.info("✅ 5 BlogCategories wurden hinzugefügt.");
    }

    private void initializeBlogEntries() {
        List<BlogCategory> categories = blogCategoryRepository.listAll();
        List<BlogUser> users = blogUserRepository.listAll();

        blogEntryRepository.persist(
                new BlogEntry(
                        null,  // id, wird von Mongo vergeben
                        "Die Zukunft der KI",
                        users.get(0),
                        "Wie KI unseren Alltag verändert.",
                        new ArrayList<>(),
                        50,
                        true,
                        new ArrayList<>(), // Noch keine Comments
                        categories.get(0)
                )
        );

        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Reise durch Skandinavien",
                        users.get(1),
                        "Eine unvergessliche Reise durch Norwegen und Schweden.",
                        new ArrayList<>(),
                        120,
                        true,
                        new ArrayList<>(),
                        categories.get(1)
                )
        );

        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Gesunde Ernährung im Alltag",
                        users.get(2),
                        "Tipps für eine ausgewogene Ernährung.",
                        new ArrayList<>(),
                        30,
                        false,
                        new ArrayList<>(),
                        categories.get(2)
                )
        );

        // Weitere Einträge ...
        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Krafttraining für Anfänger",
                        users.get(3),
                        "Wie du mit dem Training startest.",
                        new ArrayList<>(),
                        75,
                        false,
                        new ArrayList<>(),
                        categories.get(3)
                )
        );

        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "HIIT-Training: Maximale Effizienz",
                        users.get(2),
                        "Warum High-Intensity-Training so effektiv ist.",
                        new ArrayList<>(),
                        90,
                        true,
                        new ArrayList<>(),
                        categories.get(3)
                )
        );

        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Yoga für mentale Balance",
                        users.get(0),
                        "Wie Yoga Stress abbaut.",
                        new ArrayList<>(),
                        45,
                        true,
                        new ArrayList<>(),
                        categories.get(3)
                )
        );

        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Minimalismus im Alltag",
                        users.get(1),
                        "Warum weniger oft mehr ist.",
                        new ArrayList<>(),
                        110,
                        false,
                        new ArrayList<>(),
                        categories.get(4)
                )
        );

        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Produktivität steigern: 5 Tipps",
                        users.get(3),
                        "Effektive Strategien für mehr Fokus.",
                        new ArrayList<>(),
                        95,
                        true,
                        new ArrayList<>(),
                        categories.get(4)
                )
        );

        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Selbstbewusstsein stärken",
                        users.get(2),
                        "Warum mentale Stärke wichtig ist.",
                        new ArrayList<>(),
                        60,
                        true,
                        new ArrayList<>(),
                        categories.get(4)
                )
        );

        LOGGER.info("✅ 9 BlogEntries wurden hinzugefügt.");
    }

    private void addEmbeddedComments() {
        List<BlogEntry> commentableEntries = blogEntryRepository.find("commentsAllowed", true).list();
        List<BlogUser> users = blogUserRepository.listAll();

        for (int i = 0; i < 2 && i < commentableEntries.size(); i++) {
            BlogEntry entry = commentableEntries.get(i);

            if (entry.blockComments == null) {
                entry.blockComments = new ArrayList<>();
            }

            entry.blockComments.add(new BlogComment(users.get(0), new Date(), entry.id, "Sehr informativer Beitrag!"));
            entry.blockComments.add(new BlogComment(users.get(1), new Date(), entry.id, "Hat mir echt geholfen!"));
            entry.blockComments.add(new BlogComment(users.get(2), new Date(), entry.id, "Gute Tipps, danke!"));

            blogEntryRepository.update(entry);
        }

        for (int i = 2; i < 4 && i < commentableEntries.size(); i++) {
            BlogEntry entry = commentableEntries.get(i);
            if (entry.blockComments == null) {
                entry.blockComments = new ArrayList<>();
            }

            entry.blockComments.add(new BlogComment(users.get(1), new Date(), entry.id, "Sehr inspirierend!"));
            entry.blockComments.add(new BlogComment(users.get(3), new Date(), entry.id, "Ich werde das ausprobieren."));

            blogEntryRepository.update(entry);
        }

        LOGGER.info("✅ Eingebettete Kommentare zu den BlogEntries hinzugefügt.");
    }
}
