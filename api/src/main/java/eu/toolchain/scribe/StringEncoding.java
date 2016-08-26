package eu.toolchain.scribe;

/**
 * An encoding implementation capable of converting to and from strings.
 *
 * @param <Source> The instance type.
 */
public interface StringEncoding<Source> {
  /**
   * Encode the given instance as a string.
   *
   * @param instance The instance to encode.
   * @return A string encoded from the instance.
   */
  String encodeAsString(Source instance);

  /**
   * Decode the given string as an instance.
   *
   * @param source The source string to decode.
   * @return An instance decoded from the string.
   */
  Source decodeFromString(String source);
}
