package eu.toolchain.scribe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import eu.toolchain.scribe.gson.NonNullDeserializer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Ambiguous handling of null causes really bad issues in services.
 * <p>
 * This test examines how hard it is to make Gson disallow null values.
 * <p>
 * Note that I have yet to find a way for Gson to deal with primitive defaults on absent and null.
 * <p>
 * Oh yeah, and note that {@link Person} doesn't even have a callable constructor?
 */
public class GsonNullExplainedTest {
  /**
   * Just an empty object.
   * <p>
   * Note how name is null, and age is assigned {@code 0}.
   */
  @Test
  public void test1() throws Exception {
    final Gson gson = new Gson();
    final Person person = gson.fromJson("{}", Person.class);

    assertNull(person.getName());
    assertEquals(0, person.getAge());
  }

  /**
   * So let's try to figure out how to disallow null.
   * <p>
   * Seriously, Gson?
   * http://stackoverflow.com/questions/21626690/gson-optional-and-required-fields
   * <p>
   * Works for name at least, but age is never null. So I guess it's back to boxed types
   * everywhere?
   */
  @Test(expected = JsonParseException.class)
  public void test2() throws Exception {
    final Gson gson = new GsonBuilder()
      .registerTypeAdapter(Person.class, new NonNullDeserializer<Person>())
      .create();

    gson.fromJson("{}", Person.class);
  }

  public static class Person {
    private final String name;
    private final int age;

    public Person(final String name, final int age) {
      throw new IllegalStateException("lol, who cares right?");
    }

    public String getName() {
      return name;
    }

    public int getAge() {
      return age;
    }
  }
}
