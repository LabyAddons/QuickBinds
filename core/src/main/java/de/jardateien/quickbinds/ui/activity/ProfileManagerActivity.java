package de.jardateien.quickbinds.ui.activity;

import de.jardateien.quickbinds.QuickBindsAddon;
import de.jardateien.quickbinds.api.Profile;
import de.jardateien.quickbinds.api.ProfileController;
import de.jardateien.quickbinds.ui.popup.EnterNamePopup;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;

@Link("profile-manager.lss")
@AutoActivity
public class ProfileManagerActivity extends Activity {

  private final VerticalListWidget<ProfileWidget> verticalListWidget = new VerticalListWidget<>().addId("container");
  private final ProfileController profileController;

  public ProfileManagerActivity() {
    this.profileController = QuickBindsAddon.referenceStorage().profileController();
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    for (Profile profile : this.profileController.profiles()) {
      this.verticalListWidget.addChild(new ProfileWidget(profile, this));
    }

    ButtonWidget buttonWidget = ButtonWidget.i18n("labymod.ui.button.add", () -> new EnterNamePopup((profile) -> {
      if(profile.isBlank())
        return;

      this.profileController.saveCurrentProfile(profile);
      this.reload();
    }));

    buttonWidget.setHoverComponent(Component.translatable("quickbinds.settings.profiles.button.tooltip"));

    if(this.profileController.profiles().size() >= 14) {
      buttonWidget.setEnabled(false);
    }

    buttonWidget.addId("profile-add-button");

    this.document.addChild(this.verticalListWidget);
    this.document.addChild(buttonWidget);
  }

}
