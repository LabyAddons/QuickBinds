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
    return this.key;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setKey(Key key) {
    this.key = key;
  }
}
