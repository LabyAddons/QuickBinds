package de.jardateien.quickbinds.api;

import net.labymod.api.reference.annotation.Referenceable;
import java.nio.file.Path;

@Referenceable
public interface SettingController {

  void save(Path profile);

}
