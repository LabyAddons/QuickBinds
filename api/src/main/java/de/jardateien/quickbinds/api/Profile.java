package de.jardateien.quickbinds.api;

import net.labymod.api.client.gui.screen.key.Key;
import java.util.UUID;

public class Profile {

  private String name;
  private final String version;
  private final int protocol;
  private Key key;
  private final UUID id;

  public Profile(String name, String version, int protocol, UUID id) {
    this.name = name;
    this.version = version;
    this.protocol = protocol;
    this.key = Key.NONE;
    this.id = id;
  }

  public String name() {
    return this.name;
  }

  public String version() {
    return this.version;
  }

  public int protocol() {
    return this.protocol;
  }

  public UUID id() {
    return this.id;
  }

  public Key key() {
    if (this.key == null)
      return Key.NONE;

    // Gson creates a new Key instance (and loses the MouseButton type), use the registered one instead
    Key registered = Key.getByName(this.key.getActualName());
    if (registered != null && registered != this.key) {
      this.key = registered;
    }
    return this.key;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setKey(Key key) {
    this.key = key;
  }
}
