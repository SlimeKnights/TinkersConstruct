package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.recipe.modifiers.adding.MultilevelModifierRecipe.LevelEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modifier recipe that changes max level and slot behavior each level. Used for a single input recipe that has multiple slot requirements
 */
@RequiredArgsConstructor(staticName = "modifier")
public class MultilevelModifierRecipeBuilder extends AbstractRecipeBuilder<MultilevelModifierRecipeBuilder> {
  private final ModifierId result;
  private final List<LevelEntry> levels = new ArrayList<>();
  // inputs
  private final List<SizedIngredient> inputs = new ArrayList<>();
  private boolean allowCrystal = true;
  private Ingredient tools = Ingredient.EMPTY;
  private int maxToolSize = ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE;
  // requirements
  private ModifierMatch requirements = ModifierMatch.ALWAYS;
  private String requirementsError = "";


  /* Inputs */

  /**
   * Sets the list of tools this modifier can be applied to
   * @param tools    Modifier tools list
   * @param maxSize  Max stack size this recipe applies to
   * @return  Builder instance
   */
  public MultilevelModifierRecipeBuilder setTools(Ingredient tools, int maxSize) {
    this.tools = tools;
    this.maxToolSize = maxSize;
    return this;
  }

  /**
   * Sets the list of tools this modifier can be applied to
   * @param tools  Modifier tools list
   * @return  Builder instance
   */
  public MultilevelModifierRecipeBuilder setTools(Ingredient tools) {
    return setTools(tools, ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE);
  }

  /**
   * Sets the tag for applicable tools
   * @param tag  Tag
   * @return  Builder instance
   */
  public MultilevelModifierRecipeBuilder setTools(TagKey<Item> tag) {
    return this.setTools(Ingredient.of(tag));
  }

  /**
   * Adds an input to the recipe
   * @param ingredient  Input
   * @return  Builder instance
   */
  public MultilevelModifierRecipeBuilder addInput(SizedIngredient ingredient) {
    this.inputs.add(ingredient);
    return this;
  }

  /**
   * Adds an input to the recipe
   * @param ingredient  Input
   * @return  Builder instance
   */
  public MultilevelModifierRecipeBuilder addInput(Ingredient ingredient) {
    return addInput(SizedIngredient.of(ingredient));
  }

  /**
   * Adds an input with the given amount, does not affect the salvage builder
   * @param item    Item
   * @param amount  Amount
   * @return  Builder instance
   */
  public MultilevelModifierRecipeBuilder addInput(ItemLike item, int amount) {
    return addInput(SizedIngredient.fromItems(amount, item));
  }

  /**
   * Adds an input with a size of 1, does not affect the salvage builder
   * @param item    Item
   * @return  Builder instance
   */
  public MultilevelModifierRecipeBuilder addInput(ItemLike item) {
    return addInput(item, 1);
  }

  /**
   * Adds an input to the recipe
   * @param tag     Tag input
   * @param amount  Amount required
   * @return  Builder instance
   */
  public MultilevelModifierRecipeBuilder addInput(TagKey<Item> tag, int amount) {
    return addInput(SizedIngredient.fromTag(tag, amount));
  }

  /**
   * Adds an input to the recipe
   * @param tag     Tag input
   * @return  Builder instance
   */
  public MultilevelModifierRecipeBuilder addInput(TagKey<Item> tag) {
    return addInput(tag, 1);
  }


  /**
   * Allows using modifier crystals to apply this modifier
   * @return  Builder instance
   */
  public MultilevelModifierRecipeBuilder allowCrystal() {
    allowCrystal = true;
    return this;
  }

  /**
   * Disallows using modifier crystals to apply this modifier
   * @return  Builder instance
   */
  public MultilevelModifierRecipeBuilder disallowCrystal() {
    allowCrystal = false;
    return this;
  }


  /* Requirements */

  /**
   * Sets the modifier requirements for this recipe
   * @param requirements  Modifier requirements
   * @return  Builder instance
   */
  public MultilevelModifierRecipeBuilder setRequirements(ModifierMatch requirements) {
    this.requirements = requirements;
    return this;
  }

  /**
   * Sets the modifier requirements error for when it does not matcH
   * @param requirementsError  Requirements error lang key
   * @return  Builder instance
   */
  public MultilevelModifierRecipeBuilder setRequirementsError(String requirementsError) {
    this.requirementsError = requirementsError;
    return this;
  }

  /** Base logic for adding a level */
  private MultilevelModifierRecipeBuilder addLevelRange(@Nullable SlotCount slots, int minLevel, int maxLevel) {
    if (minLevel > maxLevel) {
      throw new JsonSyntaxException("minLevel must be less than or equal to maxLevel");
    }
    if (!levels.isEmpty() && minLevel <= levels.get(levels.size() - 1).maxLevel()) {
      throw new JsonSyntaxException("Level range must be greater than previous range");
    }
    this.levels.add(new LevelEntry(slots, minLevel, maxLevel));
    return this;
  }

  /** Adds a level range for the given type and count */
  public MultilevelModifierRecipeBuilder addLevelRange(SlotType slot, int slotCount, int minLevel, int maxLevel) {
    return addLevelRange(new SlotCount(slot, slotCount), minLevel, maxLevel);
  }

  /** Adds a level for the given type and count */
  public MultilevelModifierRecipeBuilder addLevel(SlotType slot, int slotCount, int level) {
    return addLevelRange(slot, slotCount, level, level);
  }

  /** Adds a level for the given type and count */
  public MultilevelModifierRecipeBuilder addMinLevel(SlotType slot, int slotCount, int level) {
    return addLevelRange(slot, slotCount, level, Short.MAX_VALUE);
  }

  /** Adds slotless at the given level range */
  public MultilevelModifierRecipeBuilder addLevelRange(int minLevel, int maxLevel) {
    return addLevelRange(null, minLevel, maxLevel);
  }

  /** Adds slotless at the given level */
  public MultilevelModifierRecipeBuilder addLevel(int level) {
    return addLevelRange(level, level);
  }

  /** Adds slotless at the given level */
  public MultilevelModifierRecipeBuilder addMinLevel(int level) {
    return addLevelRange(level, Short.MAX_VALUE);
  }


  /* Saving */

  /** Saves all salvage recipes for this recipe */
  public MultilevelModifierRecipeBuilder saveSalvage(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (levels.isEmpty()) {
      throw new IllegalStateException("Must have at least 1 level");
    }
    for (LevelEntry levelEntry : levels) {
      if (levelEntry.slots() != null) {
        consumer.accept(new FinishedSalvage(
          new ResourceLocation(id.getNamespace(), id.getPath() + "_level_" + levelEntry.minLevel()),
          levelEntry.slots(), levelEntry.minLevel(), levelEntry.maxLevel()));
      }
    }
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumerIn) {
    save(consumerIn, result);
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (inputs.isEmpty() && !allowCrystal) {
      throw new IllegalStateException("Must either have at least 1 input or allow crystal");
    }
    if (levels.isEmpty()) {
      throw new IllegalStateException("Must have at least 1 level");
    }
    if (!inputs.isEmpty() && requirementsError.isEmpty() && levels.size() > 1) {
      throw new IllegalStateException("Must set requirements error if inputs are set");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new FinishedAdding(id, advancementId));
  }

  /** Writes common JSON elements */
  private void writeCommon(JsonObject json) {
    Ingredient ingredient = tools;
    if (tools == Ingredient.EMPTY) {
      ingredient = Ingredient.of(TinkerTags.Items.MODIFIABLE);
    }
    json.add("tools", ingredient.toJson());
    if (maxToolSize != ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE) {
      json.addProperty("max_tool_size", maxToolSize);
    }
  }

  /** Recipe for modifier adding */
  private class FinishedAdding extends AbstractFinishedRecipe {
    public FinishedAdding(ResourceLocation id, @Nullable ResourceLocation advancementId) {
      super(id, advancementId);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      if (!inputs.isEmpty()) {
        JsonArray array = new JsonArray();
        for (SizedIngredient ingredient : inputs) {
          array.add(ingredient.serialize());
        }
        json.add("inputs", array);
      }
      json.addProperty("allow_crystal", allowCrystal);
      writeCommon(json);
      if (requirements != ModifierMatch.ALWAYS) {
        JsonObject reqJson = requirements.serialize();
        reqJson.addProperty("error", requirementsError);
        json.add("requirements", reqJson);
      } else if (!requirementsError.isEmpty()) {
        json.addProperty("level_error", requirementsError);
      }
      JsonArray levelArray = new JsonArray();
      for (LevelEntry levelEntry : levels) {
        levelArray.add(levelEntry.serialize());
      }
      json.add("levels", levelArray);
      json.addProperty("result", result.toString());
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerModifiers.multilevelModifierSerializer.get();
    }
  }

  /** Recipe for modifier salvage */
  private class FinishedSalvage extends AbstractFinishedRecipe {
    private final SlotCount slots;
    private final int minLevel;
    private final int maxLevel;

    public FinishedSalvage(ResourceLocation id, SlotCount slots, int minLevel, int maxLevel) {
      super(id, null);
      this.slots = slots;
      this.minLevel = minLevel;
      this.maxLevel = maxLevel;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      writeCommon(json);
      JsonObject slotJson = new JsonObject();
      slotJson.addProperty(slots.getType().getName(), slots.getCount());
      json.add("slots", slotJson);
      json.addProperty("modifier", result.toString());
      json.addProperty("min_level", minLevel);
      if (maxLevel != Short.MAX_VALUE) {
        json.addProperty("max_level", maxLevel);
      }
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerModifiers.modifierSalvageSerializer.get();
    }
  }
}
