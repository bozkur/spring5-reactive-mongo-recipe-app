package guru.springframework.repositories.reactive;

import guru.springframework.domain.UnitOfMeasure;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureReactiveRepositoryTest {

    @Autowired
    private UnitOfMeasureReactiveRepository repo;

    @Test
    public void shouldFindUoM() {
        UnitOfMeasure each = new UnitOfMeasure();
        each.setId("1L");
        each.setDescription("Each");

        UnitOfMeasure tableSpoon = new UnitOfMeasure();
        tableSpoon.setId("2");
        tableSpoon.setDescription("Table spoon");

        repo.save(each).block();
        repo.save(tableSpoon).block();

        UnitOfMeasure found = repo.findById("2").block();

        MatcherAssert.assertThat(found.getDescription(), Matchers.equalTo("Table spoon"));
    }

    @Test
    public void shouldFindByIdReturnNullWhenSuppliedIdCanNotBeFound() {
        UnitOfMeasure found = repo.findById("200").block();
        assertNull(found);
    }

}