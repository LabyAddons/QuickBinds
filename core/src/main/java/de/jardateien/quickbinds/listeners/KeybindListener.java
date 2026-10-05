package de.jardateien.quickbinds.listeners;

import de.jardateien.quickbinds.QuickBindsAddon;
import de.jardateien.quickbinds.api.Profile;
import de.jardateien.quickbinds.api.ProfileController;
import de.jardateien.quickbinds.config.QuickBindsConfiguration;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;

public class KeybindListener {

  private final QuickBindsConfiguration config;
  private final ProfileController profileController;

  public KeybindListener(QuickBindsAddon addon) {
    this.config = addon.configuration();
    this.profileController = QuickBindsAddon.referenceStorage().profileController();
  }

  @Subscribe
  public void onKeyPressed(final KeyEvent keyEvent) {
    Key settingKey = this.config.keybind().get();
    if(settingKey == null || settingKey.isUnknown() || !settingKey.isPressed())
      return;

    for (Profile profile : this.profileController.profiles()) {
      Key key = profile.key();
      if(key == null || key.isUnknown())
        continue;

      if (!key.isPressed())
        continue;

      this.profileController.loadProfile(profile.id());
    }
  }

}
