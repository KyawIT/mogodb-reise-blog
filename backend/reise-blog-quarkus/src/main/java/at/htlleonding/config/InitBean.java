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
import java.util.Arrays;
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
        LOGGER.info("Fuege Beispiel-Daten in MongoDB ein...");

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
        blogUserRepository.persist(new BlogUser(null, "john_doe", "John", "Doe", "john.doe@example.com", "password123"));
        blogUserRepository.persist(new BlogUser(null, "jane_doe", "Jane", "Doe", "jane.doe@example.com", "password456"));
        blogUserRepository.persist(new BlogUser(null, "max_mustermann", "Max", "Mustermann", "max@example.com", "test123"));
        blogUserRepository.persist(new BlogUser(null, "alice_smith", "Alice", "Smith", "alice.smith@example.com", "securePass"));
        LOGGER.info("4 BlogUsers wurden hinzugefuegt.");
    }

    private void initializeBlogCategories() {
        blogCategoryRepository.persist(new BlogCategory(null, "Technology"));
        blogCategoryRepository.persist(new BlogCategory(null, "Travel"));
        blogCategoryRepository.persist(new BlogCategory(null, "Food"));
        blogCategoryRepository.persist(new BlogCategory(null, "Fitness"));
        blogCategoryRepository.persist(new BlogCategory(null, "Lifestyle"));
        LOGGER.info("5 BlogCategories wurden hinzugefuegt.");
    }

    private void initializeBlogEntries() {
        List<BlogCategory> categories = blogCategoryRepository.listAll();
        List<BlogUser> users = blogUserRepository.listAll();

        // 1) Entry mit Links und Bildern
        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Die Zukunft der KI",
                        users.get(0),
                        "Wie KI unseren Alltag verändert.",
                        List.of(new Date()),
                        0,
                        true,
                        new ArrayList<>(Arrays.asList(
                                "Künstliche Intelligenz revolutioniert die Art und Weise, wie wir leben und arbeiten. Sie verändert Branchen, schafft neue Möglichkeiten und stellt uns vor ethische Herausforderungen. In diesem Artikel erfahren Sie mehr über die neuesten Entwicklungen und wie KI unsere Zukunft prägen könnte.",
                                "data:text/plain;base64,aHR0cHM6Ly93d3cuYWkuY29tL2FpLWluLXRoZS1mdXR1cmUsaHR0cHM6Ly93d3cuZGVlcGxlYXJuaW5nLmNvbS9leGFtcGxlcw==",
                                "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/4QBMRXhpZgAATU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAABQKADAAQAAAABAAABQA=="
                        )),
                        new ArrayList<>(),
                        categories.get(0)
                )
        );

        // 2) Entry mit zwei Bildern
        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Reise durch Skandinavien",
                        users.get(1),
                        "Eine unvergessliche Reise durch Norwegen und Schweden.",
                        List.of(new Date()),
                        0,
                        true,
                        new ArrayList<>(Arrays.asList(
                                "Entdecken Sie die atemberaubende Schönheit Skandinaviens. Von den norwegischen Fjorden bis zu den schwedischen Wäldern - eine Reise voller Abenteuer und unvergesslicher Erlebnisse. Diese Region bietet eine perfekte Mischung aus Natur, Kultur und Geschichte.",
                                "",
                                "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/4QBMRXhpZgAATU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAABQKADAAQAAAABAAABQA==,data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/4QBMRXhpZgAATU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAABQKADAAQAAAABAAABQA=="
                        )),
                        new ArrayList<>(),
                        categories.get(1)
                )
        );

        // 3) Entry ohne Bilder/Links
        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Gesunde Ernährung im Alltag",
                        users.get(2),
                        "Tipps für eine ausgewogene Ernährung.",
                        List.of(new Date()),
                        0,
                        false,
                        new ArrayList<>(Arrays.asList(
                                "Eine ausgewogene Ernährung ist der Schlüssel zu einem gesunden Leben. Hier sind praktische Tipps, die Sie einfach in Ihren Alltag integrieren können. Essen Sie mehr Obst und Gemüse, trinken Sie ausreichend Wasser und vermeiden Sie verarbeitete Lebensmittel.",
                                "",
                                ""
                        )),
                        new ArrayList<>(),
                        categories.get(2)
                )
        );

        // 4) Entry mit Links und Bildern
        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Krafttraining für Anfänger",
                        users.get(3),
                        "Wie du mit dem Training startest.",
                        List.of(new Date()),
                        0,
                        false,
                        new ArrayList<>(Arrays.asList(
                                "Der perfekte Einstieg ins Krafttraining. Lernen Sie die grundlegenden Übungen und erstellen Sie Ihren eigenen Trainingsplan. Krafttraining stärkt nicht nur die Muskeln, sondern verbessert auch die allgemeine Gesundheit und das Wohlbefinden.",
                                "data:text/plain;base64,aHR0cHM6Ly93d3cuZml0bmVzcy5kZS9rcmFmdHRyYWluaW5nLGh0dHBzOi8vd3d3LnRyYWluaW5nLmRlL2FuZmFlbmdlcg==",
                                "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/4QBMRXhpZgAATU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAABQKADAAQAAAABAAABQA=="
                        )),
                        new ArrayList<>(),
                        categories.get(3)
                )
        );

        // 5) Entry ohne Bilder/Links
        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "HIIT-Training: Maximale Effizienz",
                        users.get(2),
                        "Warum High-Intensity-Training so effektiv ist.",
                        List.of(new Date()),
                        0,
                        true,
                        new ArrayList<>(Arrays.asList(
                                "HIIT ist die effektivste Methode, um in kurzer Zeit maximale Ergebnisse zu erzielen. Entdecken Sie unsere besten HIIT-Workouts und erfahren Sie, wie Sie Ihre Fitnessziele schneller erreichen können.",
                                "",
                                ""
                        )),
                        new ArrayList<>(),
                        categories.get(3)
                )
        );

        // 6) Entry mit Links und Bildern
        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Yoga für mentale Balance",
                        users.get(0),
                        "Wie Yoga Stress abbaut.",
                        List.of(new Date()),
                        0,
                        true,
                        new ArrayList<>(Arrays.asList(
                                "Yoga ist mehr als nur körperliche Übungen. Erfahren Sie, wie Sie mit Yoga innere Ruhe und mentale Stärke entwickeln können. Diese Praxis hilft, Stress abzubauen und die Konzentration zu verbessern.",
                                "data:text/plain;base64,aHR0cHM6Ly93d3cueW9nYS1ndWlkZS5kZS9tZWRpdGF0aW9uLGh0dHBzOi8vd3d3LnlvZ2EuZGUvYmVnaW5uZXJz",
                                "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/4QBMRXhpZgAATU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAABQKADAAQAAAABAAABQA=="
                        )),
                        new ArrayList<>(),
                        categories.get(3)
                )
        );

        // 7) Entry ohne Bilder/Links
        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Minimalismus im Alltag",
                        users.get(1),
                        "Warum weniger oft mehr ist.",
                        List.of(new Date()),
                        0,
                        false,
                        new ArrayList<>(Arrays.asList(
                                "Entdecken Sie die Kunst des Minimalismus und wie sie Ihr Leben vereinfachen und bereichern kann. Weniger Besitz bedeutet mehr Freiheit und weniger Stress.",
                                "",
                                ""
                        )),
                        new ArrayList<>(),
                        categories.get(4)
                )
        );

        // 8) Entry mit Links und Bildern
        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Produktivität steigern: 5 Tipps",
                        users.get(3),
                        "Effektive Strategien für mehr Fokus.",
                        List.of(new Date()),
                        0,
                        true,
                        new ArrayList<>(Arrays.asList(
                                "Mit diesen 5 bewährten Strategien steigern Sie Ihre Produktivität und erreichen Ihre Ziele schneller. Lernen Sie, wie Sie Ihre Zeit effektiv nutzen und Ablenkungen minimieren können.",
                                "data:text/plain;base64,aHR0cHM6Ly93d3cucHJvZHVrdGl2aXRhZXQuZGUvdGlwcHMsaHR0cHM6Ly93d3cuZm9rdXMuZGUvbWV0aG9kZW4=",
                                "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/4QBMRXhpZgAATU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAABQKADAAQAAAABAAABQA=="
                        )),
                        new ArrayList<>(),
                        categories.get(4)
                )
        );

        // 9) Entry ohne Bilder/Links
        blogEntryRepository.persist(
                new BlogEntry(
                        null,
                        "Selbstbewusstsein stärken",
                        users.get(2),
                        "Warum mentale Stärke wichtig ist.",
                        List.of(new Date()),
                        0,
                        true,
                        new ArrayList<>(Arrays.asList(
                                "Lernen Sie praktische Techniken kennen, um Ihr Selbstbewusstsein zu stärken und mental widerstandsfähiger zu werden. Diese Fähigkeiten helfen Ihnen, Herausforderungen zu meistern und Ihre Ziele zu erreichen.",
                                "",
                                ""
                        )),
                        new ArrayList<>(),
                        categories.get(4)
                )
        );

        LOGGER.info("9 BlogEntries wurden hinzugefuegt.");
    }


    private void addEmbeddedComments() {
        List<BlogEntry> commentableEntries = blogEntryRepository.find("commentsAllowed", true).list();
        List<BlogUser> users = blogUserRepository.listAll();

        if(commentableEntries.getFirst().blockComments.isEmpty()) {

            for (int i = 0; i < 2 && i < commentableEntries.size(); i++) {
                BlogEntry entry = commentableEntries.get(i);

                if (entry.blockComments == null) {
                    entry.blockComments = new ArrayList<>();
                }

                entry.blockComments.add(new BlogComment(null, users.get(0), new Date(), entry.id, "Sehr informativer Beitrag!"));
                entry.blockComments.add(new BlogComment(null, users.get(1), new Date(), entry.id, "Hat mir echt geholfen!"));
                entry.blockComments.add(new BlogComment(null, users.get(2), new Date(), entry.id, "Gute Tipps, danke!"));

                blogEntryRepository.update(entry);
            }

            for (int i = 2; i < 4 && i < commentableEntries.size(); i++) {
                BlogEntry entry = commentableEntries.get(i);
                if (entry.blockComments == null) {
                    entry.blockComments = new ArrayList<>();
                }

                entry.blockComments.add(new BlogComment(null, users.get(1), new Date(), entry.id, "Sehr inspirierend!"));
                entry.blockComments.add(new BlogComment(null, users.get(3), new Date(), entry.id, "Ich werde das ausprobieren."));

                blogEntryRepository.update(entry);
            }

            LOGGER.info("Eingebettete Kommentare zu den BlogEntries hinzugefügt.");
        }
    }
}
