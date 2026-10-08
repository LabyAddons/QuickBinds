package de.jardateien.quickbinds.v1_12_2;

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
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.GameSettings;

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

    Minecraft minecraft = Minecraft.getMinecraft();
    GameSettings gameSettings = minecraft.gameSettings;
    ResourcePackRepository resourcePackRepository = minecraft.getResourcePackRepository();
    List<String> previousResourcePacks = this.getNames(resourcePackRepository.getRepositoryEntries());

    gameSettings.loadOptions();
    resourcePackRepository.updateRepositoryEntriesAll();

    List<ResourcePackRepository.Entry> selectedResourcePacks = new ArrayList<>();
    for(String resourcePack : gameSettings.resourcePacks) {
      for(ResourcePackRepository.Entry entry : resourcePackRepository.getRepositoryEntriesAll()) {
        if(entry.getResourcePackName().equals(resourcePack)) {
          selectedResourcePacks.add(entry);
          break;
        }
      }
    }

    resourcePackRepository.setRepositories(selectedResourcePacks);
    gameSettings.saveOptions();

    if(!previousResourcePacks.equals(this.getNames(selectedResourcePacks))) {
      minecraft.refreshResources();
    }
  }

  private List<String> getNames(List<ResourcePackRepository.Entry> entries) {
    List<String> names = new ArrayList<>();
    for(ResourcePackRepository.Entry entry : entries) {
      names.add(entry.getResourcePackName());
    }
    return names;
  }
}
