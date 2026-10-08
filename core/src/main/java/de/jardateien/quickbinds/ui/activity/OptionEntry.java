package de.jardateien.quickbinds.ui.activity;

import net.labymod.api.client.gui.screen.key.Key;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * One line of the profile's options.txt while the editor is open. Holds the value from the file and
 * the not yet saved change.
 */
public class OptionEntry {

  public static final Pattern INTEGER_VALUE = Pattern.compile("-?\\d+");
  public static final Pattern DECIMAL_VALUE = Pattern.compile("-?\\d*\\.\\d+");
  public static final Pattern INTEGER_INPUT = Pattern.compile("-?\\d*");
  public static final Pattern DECIMAL_INPUT = Pattern.compile("-?\\d*\\.?\\d*");

  private final String optionKey;
  private final String name;
  private final OptionCategory category;
  private final int order;
  private final Type type;
  private final OptionSlider slider;
  private final String loadedValue;
  private final String searchText;
  private final List<OptionEntry> conflicts = new ArrayList<>();

  private String value;
  private Key key;

  public OptionEntry(String optionKey, String value, String name, String categoryName, @Nullable Key key) {
    this.optionKey = optionKey;
    this.name = name;
    this.category = OptionCategory.of(optionKey);
    this.order = OptionCategory.order(optionKey);
    Type type = Type.of(optionKey, value);
    this.slider = type == Type.INTEGER || type == Type.DECIMAL ? OptionSlider.of(optionKey, value) : null;
    this.type = this.slider != null ? Type.SLIDER : type;
    this.loadedValue = value;
    this.value = value;
    this.key = key;
    this.searchText = (name + '\n' + optionKey + '\n' + categoryName).toLowerCase(Locale.ROOT);
  }

  public String optionKey() {
    return this.optionKey;
  }

  public String name() {
    return this.name;
  }

  public OptionCategory category() {
    return this.category;
  }

  public int order() {
    return this.order;
  }

  public Type type() {
    return this.type;
  }

  /**
   * @return the slider range of a {@link Type#SLIDER} option
   */
  @Nullable
  public OptionSlider slider() {
    return this.slider;
  }

  public String value() {
    return this.value;
  }

  public String loadedValue() {
    return this.loadedValue;
  }

  /**
   * @return the assigned key of a keybind or null if the value in the profile is invalid
   */
  @Nullable
  public Key key() {
    return this.key;
  }

  public void set(String value) {
    this.value = value;
  }

  public void set(String value, @Nullable Key key) {
    this.value = value;
    this.key = key;
  }

  public boolean isAssigned() {
    return this.key != null && this.key != Key.NONE;
  }

  public boolean isInvalid() {
    return this.type == Type.KEYBIND && this.key == null;
  }

  public boolean isChanged() {
    return !this.value.equals(this.loadedValue);
  }

  public boolean matches(String lowerCaseQuery) {
    return lowerCaseQuery.isEmpty() || this.searchText.contains(lowerCaseQuery);
  }

  public List<OptionEntry> conflicts() {
    return this.conflicts;
  }

  public enum Type {
    KEYBIND,
    BOOLEAN,
    INTEGER,
    DECIMAL,
    SLIDER,
    TEXT,
    READ_ONLY;

    static Type of(String optionKey, String value) {
      if (optionKey.startsWith(OptionCategory.KEY_PREFIX))
        return KEYBIND;
      // The data version tells Minecraft how to upgrade the file, changing it breaks the profile
      if (optionKey.equals("version"))
        return READ_ONLY;
      if (value.equals("true") || value.equals("false"))
        return BOOLEAN;
      if (INTEGER_VALUE.matcher(value).matches())
        return INTEGER;
      if (DECIMAL_VALUE.matcher(value).matches())
        return DECIMAL;

      return TEXT;
    }
  }
}
