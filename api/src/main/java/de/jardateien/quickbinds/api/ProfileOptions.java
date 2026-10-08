package de.jardateien.quickbinds.api;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The parsed content of a profile's own options.txt. All lines are kept untouched, only the lines
 * changed through {@link #set(String, String)} are rewritten when the file is saved.
 */
public class ProfileOptions {

  private static final char SEPARATOR = ':';

  private final List<String> lines;
  private final Map<String, Integer> indices = new LinkedHashMap<>();

  public ProfileOptions(List<String> lines) {
    this.lines = new ArrayList<>(lines);

    for (int index = 0; index < this.lines.size(); index++) {
      String line = this.lines.get(index);
      int separator = line.indexOf(SEPARATOR);
      if (separator <= 0)
        continue;

      // Minecraft lets later lines override earlier ones, so the last occurrence wins
      this.indices.put(line.substring(0, separator), index);
    }
  }

  /**
   * @return all option keys of the file in the order they appear
   */
  public Set<String> keys() {
    return this.indices.keySet();
  }

  @Nullable
  public String get(String key) {
    Integer index = this.indices.get(key);
    if (index == null)
      return null;

    String line = this.lines.get(index);
    return line.substring(line.indexOf(SEPARATOR) + 1);
  }

  public void set(String key, String value) {
    String line = key + SEPARATOR + value;
    Integer index = this.indices.get(key);
    if (index != null) {
      this.lines.set(index, line);
      return;
    }

    this.indices.put(key, this.lines.size());
    this.lines.add(line);
  }

  public List<String> lines() {
    return this.lines;
  }
}
