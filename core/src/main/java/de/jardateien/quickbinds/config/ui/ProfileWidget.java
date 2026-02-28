package de.jardateien.quickbinds.config.ui;

import de.jardateien.quickbinds.api.Profile;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.resources.ResourceLocation;

@AutoWidget
public class ProfileWidget extends HorizontalListWidget {

  private final Profile profile;

  public ProfileWidget(Profile profile) {
    this.profile = profile;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    ComponentWidget name = ComponentWidget.text(this.profile.name()).addId("profile-name");
    ButtonWidget selectProfile = ButtonWidget.icon(Icon.texture(ResourceLocation.create("", "")));
    ButtonWidget editProfile = ButtonWidget.icon(Icon.texture(ResourceLocation.create("", "")));
    ButtonWidget deleteProfile = ButtonWidget.icon(Icon.texture(ResourceLocation.create("", "")));

    this.addEntry(name);
    this.addEntry(selectProfile);
    this.addEntry(editProfile);
    this.addEntry(deleteProfile);
  }
}
