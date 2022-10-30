package guru.springframework.repositories.reactive;

import guru.springframework.domain.Category;
import guru.springframework.domain.Ingredient;
import guru.springframework.domain.Recipe;
import guru.springframework.domain.UnitOfMeasure;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RunWith(SpringRunner.class)
@DataMongoTest
public class RecipeReactiveRepositoryTest {

    @Autowired
    private RecipeReactiveRepository repo;

    @Autowired
    private UnitOfMeasureReactiveRepository uomRepo;

    @Before
    public void setUp() {
        repo.deleteAll().block();
        Category cat1 = createCategory(1, "cat1");
        Category cat2 = createCategory(2, "cat2");
        Set<Category> cats = Stream.of(cat1, cat2).collect(Collectors.toSet());
        Recipe recipe = new Recipe();
        recipe.setId("1");
        recipe.setDescription("One");
        recipe.setCategories(cats);
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setId("1");
        unitOfMeasure.setDescription("deneme");
        recipe.addIngredient(createIngredient(unitOfMeasure));
        repo.save(recipe).block();

        UnitOfMeasure foundUom = uomRepo.findById("1").block();
        Recipe recipe2 = new Recipe();
        recipe2.setId("2");
        recipe2.setDescription("Two");
        recipe2.addIngredient(createIngredient(foundUom));
        Set<Category> catFor2 = Stream.of(cat1).collect(Collectors.toSet());
        recipe2.setCategories(catFor2);

        repo.save(recipe2).block();
    }

    private Ingredient createIngredient(UnitOfMeasure unitOfMeasure) {
        Ingredient ingredient = new Ingredient();
        ingredient.setDescription("ingredient");
        ingredient.setAmount(BigDecimal.ONE);
        ingredient.setUom(unitOfMeasure);
        return ingredient;
    }

    private Category createCategory(int id, String desc) {
        Category category = new Category();
        category.setId(String.valueOf(id));
        category.setDescription(desc);
        return category;
    }

    @Test
    public void shouldFindAllRecipes() {
        Set<Recipe> recipes = repo.findAll().toStream().collect(Collectors.toSet());

        repo.findAll().toStream().forEach((r) -> System.err.println(r.getDescription()));

        MatcherAssert.assertThat(recipes, Matchers.hasSize(2));
    }

    @Test
    public void shouldFindByDescription() {
        Mono<Recipe> foundRecipe = repo.findByDescription("Two");

        MatcherAssert.assertThat(foundRecipe.block().getId(), Matchers.equalTo("2"));
    }
}