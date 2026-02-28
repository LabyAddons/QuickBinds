package de.jardateien.quickbinds.config;

import de.jardateien.quickbinds.config.ui.ProfileManagerActivity;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.widget.widgets.activity.settings.ActivitySettingWidget.ActivitySetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.util.MethodOrder;

@ConfigName("settings")
public class QuickBindsConfiguration extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @MethodOrder(after = "enabled")
  @ActivitySetting
  public Activity profiles() {
    return new ProfileManagerActivity();
  }

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }


}
