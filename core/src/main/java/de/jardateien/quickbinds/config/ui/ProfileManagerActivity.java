package de.jardateien.quickbinds.config.ui;

import de.jardateien.quickbinds.QuickBindsAddon;
import de.jardateien.quickbinds.api.Profile;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;

@Link("profile-manager.lss")
@AutoActivity
public class ProfileManagerActivity extends Activity {

  private final VerticalListWidget<ProfileWidget> verticalListWidget = new VerticalListWidget<>();

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    FlexibleContentWidget flexibleContentWidget = new FlexibleContentWidget();

    for (Profile profile : QuickBindsAddon.referenceStorage().profileController().profiles()) {
      this.verticalListWidget.addChild(new ProfileWidget(profile));
    }

    flexibleContentWidget.addFlexibleContent(this.verticalListWidget);
    this.document.addChild(flexibleContentWidget);
  }

}
