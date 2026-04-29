package de.jardateien.quickbinds.ui.popup;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.popup.SimpleAdvancedPopup;
import java.util.ArrayList;
import java.util.function.Consumer;

@Link("name.lss")
public class EnterNamePopup extends SimpleAdvancedPopup {

  public EnterNamePopup(Consumer<String> rename) {
    super.title = Component.translatable("quickbinds.popup.rename.title");

    DivWidget inputContainer = new DivWidget();
    inputContainer.addId("input-container");

    TextFieldWidget inputPreview = new TextFieldWidget().addId("input-preview");
    inputContainer.addChild(inputPreview);

    super.widgetFunction = widgetVerticalListWidget -> widgetVerticalListWidget.addChild(inputContainer);

    SimplePopupButton confirm = SimplePopupButton.create(Component.translatable("labymod.ui.button.done"), simplePopupButton ->
        rename.accept(inputPreview.getText())
    );

    super.buttons = new ArrayList<>();
    super.buttons.add(confirm);

    super.displayInOverlay();
  }
}