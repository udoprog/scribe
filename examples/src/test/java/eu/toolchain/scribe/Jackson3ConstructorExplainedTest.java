package eu.toolchain.scribe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * This is the final test. The only approach I've found with jackson which allows the absence and
 * null fields to be treated equally.
 * <p>
 * This require you to implement your own constructor, where you null check every reference type
 * <em>except</em> {@code java.util.Optional}.
 *
 * @see Jackson1NullExplainedTest
 * @see Jackson2OptionalExplainedTest
 */
public class Jackson3ConstructorExplainedTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  private ObjectMapper m;

  @Before
  public void setUp() {
    m = new ObjectMapper();
    m.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    m.enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
    // this option cannot be set, because it prevents Optional being empty if omitted from the
    // document.
    // m.enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
    m.registerModule(new Jdk8Module());
  }

  @Test
  public void test1() throws Exception {
    final Person person = m.readValue("{\"name\": \"joe\", \"age\": 42}", Person.class);

    assertEquals("joe", person.getName());
    assertEquals(42, person.getAge());
    assertEquals(Optional.empty(), person.getTitle());
  }

  @Test
  public void test2() throws Exception {
    final Person person =
      m.readValue("{\"name\": \"joe\", \"age\": 42, \"title\": null}", Person.class);

    assertEquals("joe", person.getName());
    assertEquals(42, person.getAge());
    assertEquals(Optional.empty(), person.getTitle());
  }

  @Test
  public void test3() throws Exception {
    final Person person =
      m.readValue("{\"name\": \"joe\", \"age\": 42, \"title\": \"dr\"}", Person.class);

    assertEquals("joe", person.getName());
    assertEquals(42, person.getAge());
    assertEquals(Optional.of("dr"), person.getTitle());
  }

  public static class Person {
    private final String name;
    private final int age;
    private final Optional<String> title;

    @JsonCreator
    public Person(
      final @JsonProperty("name") String name, final @JsonProperty("age") int age,
      final @JsonProperty("title") Optional<String> title
    ) {
      this.name = Objects.requireNonNull(name, "name");
      this.age = age;
      this.title = title;
    }

    public String getName() {
      return name;
    }

    public int getAge() {
      return age;
    }

    public Optional<String> getTitle() {
      return title;
    }
  }
}
