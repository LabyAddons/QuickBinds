package de.jardateien.quickbinds.ui.activity.options;

import de.jardateien.quickbinds.QuickBindsAddon;
import de.jardateien.quickbinds.api.KeyMappingController;
import de.jardateien.quickbinds.api.Profile;
import de.jardateien.quickbinds.api.ProfileController;
import de.jardateien.quickbinds.api.ProfileOptions;
import net.labymod.api.Laby;
import net.labymod.api.client.Minecraft;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.NamedTextColor;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.activity.AutoActivity;
import net.labymod.api.client.gui.screen.activity.Link;
import net.labymod.api.client.gui.screen.key.InputType;
import net.labymod.api.client.gui.screen.key.Key;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.DivWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.ScrollWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.HorizontalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.list.VerticalListWidget;
import net.labymod.api.client.gui.screen.widget.widgets.popup.SimpleAdvancedPopup;
import net.labymod.api.client.gui.screen.widget.widgets.popup.SimpleAdvancedPopup.SimplePopupButton;
import net.labymod.api.util.I18n;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Edits all options of one QuickBinds profile, split into the sections of the vanilla options screen. The profile's own options.txt (see
 * {@link ProfileController#loadOptions}) is read once when the editor opens and written back only
 * when "Done" is pressed. Minecraft's global options.txt is never touched.
 */
@Link("profile-options.lss")
@AutoActivity
public class ProfileOptionsActivity extends Activity {

  private static final Comparator<OptionEntry> ORDER = Comparator.<OptionEntry>comparingInt(entry -> entry.category().ordinal()).thenComparingInt(OptionEntry::order);

  private final Profile profile;
  private final ProfileController profileController;
  private final KeyMappingController keyMappingController;
  private final List<OptionEntry> entries = new ArrayList<>();

  private ProfileOptions options;
  private String errorKey;
  private String query = "";
  private OptionSection section;

  private OptionListWidget listWidget;
  private ScrollWidget scrollWidget;

  private ProfileOptionsActivity(Profile profile) {
    this.profile = profile;
    this.profileController = QuickBindsAddon.referenceStorage().profileController();
    this.keyMappingController = QuickBindsAddon.referenceStorage().keyMappingController();
    this.load();
  }

  public static void open(Profile profile) {
    Laby.labyAPI().minecraft().minecraftWindow().displayScreen(new ProfileOptionsActivity(profile));
  }

  private void load() {
    try {
      this.options = this.profileController.loadOptions(this.profile.id());

      for (String optionKey : this.options.keys()) {
        String value = this.options.get(optionKey);
        boolean keybind = optionKey.startsWith(OptionCategory.KEY_PREFIX);
        String categoryName = I18n.translate(OptionCategory.of(optionKey).translationKey());
        Key key = keybind ? this.keyMappingController.toKey(value) : null;

        this.entries.add(new OptionEntry(optionKey, value, displayName(optionKey), categoryName, key));
      }

      this.entries.sort(ORDER);
      this.updateConflicts();
    } catch (NoSuchFileException e) {
      this.errorKey = "quickbinds.ui.options.error.missing";
    } catch (IOException | RuntimeException e) {
      QuickBindsAddon.instance.logger().error("Failed to load the options of profile " + this.profile.id(), e);
      this.errorKey = "quickbinds.ui.options.error.load";
    }
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    DivWidget header = new DivWidget().addId("header");
    DivWidget footer = new DivWidget().addId("footer");
    HorizontalListWidget buttonRow = new HorizontalListWidget().addId("button-row");

    Component profileName = Component.text(this.profile.name());
    Component title = this.section == null
        ? Component.translatable("quickbinds.ui.options.title", profileName)
        : Component.translatable("quickbinds.ui.options.sectionTitle", Component.translatable(this.section.translationKey()), profileName);
    header.addChild(ComponentWidget.component(title).addId("title"));

    if (this.errorKey != null) {
      this.document.addChild(ComponentWidget.component(Component.translatable(this.errorKey, NamedTextColor.RED)).addId("error"));
      buttonRow.addEntry(ButtonWidget.i18n("quickbinds.ui.options.back", this::displayPreviousScreen));

      footer.addChild(buttonRow);
      this.document.addChild(header);
      this.document.addChild(footer);
      return;
    }

    if (this.profile.protocol() != Laby.labyAPI().minecraft().getProtocolVersion()) {
      footer.addChild(ComponentWidget.component(Component.translatable("quickbinds.ui.options.version", NamedTextColor.GOLD, Component.text(this.profile.version()))).addId("warning"));
    }

    DivWidget container = new DivWidget().addId("options-container");

    if (this.section == null) {
      VerticalListWidget<Widget> menu = new VerticalListWidget<>().addId("menu");
      OptionSection[] sections = OptionSection.values();
      HorizontalListWidget row = null;
      for (int index = 0; index < sections.length; index++) {
        if (index % 2 == 0) {
          row = new HorizontalListWidget().addId("menu-row");
          menu.addChild(row);
        }
        row.addEntry(this.sectionButton(sections[index]));
      }
      container.addChild(menu);

      footer.addChild(ComponentWidget.component(Component.translatable("quickbinds.ui.options.menuHint", NamedTextColor.GRAY)).addId("hint"));
      buttonRow.addEntry(ButtonWidget.i18n("quickbinds.ui.options.cancel", this::displayPreviousScreen));
      buttonRow.addEntry(ButtonWidget.i18n("quickbinds.ui.options.done", this::save));
    } else {
      TextFieldWidget searchField = new TextFieldWidget().addId("search");
      searchField.placeholder(Component.translatable("quickbinds.ui.options.search"));
      searchField.setText(this.query);
      searchField.updateListener(this::search);
      header.addChild(searchField);

      this.listWidget = new OptionListWidget(this);
      this.scrollWidget = new ScrollWidget(this.listWidget).addId("options-scroll");
      container.addChild(this.scrollWidget);

      footer.addChild(ComponentWidget.component(Component.translatable("quickbinds.ui.options.hint", NamedTextColor.GRAY)).addId("hint"));
      buttonRow.addEntry(ButtonWidget.i18n("quickbinds.ui.options.back", () -> this.openSection(null)));
    }

    footer.addChild(buttonRow);

    this.document.addChild(header);
    this.document.addChild(container);
    this.document.addChild(footer);
  }

  private ButtonWidget sectionButton(OptionSection section) {
    int count = 0;
    boolean changed = false;
    for (OptionEntry entry : this.entries) {
      if (entry.category().section() != section)
        continue;

      count++;
      changed |= entry.isChanged();
    }

    Component text = changed
        ? Component.translatable(section.translationKey(), NamedTextColor.YELLOW).append(Component.text("*", NamedTextColor.YELLOW))
        : Component.translatable(section.translationKey());

    ButtonWidget button = ButtonWidget.component(text, () -> this.openSection(section));
    button.addId("menu-button");
    button.setHoverComponent(Component.translatable("quickbinds.ui.options.count", Component.text(count)));
    button.setEnabled(count > 0);
    return button;
  }

  private void openSection(OptionSection section) {
    this.section = section;
    this.query = "";
    // Rebuild after the current input event, the clicked widget is part of the document
    Laby.labyAPI().minecraft().executeNextTick(this::reload);
  }

  @Override
  public boolean shouldHandleEscape() {
    return this.section != null || super.shouldHandleEscape();
  }

  @Override
  public boolean keyPressed(Key key, InputType type) {
    // ESC goes back to the menu, unless a keybind widget is listening and uses it to unassign a key
    if (key == Key.ESCAPE && this.section != null && !super.shouldHandleEscape()) {
      this.openSection(null);
      return true;
    }

    return super.keyPressed(key, type);
  }

  public List<OptionEntry> entries() {
    return this.entries;
  }

  public OptionSection section() {
    return this.section;
  }

  public String query() {
    return this.query;
  }

  void select(OptionWidget row, Key key) {
    OptionEntry entry = row.entry();
    if (key == null || key.equals(entry.key()))
      return;

    String value = this.keyMappingController.toOptionValue(key);
    if (value == null) {
      // The key can't be stored in an options.txt of this version, keep the previous one
      row.showValue();
      return;
    }

    entry.set(value, key);
    this.updateConflicts();
    this.listWidget.refreshRows();
  }

  void change(OptionWidget row, String value) {
    OptionEntry entry = row.entry();
    if (value.equals(entry.value()) || !isValid(entry.type(), value))
      return;

    entry.set(value);
    row.refresh();
  }

  void revert(OptionWidget row) {
    OptionEntry entry = row.entry();
    String loadedValue = entry.loadedValue();
    if (entry.type() != OptionEntry.Type.KEYBIND) {
      entry.set(loadedValue);
      row.showValue();
      row.refresh();
      return;
    }

    entry.set(loadedValue, this.keyMappingController.toKey(loadedValue));
    if (entry.key() != null) {
      row.showValue();
    } else {
      // An invalid value can't be shown by the keybind widget, rebuild the row after the click
      Laby.labyAPI().minecraft().executeNextTick(row::reInitialize);
    }

    this.updateConflicts();
    this.listWidget.refreshRows();
  }

  private void search(String text) {
    String query = text.trim().toLowerCase(Locale.ROOT);
    if (query.equals(this.query))
      return;

    this.query = query;
    this.listWidget.reInitialize();
    this.scrollWidget.scrollToTop();
  }

  private void updateConflicts() {
    for (OptionEntry entry : this.entries) {
      entry.conflicts().clear();
    }

    int size = this.entries.size();
    for (int i = 0; i < size; i++) {
      OptionEntry entry = this.entries.get(i);
      if (!entry.isAssigned())
        continue;

      for (int j = i + 1; j < size; j++) {
        OptionEntry other = this.entries.get(j);
        if (!other.isAssigned() || !Objects.equals(entry.key(), other.key()))
          continue;

        entry.conflicts().add(other);
        other.conflicts().add(entry);
      }
    }
  }

  private void save() {
    boolean changed = false;
    for (OptionEntry entry : this.entries) {
      if (!entry.isChanged())
        continue;

      this.options.set(entry.optionKey(), entry.value());
      changed = true;
    }

    if (!changed) {
      this.displayPreviousScreen();
      return;
    }

    try {
      this.profileController.saveOptions(this.profile.id(), this.options);
      this.displayPreviousScreen();
    } catch (IOException | RuntimeException e) {
      QuickBindsAddon.instance.logger().error("Failed to save the options of profile " + this.profile.id(), e);

      SimpleAdvancedPopup.builder()
          .title(Component.translatable("quickbinds.ui.options.error.save.title"))
          .description(Component.translatable("quickbinds.ui.options.error.save.description", Component.text(String.valueOf(e.getMessage()))))
          .addButton(SimplePopupButton.confirm())
          .build()
          .displayInOverlay();
    }
  }

  private static boolean isValid(OptionEntry.Type type, String value) {
    return switch (type) {
      case INTEGER -> OptionEntry.INTEGER_VALUE.matcher(value).matches();
      case DECIMAL ->
          OptionEntry.INTEGER_VALUE.matcher(value).matches() || OptionEntry.DECIMAL_VALUE.matcher(
              value).matches();
      case READ_ONLY, KEYBIND -> false;
      default -> true;
    };
  }

  /**
   * Uses Minecraft's translation of the option if there is one, otherwise the option key is turned
   * into a readable name ("renderDistance" -> "Render Distance").
   */
  private static String displayName(String optionKey) {
    String translationKey;
    String raw;
    if (optionKey.startsWith(OptionCategory.KEY_PREFIX)) {
      translationKey = optionKey.substring(OptionCategory.KEY_PREFIX.length());
      raw = translationKey.startsWith("key.") ? translationKey.substring(4) : translationKey;
    } else if (optionKey.startsWith("soundCategory_")) {
      raw = optionKey.substring("soundCategory_".length());
      translationKey = "soundCategory." + raw;
    } else if (optionKey.startsWith("modelPart_")) {
      raw = optionKey.substring("modelPart_".length());
      translationKey = "options.modelPart." + raw;
    } else {
      raw = optionKey;
      translationKey = "options." + optionKey;
    }

    Minecraft minecraft = Laby.labyAPI().minecraft();
    if (minecraft.hasTranslation(translationKey))
      return minecraft.getTranslation(translationKey);

    StringBuilder name = new StringBuilder(raw.length() + 4);
    boolean upperCase = true;
    char previous = ' ';
    for (int index = 0; index < raw.length(); index++) {
      char character = raw.charAt(index);
      if (character == '_' || character == '.' || character == '-') {
        upperCase = true;
        previous = ' ';
        continue;
      }

      if (upperCase || (Character.isUpperCase(character) && Character.isLowerCase(previous))) {
        if (!name.isEmpty()) {
          name.append(' ');
        }
        character = Character.toUpperCase(character);
        upperCase = false;
      }

      name.append(character);
      previous = character;
    }
    return name.toString();
  }
}
