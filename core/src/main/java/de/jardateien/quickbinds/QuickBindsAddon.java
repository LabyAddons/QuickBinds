package de.jardateien.quickbinds;

import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;
import de.jardateien.quickbinds.config.QuickBindsConfiguration;

@AddonMain
public class QuickBindsAddon extends LabyAddon<QuickBindsConfiguration> {

  private static QuickBindsAddon instance;

  @Override
  protected void enable() {
    instance = this;

    this.registerSettingCategory();

    this.logger().info("Enabled the Addon");
  }

  @Override
  protected Class<QuickBindsConfiguration> configurationClass() {
    return QuickBindsConfiguration.class;
  }
}
