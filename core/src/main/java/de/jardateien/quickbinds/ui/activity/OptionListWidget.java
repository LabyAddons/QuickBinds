package de.jardateien.quickbinds.ui.activity;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.component.format.TextDecoration;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists the options of the opened section matching the current search grouped by category. Rebuilt with
 * {@link #reInitialize()} from the cached entries, the profile's options.txt is never read again.
 */
@AutoWidget
public class OptionListWidget extends VerticalListWidget<Widget> {

  private final ProfileOptionsActivity activity;
  private final List<OptionWidget> rows = new ArrayList<>();

  public OptionListWidget(ProfileOptionsActivity activity) {
    this.activity = activity;
    this.addId("options-list");
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);
    this.rows.clear();

    OptionSection section = this.activity.section();
    String query = this.activity.query();
    OptionCategory category = null;
    for (OptionEntry entry : this.activity.entries()) {
      if (entry.category().section() != section || !entry.matches(query))
        continue;

      if (section.hasCategories() && entry.category() != category) {
        category = entry.category();
        this.addChild(ComponentWidget.component(Component.translatable(category.translationKey(), NamedTextColor.YELLOW, TextDecoration.BOLD)).addId("category"));
      }

      OptionWidget row = new OptionWidget(entry, this.activity);
      this.rows.add(row);
      this.addChild(row);
    }

    if (this.rows.isEmpty()) {
      this.addChild(ComponentWidget.component(Component.translatable("quickbinds.ui.options.empty", NamedTextColor.GRAY)).addId("empty"));
    }
  }

  public void refreshRows() {
    for (OptionWidget row : this.rows) {
      row.refresh();
    }
  }
}
