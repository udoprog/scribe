package eu.toolchain.scribe;

public interface CaseFormat {
  /**
   * Convert lower-case camel to upper-case camel.
   *
   * @param input Input lower-case camel string.
   * @return Resulting upper-case camel string.
   */
  static String lowerCamelToUpperCamel(String input) {
    final StringBuilder builder = new StringBuilder();

    boolean firstLower = true;

    for (int i = 0; i < input.length(); i++) {
      final char c = input.charAt(i);

      if (Character.isLowerCase(c)) {
        if (firstLower) {
          builder.append(Character.toUpperCase(c));
          firstLower = false;
          continue;
        }
      } else {
        firstLower = false;
      }

      builder.append(c);
    }

    return builder.toString();
  }
}
