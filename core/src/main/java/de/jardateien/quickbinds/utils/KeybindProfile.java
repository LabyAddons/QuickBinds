package de.jardateien.quickbinds.utils;

import org.jetbrains.annotations.NotNull;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KeybindProfile {

  private final String profile;
  private final String address;
  private final List<String> keyboard;

  public KeybindProfile(@NotNull String profile) {
    this(profile, null);
  }

  public KeybindProfile(@NotNull String profile, String address) {
    this.profile = profile;
    this.address = address;
    this.keyboard = new LinkedList<>();
  }

  public void addKeybind(String keybindPath) {
    this.keyboard.add(keybindPath);
  }

  public String profile() {
    return this.profile;
  }
  public String address() {
    return this.address;
  }
  public List<String> keyboard() {
    return this.keyboard;
  }
}
