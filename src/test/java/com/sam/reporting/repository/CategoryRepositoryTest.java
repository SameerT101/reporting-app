package com.sam.reporting.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional          // optional – keeps the session open for lazy collections
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository repo;

    @Test
    void categoriesAreLoadedWithProducts() {
        var categories = repo.findAllWithProducts();

        assertThat(categories).isNotEmpty()
                .allSatisfy(c ->
                        assertThat(c.getProducts())
                                .withFailMessage("Category %s has no products", c.getName())
                                .isNotEmpty());
    }
}