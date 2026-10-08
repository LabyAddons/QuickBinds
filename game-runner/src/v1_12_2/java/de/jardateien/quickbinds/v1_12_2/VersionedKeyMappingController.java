package de.jardateien.quickbinds.v1_12_2;

import de.jardateien.quickbinds.api.KeyMappingController;
import javax.inject.Singleton;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.key.MouseButton;
import net.labymod.api.client.gui.screen.key.mapper.KeyMapper;
import net.labymod.api.models.Implements;

@Singleton
@Implements(KeyMappingController.class)
public class VersionedKeyMappingController implements KeyMappingController {

  private static final String UNASSIGNED = "0";
  private static final int MOUSE_OFFSET = 100;

  @Override
  public Key toKey(String value) {
    int keyCode;
    try {
      keyCode = Integer.parseInt(value.trim());
    } catch (NumberFormatException e) {
      return null;
    }

    if (keyCode == 0)
      return Key.NONE;

    // Mouse buttons are stored as button - 100
    if (keyCode < 0)
      return KeyMapper.getMouseButton(keyCode + MOUSE_OFFSET);

    return KeyMapper.getKey(keyCode);
  }

  @Override
  public String toOptionValue(Key key) {
    if (key == Key.NONE)
      return UNASSIGNED;

    int keyCode = KeyMapper.getKeyCode(key);
    if (keyCode < 0)
      return null;

    if (key instanceof MouseButton)
      return String.valueOf(keyCode - MOUSE_OFFSET);

    return keyCode == 0 ? null : String.valueOf(keyCode);
  }
}
