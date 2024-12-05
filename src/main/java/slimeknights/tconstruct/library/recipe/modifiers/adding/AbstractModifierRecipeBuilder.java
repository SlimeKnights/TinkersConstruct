package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierSalvage;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;

import javax.annotation.Nullable;
import java.util.function.Consumer;

import static slimeknights.tconstruct.library.modifiers.ModifierEntry.VALID_LEVEL;

/** Shared logic between normal and incremental modifier recipe builders */
@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractModifierRecipeBuilder<T extends AbstractModifierRecipeBuilder<T>> extends AbstractRecipeBuilder<T> {
  // shared
  protected final ModifierId result;
  protected Ingredient tools = Ingredient.of(TinkerTags.Items.MODIFIABLE);
  protected int maxToolSize = ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE;
  @Nullable
  protected SlotCount slots;
  protected int minLevel = 1;
  protected int maxLevel = VALID_LEVEL.max();
  protected boolean useSalvageMax = false;
  // modifier recipe
  protected boolean allowCrystal = true;
  protected boolean checkTraitLevel = false;

  /**
   * Sets the list of tools this modifier can be applied to
   * @param tools  Modifier tools list
   * @return  Builder instance
   */
  public T setTools(Ingredient tools) {
    return setTools(tools, ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE);
  }

  /**
   * Sets the list of tools this modifier can be applied to
   * @param tools    Modifier tools list
   * @param maxSize  Max stack size this recipe applies to
   * @return  Builder instance
   */
  public T setTools(Ingredient tools, int maxSize) {
    this.tools = tools;
    this.maxToolSize = maxSize;
    return (T) this;
  }

  /**
   * Sets the tag for applicable tools
   * @param tag  Tag
   * @return  Builder instance
   */
  public T setTools(TagKey<Item> tag) {
    return this.setTools(Ingredient.of(tag));
  }

  /**
   * Sets the max level for this modifier, affects both the recipe and the salvage
   * @param level  Max level
   * @return  Builder instance
   */
  @CanIgnoreReturnValue
  public T setMinLevel(int level) {
    if (level < 1) {
      throw new IllegalArgumentException("Min level must be greater than 0");
    }
    this.minLevel = level;
    return (T) this;
  }

  /**
   * Sets the max level for this modifier, affects both the recipe and the salvage
   * @param level  Max level
   * @return  Builder instance
   */
  public T setMaxLevel(int level) {
    if (level < 1) {
      throw new IllegalArgumentException("Max level must be greater than 0");
    }
    this.maxLevel = level;
    return (T) this;
  }

  /**
   * Tells the builder to set the max level for salvage recipes.
   * Normally we leave it off for flexability, but it sometimes needs to be forced as a higher level has another meaning.
   * @return  Builder instance
   */
  public T useSalvageMax() {
    this.useSalvageMax = true;
    return (T) this;
  }

  /** Sets this modifier to only work at a single level */
  public T exactLevel(int level) {
    return setLevelRange(level, level);
  }

  /** Sets this modifier to work on the given range of levels */
  public T setLevelRange(int min, int max) {
    setMinLevel(min);
    setMaxLevel(max);
    return (T) this;
  }

  /**
   * Allows using modifier crystals to apply this modifier
   * @return  Builder instance
   */
  public T allowCrystal() {
    allowCrystal = true;
    return (T) this;
  }

  /**
   * Disallows using modifier crystals to apply this modifier
   * @return  Builder instance
   */
  public T disallowCrystal() {
    allowCrystal = false;
    return (T) this;
  }

  /**
   * Makes the recipe check the trait level in addition to the level of recipe modifiers
   * @return  Builder instance
   */
  public T checkTraitLevel() {
    checkTraitLevel = true;
    return (T) this;
  }


  /* Slots */

  /**
   * Sets the number of slots required by this recipe
   * @param slotType  Slot type
   * @param slots     Slot count
   * @return  Builder instance
   */
  public T setSlots(SlotType slotType, int slots) {
    if (slots < 0) {
      throw new IllegalArgumentException("Slots must be positive");
    }
    this.slots = new SlotCount(slotType, slots);
    return (T) this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, result);
  }

  /**
   * Builds a salvage recipe from this recipe builder
   * @param consumer  Consumer instance
   * @param id        Recipe ID
   */
  public T saveSalvage(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (maxLevel < minLevel) {
      throw new IllegalStateException("Max level must be greater than min level");
    }
    if (slots == null) {
      TConstruct.LOG.warn("Salvage for {} has no modifier slots. This is useless and will throw an exception in a future version.", id);
      return (T) this;
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new LoadableFinishedRecipe<>(makeSalvage(id), ModifierSalvage.LOADER, advancementId));
    return (T) this;
  }

  /** Makes the salvage recipe to save in {@link #saveSalvage(Consumer, ResourceLocation)} */
  protected ModifierSalvage makeSalvage(ResourceLocation id) {
    return new ModifierSalvage(id, tools, maxToolSize, result, VALID_LEVEL.range(minLevel, useSalvageMax ? maxLevel : VALID_LEVEL.max()), slots);
  }
}
