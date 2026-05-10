package de.jardateien.quickbinds.listeners;

import de.jardateien.quickbinds.QuickBindsAddon;
import de.jardateien.quickbinds.config.QuickBindsConfiguration;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.window.Window;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.input.KeyEvent;
import net.labymod.api.event.client.input.KeyEvent.State;

public class KeybindListener {

  private final QuickBindsConfiguration config;

  public KeybindListener(QuickBindsAddon addon) {
    this.config = addon.configuration();
  }

  @Subscribe
  public void onKeyPressed(final KeyEvent keyEvent) {
    if(this.config.keybind().get() != keyEvent.key() || keyEvent.state() != State.PRESS)
      return;

    Window window = Laby.labyAPI().minecraft().minecraftWindow();
    if(window.isScreenOpened())
      return;

    window.displayScreen(this.config.profiles());
  }
}
