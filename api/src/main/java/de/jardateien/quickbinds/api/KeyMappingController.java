package de.jardateien.quickbinds.api;

import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.reference.annotation.Referenceable;
import org.jetbrains.annotations.Nullable;

@Referenceable
public interface KeyMappingController {

  /**
   * @param value a key value as it is stored in a profile's options.txt
   * @return the matching key, {@link Key#NONE} if unassigned or null if the value is invalid
   */
  @Nullable
  Key toKey(String value);

  /**
   * @param key the key to convert
   * @return the value to store in a profile's options.txt or null if the key can't be stored
   */
  @Nullable
  String toOptionValue(Key key);

}
