package eu.toolchain.scribe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * @see Jackson1NullExplainedTest
 */
public class Jackson2OptionalExplainedTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  private ObjectMapper m;

  @Before
  public void setUp() {
    m = new ObjectMapper();
    m.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    m.enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
    m.enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
    m.registerModule(new Jdk8Module());
  }

  /**
   * Optional field absent?
   * <p>
   * Unfortunately we do not get Optional.empty, instead we get an exception. Forcing the API to
   * accept null.
   */
  @Test
  public void test1() throws Exception {
    exception.expect(JsonMappingException.class);
    exception.expectMessage("Missing creator property 'title'");

    m.readValue("{\"name\": \"joe\", \"age\": 42}", Person.class);
  }

  /**
   * Optional fields have to be set as null in the API.
   */
  @Test
  public void test2a() throws Exception {
    final Person person =
      m.readValue("{\"name\": \"joe\", \"age\": 42, \"title\": null}", Person.class);

    assertEquals("joe", person.getName());
    assertEquals(42, person.getAge());
    assertEquals(Optional.empty(), person.getTitle());
  }

  @Test
  public void test2b() throws Exception {
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
      this.name = name;
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
