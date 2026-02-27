package de.jardateien.quickbinds.config;

import de.jardateien.quickbinds.utils.KeybindProfile;
import net.labymod.api.Laby;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.Minecraft;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.Exclude;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.util.MethodOrder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ConfigName("settings")
public class QuickBindsConfiguration extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @MethodOrder(after = "enabled")
  @ButtonSetting
  public void saveProfile() {
    Path optionsFile = Paths.get("options.txt");
    if(!Files.exists(optionsFile)) {
      Laby.references().chatExecutor().chat("[QuickBinds] - " + optionsFile + " does not exist.");
      return;
    }

    try {
      List<String> paths = Files.readAllLines(optionsFile);
      KeybindProfile profile = new KeybindProfile(UUID.randomUUID().toString());

      paths.forEach(profile::addKeybind);

      this.profiles.add(profile);
      Laby.references().chatExecutor().chat("[QuickBinds] - " + profile.profile() + " wurde erstellt!");
    } catch (IOException e) {
      Laby.references().chatExecutor().chat(e.getMessage());
    }
  }

  @MethodOrder(after = "saveProfile")
  @ButtonSetting
  public void loadProfile() {
    if (this.profiles.isEmpty()) {
      Laby.references().chatExecutor().chat("[QuickBinds] - No profiles found.");
      return;
    }

    Path optionsFile = Paths.get("options.txt");
    if(!Files.exists(optionsFile)) {
      Laby.references().chatExecutor().chat("[QuickBinds] - " + optionsFile + " does not exist.");
      return;
    }

    try {
      Files.write(optionsFile, this.profiles.removeLast().keyboard(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    } catch (IOException e) {
      Laby.references().chatExecutor().chat(e.getMessage());
    }

    Laby.labyAPI().minecraft().options().save();

    this.profiles.forEach(profile -> Laby.references().chatExecutor().chat(profile.profile()));
  }

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

  @Exclude
  private final List<KeybindProfile> profiles = new ArrayList<>();
  public List<KeybindProfile> profiles() {
    return this.profiles;
  }
}
