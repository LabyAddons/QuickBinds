package de.jardateien.quickbinds.listeners;

import de.jardateien.quickbinds.QuickBindsAddon;
import de.jardateien.quickbinds.config.QuickBindsConfiguration;
import de.jardateien.quickbinds.ui.activity.ProfileManagerActivity;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.screen.key.Key;
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
    Key key = keyEvent.key();
    if(!key.isPressed() || keyEvent.state() != State.PRESS || !this.config.keybind().get().equals(key)) return;

    Laby.labyAPI().minecraft().minecraftWindow().displayScreen(new ProfileManagerActivity());
  }
}
