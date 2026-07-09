package de.jardateien.quickbinds;

import de.jardateien.quickbinds.api.generated.ReferenceStorage;
import de.jardateien.quickbinds.listeners.KeybindListener;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;
import de.jardateien.quickbinds.config.QuickBindsConfiguration;

@AddonMain
public class QuickBindsAddon extends LabyAddon<QuickBindsConfiguration> {

  public static QuickBindsAddon instance;

  @Override
  protected void enable() {
    instance = this;
    this.registerSettingCategory();
    this.registerListener(new KeybindListener(this));
  }

  @Override
  protected Class<QuickBindsConfiguration> configurationClass() {
    return QuickBindsConfiguration.class;
  }

  public static ReferenceStorage referenceStorage() {
    return instance.referenceStorageAccessor();
  }

}
