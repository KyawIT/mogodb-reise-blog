package at.htlleonding.config;

import at.htlleonding.entity.BlogCategory;
import at.htlleonding.entity.BlogUser;
import at.htlleonding.repository.BlogCategoryRepository;
import at.htlleonding.repository.BlogUserRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.util.logging.Logger;

@ApplicationScoped
public class InitBean {
    @Inject
    BlogCategoryRepository blogCategoryRepository;

    @Inject
    BlogUserRepository blogUserRepository;

    private static final Logger LOGGER = Logger.getLogger(String.valueOf(InitBean.class));
    void onStart(@Observes StartupEvent ev) {
        LOGGER.info("Initializing database with sample data...");

        if (blogUserRepository.count() == 0) {
            initializeBlogCategories();
            initializeBlogUsers();
            LOGGER.info("Added sample BlogUser data to the database.");
        } else {
            LOGGER.info("Database already contains data. Skipping initialization.");
        }
    }


    private void initializeBlogUsers() {
        if (blogUserRepository.count() == 0) {
            // Create and persist sample BlogUser entities
            BlogUser user1 = new BlogUser();
            user1.username = "john_doe";
            user1.firstName = "John";
            user1.lastName = "Doe";
            user1.email = "john.doe@example.com";
            user1.password = "password123";
            blogUserRepository.persist(user1);

            BlogUser user2 = new BlogUser();
            user2.username = "jane_doe";
            user2.firstName = "Jane";
            user2.lastName = "Doe";
            user2.email = "jane.doe@example.com";
            user2.password = "password456";
            blogUserRepository.persist(user2);

            LOGGER.info("Added sample BlogUser data to the database.");
        } else {
            LOGGER.info("BlogUser collection already contains data. Skipping initialization.");
        }
    }

    private void initializeBlogCategories() {
        if (blogCategoryRepository.count() == 0) {
            // Create and persist sample BlogCategory entities
            BlogCategory category1 = new BlogCategory();
            category1.category = "Technology";
            blogCategoryRepository.persist(category1);

            BlogCategory category2 = new BlogCategory();
            category2.category = "Travel";
            blogCategoryRepository.persist(category2);

            BlogCategory category3 = new BlogCategory();
            category3.category = "Food";
            blogCategoryRepository.persist(category3);

            BlogCategory category4 = new BlogCategory();
            category4.category = "Lifestyle";
            blogCategoryRepository.persist(category4);

            LOGGER.info("Added sample BlogCategory data to the database.");
        } else {
            LOGGER.info("BlogCategory collection already contains data. Skipping initialization.");
        }
    }
}
