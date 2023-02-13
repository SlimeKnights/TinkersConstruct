package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.util.Lazy;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Shared logic between normal and incremental modifier recipe builders */
@SuppressWarnings("unchecked")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractModifierRecipeBuilder<T extends AbstractModifierRecipeBuilder<T>> extends AbstractRecipeBuilder<T> {
  protected static final Lazy<Ingredient> DEFAULT_TOOL = Lazy.of(() -> Ingredient.of(TinkerTags.Items.MODIFIABLE));
  /** @deprecated use {@link TinkerTags.Items#UNARMED} */
  @Deprecated
  protected static final Lazy<ModifierMatch> UNARMED_MODIFIER = Lazy.of(() -> ModifierMatch.entry(TinkerModifiers.ambidextrous));
  /** @deprecated use {@link TinkerTags.Items#UNARMED} */
  @Deprecated
  protected static final String UNARMED_ERROR = TConstruct.makeTranslationKey("recipe", "modifier.unarmed");
  // shared
  protected final ModifierEntry result;
  protected Ingredient tools = Ingredient.EMPTY;
  protected int maxToolSize = ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE;
  protected SlotType slotType;
  protected int slots;
  protected int maxLevel = 0;
  // modifier recipe
  protected ModifierMatch requirements = ModifierMatch.ALWAYS;
  protected String requirementsError = null;
  protected boolean allowCrystal = true;
  // salvage recipe
  protected int salvageMinLevel = 1;
  protected int salvageMaxLevel = 0;
  protected boolean includeUnarmed = false;

  /** Generates a second copy of this recipe for the sake of the unarmed modifier */
  @Deprecated
  public T includeUnarmed() {
    TConstruct.LOG.warn("Unarmed has been reworked for Tinkers 1.18. Instead of having two recipes, just include the unarmed tag as a valid input for your recipe. This method will be removed in 1.19");
    this.includeUnarmed = true;
    return (T) this;
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
   * Sets the modifier requirements for this recipe
   * @param requirements  Modifier requirements
   * @return  Builder instance
   */
  public T setRequirements(ModifierMatch requirements) {
    this.requirements = requirements;
    return (T) this;
  }

  /**
   * Sets the modifier requirements error for when it does not matcH
   * @param requirementsError  Requirements error lang key
   * @return  Builder instance
   */
  public T setRequirementsError(String requirementsError) {
    this.requirementsError = requirementsError;
    return (T) this;
  }

  /**
   * Sets the min level for the salvage recipe
   * @param level  Min level
   * @return  Builder instance
   */
  public T setMinSalvageLevel(int level) {
    if (level < 1) {
      throw new IllegalArgumentException("Min level must be greater than 0");
    }
    this.salvageMinLevel = level;
    return (T) this;
  }

  /**
   * Sets the min level for the salvage recipe
   * @param minLevel  Min level for salvage
   * @param maxLevel  Max level for salvage
   * @return  Builder instance
   */
  public T setSalvageLevelRange(int minLevel, int maxLevel) {
    setMinSalvageLevel(minLevel);
    if (maxLevel < minLevel) {
      throw new IllegalArgumentException("Max level must be grater than or equal to min level");
    }
    this.salvageMaxLevel = maxLevel;
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
    this.slotType = slotType;
    this.slots = slots;
    return (T) this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, result.getId());
  }

  /**
   * Builds a salvage recipe from this recipe builder
   * @param consumer  Consumer instance
   * @param id        Recipe ID
   */
  public abstract T saveSalvage(Consumer<FinishedRecipe> consumer, ResourceLocation id);

  /** Writes common JSON components between the two types */
  private void writeCommon(JsonObject json, boolean unarmed) {
    Ingredient ingredient = tools;
    if (tools == Ingredient.EMPTY) {
      ingredient = DEFAULT_TOOL.get();
    }
    // if true, only chestplates
    if (unarmed) {
      ingredient = CompoundIngredient.of(ingredient, Ingredient.of(TinkerTags.Items.UNARMED));
    }
    json.add("tools", ingredient.toJson());
    if (maxToolSize != ITinkerStationRecipe.DEFAULT_TOOL_STACK_SIZE) {
      json.addProperty("max_tool_size", maxToolSize);
    }
    if (slotType != null && slots > 0) {
      JsonObject slotJson = new JsonObject();
      slotJson.addProperty(slotType.getName(), slots);
      json.add("slots", slotJson);
    }
  }

  /** Base logic to write all relevant builder fields to JSON */
  protected abstract class ModifierFinishedRecipe extends AbstractFinishedRecipe {
    private final boolean withUnarmed;
    public ModifierFinishedRecipe(ResourceLocation ID, @Nullable ResourceLocation advancementID, boolean withUnarmed) {
      super(ID, advancementID);
      this.withUnarmed = withUnarmed;
    }

    public ModifierFinishedRecipe(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      this(ID, advancementID, false);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      writeCommon(json, withUnarmed);
      if (requirements != ModifierMatch.ALWAYS) {
        JsonObject reqJson = requirements.serialize();
        reqJson.addProperty("error", requirementsError);
        json.add("requirements", reqJson);
      }
      json.addProperty("allow_crystal", allowCrystal);
      json.add("result", result.toJson());
      if (maxLevel != 0) {
        json.addProperty("max_level", maxLevel);
      }
    }
  }

  /** Base logic to write all relevant builder fields to JSON */
  protected class SalvageFinishedRecipe extends AbstractFinishedRecipe {
    public SalvageFinishedRecipe(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      writeCommon(json, includeUnarmed);
      json.addProperty("modifier", result.getId().toString());
      json.addProperty("min_level", salvageMinLevel);
      if (salvageMaxLevel != 0) {
        json.addProperty("max_level", salvageMaxLevel);
      }
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerModifiers.modifierSalvageSerializer.get();
    }
  }
}
