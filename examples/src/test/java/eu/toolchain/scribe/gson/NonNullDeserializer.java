package eu.toolchain.scribe.gson;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NonNullDeserializer<T> implements JsonDeserializer<T> {
  public T deserialize(JsonElement je, Type type, JsonDeserializationContext context)
      throws JsonParseException {
    final T pojo = new Gson().fromJson(je, type);

    Field[] fields = pojo.getClass().getDeclaredFields();

    for (Field f : fields) {
      try {
        f.setAccessible(true);

        if (f.get(pojo) == null) {
          throw new JsonParseException("Missing field in JSON: " + f.getName());
        }
      } catch (IllegalArgumentException ex) {
        Logger.getLogger(NonNullDeserializer.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
        Logger.getLogger(NonNullDeserializer.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    return pojo;
  }
}
