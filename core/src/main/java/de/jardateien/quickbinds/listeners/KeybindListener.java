package de.jardateien.quickbinds.listeners;

import de.jardateien.quickbinds.QuickBindsAddon;
import de.jardateien.quickbinds.api.Profile;
import de.jardateien.quickbinds.api.ProfileController;
import de.jardateien.quickbinds.config.QuickBindsConfiguration;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.input.KeyEvent.State;

public class KeybindListener {

  private final QuickBindsConfiguration config;
  private final ProfileController profileController;

  public KeybindListener(QuickBindsAddon addon) {
    this.config = addon.configuration();
    this.profileController = QuickBindsAddon.referenceStorage().profileController();
  }

  @Subscribe
  public void onKeyPressed(final KeyEvent keyEvent) {
    if(!this.config.enabled().get() || keyEvent.state() != State.PRESS)
      return;

    // Ignore hotkeys while a screen (chat, sign, book, settings) is open
    if(Laby.labyAPI().minecraft().minecraftWindow().isScreenOpened())
      return;

    Key settingKey = this.config.keybind().get();
    if(settingKey == null || settingKey.isUnknown() || !settingKey.isPressed())
      return;

    for (Profile profile : this.profileController.profiles()) {
      Key key = profile.key();
      if(key == null || key.isUnknown() || !key.equals(keyEvent.key()))
        continue;

      this.profileController.loadProfile(profile.id());
    }
  }

}
