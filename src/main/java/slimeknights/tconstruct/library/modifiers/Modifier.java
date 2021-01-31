package slimeknights.tconstruct.library.modifiers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.tools.ToolStatsModifierBuilder;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Interface representing both modifiers and traits.
 * Any behavior special to either one is handled elsewhere.
 */
@RequiredArgsConstructor
public class Modifier implements IForgeRegistryEntry<Modifier> {
  private static final String KEY_LEVEL = "enchantment.level.";
  public static final int DEFAULT_PRIORITY = 100;

  /** Display color for all text for this modifier */
  @Getter
  private final int color;

  /** Registry name of this modifier, null before fully registered */
  @Getter @Nullable
  private ModifierId registryName;

  /** Cached key used for translations */
  @Nullable
  private String translationKey;
  /** Cached text component for display names */
  @Nullable
  private ITextComponent displayName;

  /**
   * Override this method to make your modifier run earlier or later.
   * Higher numbers run earlier, 100 is default
   * @return Priority
   */
  public int getPriority() {
    return DEFAULT_PRIORITY;
  }


  /* Registry methods */

  @Override
  public final Modifier setRegistryName(ResourceLocation name) {
    if (registryName != null) {
      throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + registryName);
    }
    // check mod container, should be the active mod
    // don't want mods registering stuff in Tinkers namespace, or Minecraft
    String activeMod = ModLoadingContext.get().getActiveNamespace();
    if (!name.getNamespace().equals(activeMod)) {
      LogManager.getLogger().info("Potentially Dangerous alternative prefix for name `{}`, expected `{}`. This could be a intended override, but in most cases indicates a broken mod.", name, activeMod);
    }
    this.registryName = new ModifierId(name);
    return this;
  }

  /**
   * Gets the modifier ID. Unlike {@link #getRegistryName()}, this method must be nonnull
   * @return  Modifier ID
   */
  public ModifierId getId() {
    return Objects.requireNonNull(registryName, "Modifier has null registry name");
  }

  @Override
  public Class<Modifier> getRegistryType() {
    return Modifier.class;
  }


  /* Tooltips */

  /**
   * Overridable method to create a translation key. Will be called once and the result cached
   * @return  Translation key
   */
  protected String makeTranslationKey() {
    return Util.makeTranslationKey("modifier", registryName);
  }

  /**
   * Gets the translation key for this modifier
   * @return  Translation key
   */
  public final String getTranslationKey() {
    if (translationKey == null) {
      translationKey = makeTranslationKey();
    }
    return translationKey;
  }

  /**
   * Overridable method to create the display name for this modifier, ideal to modify colors
   * @return  Display name
   */
  protected ITextComponent makeDisplayName() {
    return new TranslationTextComponent(getTranslationKey());
  }

  /**
   * Gets the display name for this modifier
   * @return  Display name for this modifier
   */
  public final ITextComponent getDisplayName() {
    if (displayName == null) {
      displayName = new TranslationTextComponent(getTranslationKey()).modifyStyle(style -> style.setColor(Color.fromInt(color)));
    }
    return displayName;
  }

  /**
   * Gets the display name for the given level of this modifier
   * @param level  Modifier level
   * @return  Display name
   */
  public ITextComponent getDisplayName(int level) {
    return new TranslationTextComponent(getTranslationKey())
      .append(new TranslationTextComponent(KEY_LEVEL + level))
      .modifyStyle(style -> style.setColor(Color.fromInt(color)));
  }


  /* Tool building hooks */

  /**
   * Adds raw stats to the tool. Called whenever modifiers are rebuilt
   * @param level    Modifier level
   * @param builder  Tool stat builder
   */
  public void addToolStats(int level, ToolStatsModifierBuilder builder) {}

  /**
   * Adds any relevant volatile data to the tool data. This data is rebuilt every time modifiers rebuild
   * @param level  Modifier level
   * @param data   Mutable mod NBT data
   */
  public void addVolatileData(int level, ModDataNBT data) {}

  /**
   * Adds enchantments from this modifier's effect
   * @param level     Modifier level
   * @param consumer  Consumer accepting any enchantments
   */
  public void addEnchantments(int level, BiConsumer<Enchantment, Integer> consumer) {}

  /**
   * Adds attributes from this modifier's effect
   * @param level     Modifier level
   * @param consumer  Attribute consumer
   */
  public void addAttributes(int level, BiConsumer<Attribute,AttributeModifier> consumer) {}
}
