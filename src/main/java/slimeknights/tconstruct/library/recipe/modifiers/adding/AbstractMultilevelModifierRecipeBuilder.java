package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierSalvage;
import slimeknights.tconstruct.library.recipe.modifiers.adding.MultilevelModifierRecipe.LevelEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractMultilevelModifierRecipeBuilder<T extends AbstractMultilevelModifierRecipeBuilder<T>> extends AbstractRecipeBuilder<T> {
  protected final ModifierId result;
  protected final List<LevelEntry> levels = new ArrayList<>();
  protected boolean allowCrystal = true;
  protected Ingredient tools = Ingredient.EMPTY;
  protected int maxToolSize = ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE;
  protected boolean checkTraitLevel = false;

  /** Gets the casted builder */
  @SuppressWarnings("unchecked")
  private T self() {
    return (T) this;
  }


  /* Tool */

  /**
   * Sets the list of tools this modifier can be applied to
   * @param tools    Modifier tools list
   * @param maxSize  Max stack size this recipe applies to
   * @return  Builder instance
   */
  public T setTools(Ingredient tools, int maxSize) {
    this.tools = tools;
    this.maxToolSize = maxSize;
    return self();
  }

  /**
   * Sets the list of tools this modifier can be applied to
   * @param tools  Modifier tools list
   * @return  Builder instance
   */
  public T setTools(Ingredient tools) {
    return setTools(tools, ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE);
  }

  /**
   * Sets the tag for applicable tools
   * @param tag  Tag
   * @return  Builder instance
   */
  public T setTools(TagKey<Item> tag) {
    return this.setTools(Ingredient.of(tag));
  }


  /* Boolean */

  /**
   * Allows using modifier crystals to apply this modifier
   * @return  Builder instance
   */
  public T allowCrystal() {
    allowCrystal = true;
    return self();
  }

  /**
   * Disallows using modifier crystals to apply this modifier
   * @return  Builder instance
   */
  public T disallowCrystal() {
    allowCrystal = false;
    return self();
  }

  /**
   * Makes the recipe check the trait level in addition to the level of recipe modifiers
   * @return  Builder instance
   */
  public T checkTraitLevel() {
    checkTraitLevel = true;
    return self();
  }


  /* Requirements */

  /** Base logic for adding a level */
  private T addLevelRange(@Nullable SlotCount slots, int minLevel, int maxLevel) {
    if (minLevel > maxLevel) {
      throw new JsonSyntaxException("minLevel must be less than or equal to maxLevel");
    }
    if (!levels.isEmpty() && minLevel <= levels.get(levels.size() - 1).level().max()) {
      throw new JsonSyntaxException("Level range must be greater than previous range");
    }
    this.levels.add(new LevelEntry(slots, ModifierEntry.VALID_LEVEL.range(minLevel, maxLevel)));
    return self();
  }

  /** Adds a level range for the given type and count */
  public T addLevelRange(SlotType slot, int slotCount, int minLevel, int maxLevel) {
    return addLevelRange(new SlotCount(slot, slotCount), minLevel, maxLevel);
  }

  /** Adds a level for the given type and count */
  public T addLevel(SlotType slot, int slotCount, int level) {
    return addLevelRange(slot, slotCount, level, level);
  }

  /** Adds a level for the given type and count */
  public T addMinLevel(SlotType slot, int slotCount, int level) {
    return addLevelRange(slot, slotCount, level, Short.MAX_VALUE);
  }

  /** Adds slotless at the given level range */
  public T addLevelRange(int minLevel, int maxLevel) {
    return addLevelRange(null, minLevel, maxLevel);
  }

  /** Adds slotless at the given level */
  public T addLevel(int level) {
    return addLevelRange(level, level);
  }

  /** Adds slotless at the given level */
  public T addMinLevel(int level) {
    return addLevelRange(level, Short.MAX_VALUE);
  }


  /* Saving */

  /** Saves all salvage recipes for this recipe */
  public T saveSalvage(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (levels.isEmpty()) {
      throw new IllegalStateException("Must have at least 1 level");
    }
    for (LevelEntry levelEntry : levels) {
      if (levelEntry.slots() != null) {
        consumer.accept(new LoadableFinishedRecipe<>(new ModifierSalvage(
          id.withSuffix("_level_" + levelEntry.level().min()),
          tools, maxToolSize, result, levelEntry.level(), levelEntry.slots()), ModifierSalvage.LOADER, null));
      }
    }
    return self();
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, result);
  }
}
