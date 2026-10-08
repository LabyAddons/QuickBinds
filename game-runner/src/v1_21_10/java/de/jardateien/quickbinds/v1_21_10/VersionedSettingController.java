package de.jardateien.quickbinds.v1_21_10;

import de.jardateien.quickbinds.api.SettingController;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import net.labymod.api.models.Implements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.server.packs.repository.PackRepository;

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

    Minecraft minecraft = Minecraft.getInstance();
    Options options = minecraft.options;
    PackRepository resourcePackRepository = minecraft.getResourcePackRepository();
    List<String> previousResourcePacks = new ArrayList<>(resourcePackRepository.getSelectedIds());

    options.load();
    resourcePackRepository.reload();
    options.loadSelectedResourcePacks(resourcePackRepository);
    options.save();

    if(!previousResourcePacks.equals(new ArrayList<>(resourcePackRepository.getSelectedIds()))) {
      minecraft.reloadResourcePacks();
    }
  }
}
