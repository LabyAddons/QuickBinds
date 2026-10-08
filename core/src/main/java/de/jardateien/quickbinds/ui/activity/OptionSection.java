package de.jardateien.quickbinds.ui.activity;

/**
 * The buttons of the profile options menu, ordered like the vanilla options screen (two per row).
 */
public enum OptionSection {

  CONTROLS("controls"),
  MOUSE("mouse"),
  VIDEO("video"),
  SOUND("sound"),
  CHAT("chat"),
  SKIN("skin"),
  ACCESSIBILITY("accessibility"),
  OTHER("other");

  private final String translationKey;

  OptionSection(String name) {
    this.translationKey = "quickbinds.ui.options.section." + name;
  }

  public String translationKey() {
    return this.translationKey;
  }

  /**
   * @return whether the options of this section are split into several categories that need headers
   */
  public boolean hasCategories() {
    return this == CONTROLS || this == OTHER;
  }
}
