package de.jardateien.quickbinds.v1_19_4;

import com.mojang.blaze3d.platform.InputConstants;
import de.jardateien.quickbinds.api.KeyMappingController;
import javax.inject.Singleton;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.key.MouseButton;
import net.labymod.api.client.gui.screen.key.mapper.KeyMapper;
import net.labymod.api.models.Implements;

@Singleton
@Implements(KeyMappingController.class)
public class VersionedKeyMappingController implements KeyMappingController {

  @Override
  public Key toKey(String value) {
    InputConstants.Key key;
    try {
      key = InputConstants.getKey(value);
    } catch (RuntimeException e) {
      return null;
    }

    if (key.equals(InputConstants.UNKNOWN))
      return Key.NONE;

    if (key.getType() == InputConstants.Type.MOUSE)
      return KeyMapper.getMouseButton(key.getValue());

    if (key.getType() == InputConstants.Type.KEYSYM)
      return KeyMapper.getKey(key.getValue());

    return null;
  }

  @Override
  public String toOptionValue(Key key) {
    if (key == Key.NONE)
      return InputConstants.UNKNOWN.getName();

    int keyCode = KeyMapper.getKeyCode(key);
    if (keyCode < 0)
      return null;

    InputConstants.Type type = key instanceof MouseButton ? InputConstants.Type.MOUSE : InputConstants.Type.KEYSYM;
    return type.getOrCreate(keyCode).getName();
  }
}
