package de.jardateien.quickbinds.ui.popup;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.CheckBoxWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.CheckBoxWidget.State;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.popup.SimpleAdvancedPopup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Link("name-checkbox.lss")
public class EnterNameCheckboxPopup extends SimpleAdvancedPopup {

  private EnterNameCheckboxPopup(Builder builder) {
    super.title = builder.title;

    VerticalListWidget<Widget> container = new VerticalListWidget<>().addId("name-checkbox-container");

    TextFieldWidget inputPreview = new TextFieldWidget().addId("input-preview");
    inputPreview.setText(builder.text);
    container.addChild(inputPreview);

    VerticalListWidget<Widget> checkboxList = new VerticalListWidget<>().addId("checkbox-list");
    Map<String, CheckBoxWidget> checkBoxes = new LinkedHashMap<>();

    for(Option option : builder.options) {
      HorizontalListWidget row = new HorizontalListWidget().addId("checkbox-row");

      CheckBoxWidget checkBox = new CheckBoxWidget().addId("checkbox");
      checkBox.setState(option.checked ? State.CHECKED : State.UNCHECKED);
      row.addEntry(checkBox);
      row.addEntry(ComponentWidget.component(option.label).addId("checkbox-label"));

      checkboxList.addChild(row);
      checkBoxes.put(option.key, checkBox);
    }

    container.addChild(checkboxList);

    super.widgetFunction = widgetVerticalListWidget -> widgetVerticalListWidget.addChild(container);

    SimplePopupButton confirm = SimplePopupButton.create(Component.translatable("labymod.ui.button.done"), simplePopupButton -> {
      Map<String, Boolean> values = new LinkedHashMap<>();
      checkBoxes.forEach((key, checkBox) -> values.put(key, checkBox.state() == State.CHECKED));
      builder.callback.accept(new Result(inputPreview.getText(), values));
    });

    super.buttons = new ArrayList<>();
    super.buttons.add(confirm);

    super.displayInOverlay();
  }

  public static Builder create() {
    return new Builder();
  }

  public static class Builder {

    private Component title = Component.translatable("quickbinds.popup.rename.title");
    private String text = "";
    private final List<Option> options = new ArrayList<>();
    private Consumer<Result> callback = result -> {};

    private Builder() {}

    public Builder title(Component title) {
      this.title = title;
      return this;
    }

    public Builder text(String text) {
      this.text = text;
      return this;
    }

    public Builder option(String key, Component label) {
      return this.option(key, label, false);
    }

    public Builder option(String key, Component label, boolean checked) {
      this.options.add(new Option(key, label, checked));
      return this;
    }

    public void open(Consumer<Result> callback) {
      this.callback = callback;
      new EnterNameCheckboxPopup(this);
    }

  }

  private record Option(String key, Component label, boolean checked) {}

  public record Result(String name, Map<String, Boolean> checkboxes) {

    public Result {
      checkboxes = Collections.unmodifiableMap(checkboxes);
    }

    public boolean isChecked(String key) {
      return this.checkboxes.getOrDefault(key, false);
    }

  }

}
