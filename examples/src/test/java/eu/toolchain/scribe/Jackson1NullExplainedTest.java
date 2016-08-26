package eu.toolchain.scribe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Ambiguous handling of null causes really bad issues in services.
 * <p>
 * This test examines how hard it is to make Jackson disallow null values.
 * <p>
 * Progress have been made over the years, but it is disabled by default behind feature flags.
 */
public class Jackson1NullExplainedTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  private ObjectMapper m;

  @Before
  public void setUp() {
    m = new ObjectMapper();
  }

  /**
   * Default config with an empty object.
   * <p>
   * Note how name is null, and age is assigned {@code 0}.
   */
  @Test
  public void test1() throws Exception {
    final ObjectMapper m = new ObjectMapper();
    final Person person = m.readValue("{}", Person.class);

    assertNull(person.getName());
    assertEquals(0, person.getAge());
  }

  /**
   * Let's try with FAIL_ON_NULL_FOR_PRIMITIVES.
   * <p>
   * Now it no longer accepts the input because an absent primitive is treated the same as null.
   */
  @Test
  public void test2() throws Exception {
    exception.expect(JsonMappingException.class);
    exception.expectMessage("Can not map JSON null into type int");

    m.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);

    m.readValue("{}", Person.class);
  }

  /**
   * What about the name. In this one we try do add FAIL_ON_MISSING_CREATOR_PROPERTIES.
   * <p>
   * Now we should fail when name is absent.
   */
  @Test
  public void test3() throws Exception {
    exception.expect(JsonMappingException.class);
    exception.expectMessage("Missing creator property 'name'");

    m.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    m.enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);

    m.readValue("{\"age\": 32}", Person.class);
  }

  /**
   * So what about if name is null?
   * <p>
   * 2.8.0 has you covered: https://github.com/FasterXML/jackson-databind/pull/990
   */
  @Test
  public void test4() throws Exception {
    exception.expect(JsonMappingException.class);
    exception.expectMessage("Null value for creator property 'name'");

    m.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    m.enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
    m.enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);

    m.readValue("{\"age\": 32, \"name\": null}", Person.class);
  }

  @Test
  public void test5() throws Exception {
    m.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    m.enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
    m.enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);

    final Person person = m.readValue("{\"age\": 32, \"name\": \"joe\"}", Person.class);

    assertEquals("joe", person.getName());
    assertEquals(32, person.getAge());
  }

  public static class Person {
    private final String name;
    private final int age;

    @JsonCreator
    public Person(final @JsonProperty("name") String name, final @JsonProperty("age") int age) {
      this.name = name;
      this.age = age;
    }

    public String getName() {
      return name;
    }

    public int getAge() {
      return age;
    }
  }
}
