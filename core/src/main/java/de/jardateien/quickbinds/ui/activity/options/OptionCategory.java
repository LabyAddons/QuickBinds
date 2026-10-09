package de.jardateien.quickbinds.ui.activity.options;

import java.util.HashMap;
import java.util.Map;

/**
 * Groups the lines of a profile's options.txt like the vanilla options screens. Only the option key
 * is used, nothing is read from Minecraft.
 */
public enum OptionCategory {

  CONTROL_OPTIONS("controlOptions", OptionSection.CONTROLS),
  MOVEMENT("movement", OptionSection.CONTROLS),
  GAMEPLAY("gameplay", OptionSection.CONTROLS),
  INVENTORY("inventory", OptionSection.CONTROLS),
  CREATIVE("creative", OptionSection.CONTROLS),
  MULTIPLAYER("multiplayer", OptionSection.CONTROLS),
  MISCELLANEOUS("miscellaneous", OptionSection.CONTROLS),
  OTHER_KEYS("otherKeys", OptionSection.CONTROLS),
  MOUSE("mouse", OptionSection.MOUSE),
  CHAT("chat", OptionSection.CHAT),
  SOUND("sound", OptionSection.SOUND),
  VIDEO("video", OptionSection.VIDEO),
  SKIN("skin", OptionSection.SKIN),
  ACCESSIBILITY("accessibility", OptionSection.ACCESSIBILITY),
  RESOURCE_PACKS("resourcePacks", OptionSection.OTHER),
  GENERAL("general", OptionSection.OTHER),
  OTHER("other", OptionSection.OTHER);

  public static final String KEY_PREFIX = "key_";

  private static final Map<String, OptionCategory> CATEGORIES = new HashMap<>();
  private static final Map<String, Integer> ORDER = new HashMap<>();

  static {
    keys(MOVEMENT, "key.forward", "key.left", "key.back", "key.right", "key.jump", "key.sneak", "key.sprint");
    keys(GAMEPLAY, "key.attack", "key.use", "key.pickItem");
    keys(INVENTORY, "key.inventory", "key.swapOffhand", "key.drop", "key.hotbar.1", "key.hotbar.2", "key.hotbar.3", "key.hotbar.4", "key.hotbar.5", "key.hotbar.6", "key.hotbar.7", "key.hotbar.8", "key.hotbar.9");
    keys(CREATIVE, "key.saveToolbarActivator", "key.loadToolbarActivator");
    keys(MULTIPLAYER, "key.chat", "key.command", "key.playerlist", "key.socialInteractions", "key.spectatorOutlines", "key.spectatorHotbar");
    keys(MISCELLANEOUS, "key.screenshot", "key.togglePerspective", "key.smoothCamera", "key.fullscreen", "key.advancements", "key.quickActions", "key.toggleGui", "key.toggleSpectatorShaderEffects", "key.streamStartStop", "key.streamPauseUnpause", "key.streamCommercial", "key.streamToggleMic");

    options(VIDEO, "renderDistance", "simulationDistance", "graphicsMode", "fancyGraphics", "maxFps", "inactivityFpsLimit", "enableVsync", "fullscreen", "fullscreenResolution", "guiScale", "gamma", "fov", "bobView", "renderClouds", "cloudRange", "particles", "ao", "biomeBlendRadius", "mipmapLevels", "entityShadows", "entityDistanceScaling", "prioritizeChunkUpdates", "attackIndicator", "menuBackgroundBlurriness", "chunkSectionFadeInTime", "textureFiltering", "maxAnisotropyBit", "cutoutLeaves", "improvedTransparency", "vignette", "weatherRadius", "preferredGraphicsBackend", "useVbo", "fboEnable", "anaglyph3d", "showAutosaveIndicator");
    options(SOUND, "directionalAudio", "showSubtitles", "soundDevice", "musicFrequency", "showNowPlayingToast");
    options(MOUSE, "invertYMouse", "invertXMouse", "rawMouseInput", "discrete_mouse_scroll", "touchscreen", "allowCursorChanges");
    options(CONTROL_OPTIONS, "toggleCrouch", "toggleSprint", "toggleAttack", "toggleUse", "sprintWindow", "autoJump", "operatorItemsTab");
    options(CHAT, "onlyShowSecureChat", "saveChatDrafts", "hideMatchedNames", "autoSuggestions", "reducedDebugInfo", "textBackgroundOpacity", "backgroundForChatOnly");
    options(SKIN, "mainHand");
    options(ACCESSIBILITY, "narrator", "highContrast", "highContrastBlockOutline", "darkMojangStudiosBackground", "hideLightningFlashes", "hideSplashTexts", "panoramaScrollSpeed", "damageTiltStrength", "notificationDisplayTime", "glintSpeed", "glintStrength", "screenEffectScale", "fovEffectScale", "darknessEffectScale", "rotateWithMinecart", "japaneseGlyphVariants", "onboardAccessibility");
    options(RESOURCE_PACKS, "resourcePacks", "incompatibleResourcePacks");
    options(GENERAL, "version", "lang", "forceUnicodeFont", "realmsNotifications", "allowServerListing", "pauseOnLostFocus", "advancedItemTooltips", "heldItemTooltips", "hideServerAddress", "skipMultiplayerWarning", "skipRealms32bitWarning", "joinedFirstServer", "tutorialStep", "hideBundleTutorial", "syncChunkWrites", "useNativeTransport", "telemetryOptInExtra", "snooperEnabled", "overrideWidth", "overrideHeight", "lastServer");
  }

  private final String translationKey;
  private final OptionSection section;

  OptionCategory(String name, OptionSection section) {
    this.translationKey = "quickbinds.ui.options.category." + name;
    this.section = section;
  }

  public String translationKey() {
    return this.translationKey;
  }

  public OptionSection section() {
    return this.section;
  }

  public static OptionCategory of(String optionKey) {
    if (optionKey.startsWith(KEY_PREFIX)) {
      String id = optionKey.substring(KEY_PREFIX.length());
      if (id.startsWith("key.debug."))
        return MISCELLANEOUS;

      OptionCategory category = CATEGORIES.get(id);
      return category != null ? category : OTHER_KEYS;
    }

    OptionCategory category = CATEGORIES.get(optionKey);
    if (category != null)
      return category;

    if (optionKey.startsWith("soundCategory_"))
      return SOUND;
    if (optionKey.startsWith("modelPart_"))
      return SKIN;
    if (optionKey.startsWith("chat"))
      return CHAT;
    if (optionKey.startsWith("mouse"))
      return MOUSE;

    return OTHER;
  }

  /**
   * @return the position inside the category, vanilla keybinds keep the order of the controls
   *     screen, everything else keeps the order of the file
   */
  public static int order(String optionKey) {
    Integer order = ORDER.get(optionKey.startsWith(KEY_PREFIX) ? optionKey.substring(KEY_PREFIX.length()) : optionKey);
    return order != null ? order : Integer.MAX_VALUE;
  }

  private static void keys(OptionCategory category, String... ids) {
    for (int index = 0; index < ids.length; index++) {
      CATEGORIES.put(ids[index], category);
      ORDER.put(ids[index], index);
    }
  }

  private static void options(OptionCategory category, String... keys) {
    for (String key : keys) {
      CATEGORIES.put(key, category);
    }
  }
}
