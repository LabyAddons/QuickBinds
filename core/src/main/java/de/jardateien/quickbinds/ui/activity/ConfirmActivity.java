package de.jardateien.quickbinds.ui.activity;

import java.util.function.Consumer;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.activity.types.TitledActivity;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;

@Link("confirm.lss")
@AutoActivity
public class ConfirmActivity extends TitledActivity {

  private final Component description;
  private final Component confirmText;
  private final Component cancelText;

  private final Consumer<Boolean> responseConsumer;
  private boolean fired;

  protected ComponentWidget descriptionWidget;
  protected HorizontalListWidget buttonRow;

  private ConfirmActivity(Component title, Component description, Consumer<Boolean> responseConsumer) {
    super(title);

    this.description = description;
    this.confirmText = Component.translatable("gui.yes");
    this.cancelText = Component.translatable("gui.no");
    this.responseConsumer = responseConsumer;
  }

  public static void confirm(Component title, Component description, Consumer<Boolean> responseConsumer) {
    ConfirmActivity activity = new ConfirmActivity(title, description, responseConsumer);

    Laby.labyAPI().minecraft().minecraftWindow().displayScreen(activity);
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    if (this.description != null) {
      this.descriptionWidget = ComponentWidget.component(this.description);
      this.descriptionWidget.addId("description");
      super.document.addChild(this.descriptionWidget);
    }

    this.buttonRow = new HorizontalListWidget();
    this.buttonRow.addId("button-row");

    this.buttonRow.addEntry(ButtonWidget.component(this.confirmText, () -> this.clicked(true)));
    this.buttonRow.addEntry(ButtonWidget.component(this.cancelText, () -> this.clicked(false)));

    super.document.addChild(this.buttonRow);
  }

  private void clicked(boolean accepted) {
    this.accept(accepted);
    super.displayPreviousScreen();
  }

  private void accept(Boolean response) {
    if (!this.fired) {
      this.fired = true;
      this.responseConsumer.accept(response);
    }
  }

  @Override
  public void onCloseScreen() {
    super.onCloseScreen();

    this.accept(null);
  }

}