package de.jardateien.quickbinds.ui.activity;

import de.jardateien.quickbinds.QuickBindsAddon;
import de.jardateien.quickbinds.api.Profile;
import de.jardateien.quickbinds.api.ProfileController;
import de.jardateien.quickbinds.ui.popup.EnterNamePopup;
import net.labymod.api.Textures.SpriteCommon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;

@AutoWidget
public class ProfileWidget extends HorizontalListWidget {

  private final ProfileController profileController;
  private final Profile profile;
  private final ProfileManagerActivity activity;

  public ProfileWidget(Profile profile, ProfileManagerActivity activity) {
    this.profile = profile;
    this.profileController = QuickBindsAddon.referenceStorage().profileController();
    this.activity = activity;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    ComponentWidget name = ComponentWidget.text(this.profile.name() + " §7(" + this.profile.version()+")").addId("profile-name");

    HorizontalListWidget buttons = new HorizontalListWidget();
    buttons.addId("buttons");

    ButtonWidget selectProfile = ButtonWidget.icon(SpriteCommon.GREEN_CHECKED, () -> this.profileController.loadProfile(this.profile.id()));
    ButtonWidget renameButton = ButtonWidget.icon(SpriteCommon.PAINT, () ->
        new EnterNamePopup(renameProfile -> {
          if(renameProfile.isBlank())
            return;

          this.profileController.renameProfile(this.profile.id(), renameProfile);
          this.activity.reload();
        })
    );

    ButtonWidget deleteProfile = ButtonWidget.icon(SpriteCommon.TRASH, () -> {
      this.profileController.deleteProfile(this.profile.id());
      this.activity.reload();
    });

    buttons.addEntry(selectProfile);
    buttons.addEntry(renameButton);
    buttons.addEntry(deleteProfile);

    this.addEntry(name);
    this.addEntry(buttons);
  }
}
