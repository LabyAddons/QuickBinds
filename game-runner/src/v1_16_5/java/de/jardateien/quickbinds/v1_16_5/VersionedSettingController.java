package de.jardateien.quickbinds.v1_16_5;

import de.jardateien.quickbinds.api.SettingController;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.inject.Singleton;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;

@Singleton
@Implements(SettingController.class)
public class VersionedSettingController implements SettingController {

  @Override
  public void save(Path profile) {
    try {
      List<String> strings = Files.readAllLines(profile);
      Files.write(Paths.get("options.txt"), strings);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Minecraft.getInstance().options.load();
    Minecraft.getInstance().options.save();
  }
}
