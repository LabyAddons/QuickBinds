package de.jardateien.quickbinds.v1_21_10;

import de.jardateien.quickbinds.api.SettingController;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Singleton
@Implements(SettingController.class)
public class VersionedSettingController implements SettingController {

  @Override
  public void save(Path profile) {
    Minecraft.getInstance().options.load();
    try {
      Files.copy(profile, Paths.get("options.txt"), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Minecraft.getInstance().options.save();
  }
}
