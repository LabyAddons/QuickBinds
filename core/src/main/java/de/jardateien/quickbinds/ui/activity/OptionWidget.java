package de.jardateien.quickbinds.ui.activity;

import net.labymod.api.Textures.SpriteCommon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.KeybindWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import java.util.List;

@AutoWidget
public class OptionWidget extends HorizontalListWidget {

  private static final int MAX_TEXT_LENGTH = 32767;

  private final OptionEntry entry;
  private final ProfileOptionsActivity activity;

  private ComponentWidget nameWidget;
  private KeybindWidget keybindWidget;
  private SwitchWidget switchWidget;
  private SliderWidget sliderWidget;
  private TextFieldWidget textFieldWidget;
  private ButtonWidget revertButton;

  public OptionWidget(OptionEntry entry, ProfileOptionsActivity activity) {
    this.entry = entry;
    this.activity = activity;
    this.addId("option-row");
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    this.nameWidget = ComponentWidget.empty().addId("option-name");

    HorizontalListWidget controls = new HorizontalListWidget();
    controls.addId("option-controls");

    this.revertButton = ButtonWidget.icon(SpriteCommon.REFRESH, () -> this.activity.revert(this));
    this.revertButton.addId("option-revert");
    this.revertButton.setHoverComponent(Component.translatable("quickbinds.ui.options.revert"));

    controls.addEntry(this.createValueWidget());
    controls.addEntry(this.revertButton);

    this.addEntry(this.nameWidget);
    this.addEntry(controls);

    this.refresh();
  }

  private Widget createValueWidget() {
    switch (this.entry.type()) {
      case KEYBIND:
        this.keybindWidget = new KeybindWidget(key -> this.activity.select(this, key));
        this.keybindWidget.addId("option-value");
        this.showValue();
        return this.keybindWidget;
      case BOOLEAN:
        this.switchWidget = SwitchWidget.create(value -> this.activity.change(this, String.valueOf(value)));
        this.switchWidget.addId("option-value", "option-switch");
        this.showValue();
        return this.switchWidget;
      case SLIDER:
        OptionSlider slider = this.entry.slider();
        boolean integer = OptionEntry.INTEGER_VALUE.matcher(this.entry.loadedValue()).matches();
        this.sliderWidget = new SliderWidget(slider.steps(), value -> this.activity.change(this, slider.toOptionValue(value, integer)));
        this.sliderWidget.range(slider.min(), slider.max());
        this.sliderWidget.withFormatter(slider::label);
        this.sliderWidget.addId("option-value");
        this.showValue();
        return this.sliderWidget;
      default:
        this.textFieldWidget = new TextFieldWidget();
        this.textFieldWidget.addId("option-value");
        this.textFieldWidget.maximalLength(MAX_TEXT_LENGTH);
        this.showValue();

        if (this.entry.type() == OptionEntry.Type.INTEGER) {
          this.textFieldWidget.validator(text -> OptionEntry.INTEGER_INPUT.matcher(text).matches());
        } else if (this.entry.type() == OptionEntry.Type.DECIMAL) {
          this.textFieldWidget.validator(text -> OptionEntry.DECIMAL_INPUT.matcher(text).matches());
        } else if (this.entry.type() == OptionEntry.Type.READ_ONLY) {
          this.textFieldWidget.setEditable(false);
        }

        this.textFieldWidget.updateListener(text -> this.activity.change(this, text));
        return this.textFieldWidget;
    }
  }

  public OptionEntry entry() {
    return this.entry;
  }

  /**
   * Shows the current value of the entry without reporting it as a change.
   */
  public void showValue() {
    if (this.keybindWidget != null && this.entry.key() != null) {
      this.keybindWidget.key(this.entry.key());
    } else if (this.switchWidget != null) {
      this.switchWidget.setValue(Boolean.parseBoolean(this.entry.value()));
    } else if (this.sliderWidget != null) {
      this.sliderWidget.setValue(Float.parseFloat(this.entry.value()), false);
    } else if (this.textFieldWidget != null) {
      this.textFieldWidget.setText(this.entry.value(), true);
    }
  }

  /**
   * Updates the name color, hover and revert button. Only called when an option changed, never per
   * frame.
   */
  public void refresh() {
    if (this.nameWidget == null)
      return;

    Component hover = Component.text(this.entry.optionKey(), NamedTextColor.GRAY);
    List<OptionEntry> conflicts = this.entry.conflicts();

    if (this.entry.isInvalid()) {
      this.nameWidget.setComponent(Component.text(this.entry.name(), NamedTextColor.GOLD));
      hover = hover.append(Component.newline()).append(Component.translatable("quickbinds.ui.options.invalid", NamedTextColor.GOLD, Component.text(this.entry.value())));
    } else if (!conflicts.isEmpty()) {
      StringBuilder names = new StringBuilder();
      for (OptionEntry conflict : conflicts) {
        if (names.length() > 0) {
          names.append(", ");
        }
        names.append(conflict.name());
      }

      this.nameWidget.setComponent(Component.text(this.entry.name(), NamedTextColor.RED));
      hover = hover.append(Component.newline()).append(Component.translatable("quickbinds.ui.options.conflict", NamedTextColor.RED, Component.text(names.toString())));
    } else if (this.entry.isChanged()) {
      this.nameWidget.setComponent(Component.text(this.entry.name(), NamedTextColor.YELLOW));
    } else {
      this.nameWidget.setComponent(Component.text(this.entry.name()));
    }

    this.nameWidget.setHoverComponent(hover);
    this.revertButton.setEnabled(this.entry.isChanged());
  }
}
