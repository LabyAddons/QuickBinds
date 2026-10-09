package de.jardateien.quickbinds.ui.activity.options;

import net.labymod.api.client.component.Component;
import org.jetbrains.annotations.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * The range of an option that is edited with a slider. Options without a known range keep their
 * text field.
 */
public class OptionSlider {

  private static final Map<String, OptionSlider> SLIDERS = new HashMap<>();
  private static final OptionSlider PERCENT = new OptionSlider(0, 1, 0.01F, Format.PERCENT);
  // Since 1.13 the fov is stored as (fov - 70) / 40, before that as degrees
  private static final OptionSlider FOV_NORMALIZED = new OptionSlider(-1, 1, 0.025F, Format.FOV);
  private static final OptionSlider FOV_DEGREES = new OptionSlider(30, 110, 1, Format.NUMBER);

  static {
    percent("gamma", "mouseSensitivity", "chatOpacity", "textBackgroundOpacity", "chatLineSpacing", "chatScale", "chatWidth", "chatHeightFocused", "chatHeightUnfocused", "screenEffectScale", "fovEffectScale", "darknessEffectScale", "glintSpeed", "glintStrength", "damageTiltStrength", "panoramaScrollSpeed");

    number("chatDelay", 0, 6, 0.1F);
    number("entityDistanceScaling", 0.5F, 5, 0.25F);
    number("mouseWheelSensitivity", 0.01F, 10, 0.01F);
    number("notificationDisplayTime", 0.5F, 10, 0.1F);
    number("renderDistance", 2, 32, 1);
    number("simulationDistance", 5, 32, 1);
    number("biomeBlendRadius", 0, 7, 1);
    number("mipmapLevels", 0, 4, 1);
    number("menuBackgroundBlurriness", 0, 10, 1);
    number("cloudRange", 2, 128, 1);
    number("weatherRadius", 3, 10, 1);

    SLIDERS.put("maxFps", new OptionSlider(10, 260, 10, Format.MAX_FPS));
    SLIDERS.put("guiScale", new OptionSlider(0, 6, 1, Format.GUI_SCALE));
  }

  private final float min;
  private final float max;
  private final float steps;
  private final int decimals;
  private final Format format;

  private OptionSlider(float min, float max, float steps, Format format) {
    this.min = min;
    this.max = max;
    this.steps = steps;
    this.decimals = Math.max(0, new BigDecimal(String.valueOf(steps)).stripTrailingZeros().scale());
    this.format = format;
  }

  /**
   * @return the slider of the option or null if it has no known range or the value is outside of it
   */
  @Nullable
  public static OptionSlider of(String optionKey, String value) {
    float number;
    try {
      number = Float.parseFloat(value);
    } catch (NumberFormatException e) {
      return null;
    }

    OptionSlider slider;
    if (optionKey.equals("fov")) {
      slider = number >= -1 && number <= 1 ? FOV_NORMALIZED : FOV_DEGREES;
    } else if (optionKey.startsWith("soundCategory_")) {
      slider = PERCENT;
    } else {
      slider = SLIDERS.get(optionKey);
    }

    if (slider == null || number < slider.min || number > slider.max)
      return null;

    return slider;
  }

  public float min() {
    return this.min;
  }

  public float max() {
    return this.max;
  }

  public float steps() {
    return this.steps;
  }

  /**
   * @param value the slider value
   * @param integer whether the profile stores this option without decimals
   * @return the value to store in the profile's options.txt
   */
  public String toOptionValue(float value, boolean integer) {
    if (integer)
      return String.valueOf(Math.round(value));

    BigDecimal decimal = new BigDecimal(value).setScale(this.decimals, RoundingMode.HALF_UP).stripTrailingZeros();
    if (decimal.scale() < 1) {
      // Minecraft writes floating point options with at least one decimal ("1.0")
      decimal = decimal.setScale(1, RoundingMode.UNNECESSARY);
    }
    return decimal.toPlainString();
  }

  public Component label(float value) {
    switch (this.format) {
      case PERCENT:
        return Component.text(Math.round(value * 100) + "%");
      case FOV:
        return Component.text(String.valueOf(Math.round(70 + value * 40)));
      case MAX_FPS:
        return value >= this.max ? Component.translatable("quickbinds.ui.options.slider.unlimited") : Component.text(String.valueOf(Math.round(value)));
      case GUI_SCALE:
        return value <= this.min ? Component.translatable("quickbinds.ui.options.slider.auto") : Component.text(String.valueOf(Math.round(value)));
      default:
        return Component.text(new BigDecimal(value).setScale(this.decimals, RoundingMode.HALF_UP).toPlainString());
    }
  }

  private static void percent(String... optionKeys) {
    for (String optionKey : optionKeys) {
      SLIDERS.put(optionKey, PERCENT);
    }
  }

  private static void number(String optionKey, float min, float max, float steps) {
    SLIDERS.put(optionKey, new OptionSlider(min, max, steps, Format.NUMBER));
  }

  private enum Format {
    NUMBER,
    PERCENT,
    FOV,
    MAX_FPS,
    GUI_SCALE
  }
}
