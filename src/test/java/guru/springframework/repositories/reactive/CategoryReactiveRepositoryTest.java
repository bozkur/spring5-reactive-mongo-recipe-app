package guru.springframework.repositories.reactive;

import guru.springframework.domain.Category;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@DataMongoTest
public class CategoryReactiveRepositoryTest {

    @Autowired
    private CategoryReactiveRepository repo;
    private Category cat1;
    private Category cat2;

    @Before
    public void setUp() {
        repo.deleteAll().block();
        cat1 = new Category();
        cat1.setId(String.valueOf(1));
        cat1.setDescription("Category 1");

        cat2 = new Category();
        cat2.setId(String.valueOf(2));
        cat2.setDescription("Category 2");

        Mono<Category> category1 = repo.save(cat1);
        Mono<Category> category2 = repo.save(cat2);

        category1.block();
        category2.block();
    }

    @Test
    public void shouldFetchCategories() throws InterruptedException {
        List<Category> categoryList = repo.findAll().toStream().collect(Collectors.toList());

        MatcherAssert.assertThat(categoryList.get(0).getDescription(), Matchers.equalTo(cat1.getDescription()));
        MatcherAssert.assertThat(categoryList.get(1).getDescription(), Matchers.equalTo(cat2.getDescription()));
    }

    @Test
    public void shouldFindById() {
        Mono<Category> catById = repo.findById(cat1.getId());

        Category category = catById.block();
        MatcherAssert.assertThat(category.getDescription(), Matchers.equalTo(cat1.getDescription()));
    }

    @Test
    public void shouldFindWithIdReturnsNullWhenSearchedIdCanNotBeFound() {
        Mono<Category> notFound = repo.findById("Some dummy id");
        Assertions.assertNull(notFound.block());
    }
}