package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** Shared logic between modifier and incremental modifier recipes */
public abstract class AbstractModifierRecipe implements ITinkerStationRecipe, IDisplayModifierRecipe {
  /** Error for when the tool is at the max modifier level */
  protected static final String KEY_MAX_LEVEL = Util.makeTranslationKey("recipe", "modifier.max_level");
  /** Error for when the tool has too few upgrade slots */
  protected static final String KEY_NOT_ENOUGH_UPGRADES = Util.makeTranslationKey("recipe", "modifier.not_enough_upgrades");
  /** Error for when the tool has too few ability slots */
  protected static final String KEY_NOT_ENOUGH_ABILITIES = Util.makeTranslationKey("recipe", "modifier.not_enough_abilities");
  /** Generic requirements error, for if a proper error is missing */
  protected static final ValidatedResult REQUIREMENTS_ERROR = ValidatedResult.failure(ModifierRecipeLookup.DEFAULT_ERROR_KEY);

  @Getter
  private final ResourceLocation id;
  /** Ingredient representing the required tool, typically a tag */
  protected final Ingredient toolRequirement;
  /** Modifiers that must match for this recipe */
  protected final ModifierMatch requirements;
  /** Error message to display if the requirements do not match */
  protected final String requirementsError;
  /** Modifier this recipe is adding */
  protected final ModifierEntry result;
  /** Maximum level of this modifier allowed */
  @Getter
  private final int maxLevel;
  /** Required ability slots to add this modifier */
  @Getter
  private final int upgradeSlots;
  /** Required ability slots to add this modifier */
  @Getter
  private final int abilitySlots;

  protected AbstractModifierRecipe(ResourceLocation id, Ingredient toolRequirement, ModifierMatch requirements,
                                   String requirementsError, ModifierEntry result, int maxLevel, int upgradeSlots, int abilitySlots) {
    this.id = id;
    this.toolRequirement = toolRequirement;
    this.requirements = requirements;
    this.requirementsError = requirementsError;
    this.result = result;
    this.maxLevel = maxLevel;
    this.upgradeSlots = upgradeSlots;
    this.abilitySlots = abilitySlots;
    ModifierRecipeLookup.addRequirement(result.getModifier(), requirements, requirementsError);
  }

  @Override
  public abstract ValidatedResult getValidatedResult(ITinkerStationInventory inv);

  /** @deprecated */
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }


  /* JEI display */
  /** Cache of display inputs */
  private List<List<ItemStack>> displayItems = null;
  /** Display result, may be a higher level than real result */
  private ModifierEntry displayResult;

  @Override
  public ModifierEntry getDisplayResult() {
    if (displayResult == null) {
      // if the recipe has a minimum level of this modifier, add that min level to the display result
      int min = requirements.getMinLevel(result.getModifier());
      if (min > 0) {
        displayResult = new ModifierEntry(result.getModifier(), result.getLevel() + min);
      } else {
        displayResult = result;
      }
    }
    return displayResult;
  }

  /**
   * Add extra ingredients for display in JEI
   * @param builder  Ingredient list builder
   */
  protected abstract void addIngredients(ImmutableList.Builder<List<ItemStack>> builder);

  @Override
  public List<List<ItemStack>> getDisplayItems() {
    if (displayItems == null) {
      // if empty requirement, assume any modifiable
      List<ItemStack> toolInputs = Arrays.stream(this.toolRequirement.getMatchingStacks()).map(stack -> {
        if (stack.getItem() instanceof ToolCore) {
          return ((ToolCore)stack.getItem()).buildToolForRendering();
        }
        return stack;
      }).collect(Collectors.toList());
      ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();
      // outputs
      builder.add(toolInputs.stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, requirements, result)).collect(Collectors.toList()));
      // inputs
      builder.add(toolInputs.stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, requirements, null)).collect(Collectors.toList()));
      addIngredients(builder);
      displayItems = builder.build();
    }
    return displayItems;
  }

  @Override
  public boolean hasRequirements() {
    return requirements != ModifierMatch.ALWAYS;
  }

  @Override
  public String getRequirementsError() {
    if (requirementsError.isEmpty()) {
      return ModifierRecipeLookup.DEFAULT_ERROR_KEY;
    }
    return requirementsError;
  }


  /* Helpers */

  /**
   * Validates that this tool meets the modifier requirements, is not too high of a level, and has enough upgrade/ability slots
   * @param tool           Tool stack instance
   * @return  Validated result with error, or pass if no error
   */
  protected ValidatedResult validatePrerequisites(ToolStack tool) {
    // validate modifier prereqs
    if (!requirements.test(tool.getModifierList())) {
      return requirementsError.isEmpty() ? REQUIREMENTS_ERROR : ValidatedResult.failure(requirementsError);
    }
    // max level of modifier
    if (maxLevel != 0 && tool.getUpgrades().getLevel(result.getModifier()) + result.getLevel() > maxLevel) {
      return ValidatedResult.failure(KEY_MAX_LEVEL, result.getModifier().getDisplayName(), maxLevel);
    }
    // ensure we have enough slots
    if (tool.getFreeUpgrades() < upgradeSlots) {
      return ValidatedResult.failure(KEY_NOT_ENOUGH_UPGRADES, upgradeSlots);
    }
    if (tool.getFreeAbilities() < abilitySlots) {
      return ValidatedResult.failure(KEY_NOT_ENOUGH_ABILITIES, abilitySlots);
    }
    return ValidatedResult.PASS;
  }

  /** Shared serializer logic */
  public static abstract class Serializer<T extends AbstractModifierRecipe> extends RecipeSerializer<T> {
    /**
     * Reads any remaining data from the modifier recipe
     * @return  Full recipe instance
     */
    public abstract T read(ResourceLocation id, JsonObject json, Ingredient toolRequirement, ModifierMatch requirements,
                           String requirementsError, ModifierEntry result, int maxLevel, int upgradeSlots, int abilitySlots);

    /**
     * Reads any remaining data from the modifier recipe
     * @return  Full recipe instance
     */
    public abstract T read(ResourceLocation id, PacketBuffer buffer, Ingredient toolRequirement, ModifierMatch requirements,
                           String requirementsError, ModifierEntry result, int maxLevel, int upgradeSlots, int abilitySlots);

    @Override
    public final T read(ResourceLocation id, JsonObject json) {
      Ingredient toolRequirement = Ingredient.deserialize(json.get("tools"));
      ModifierMatch requirements = ModifierMatch.ALWAYS;
      String requirementsError = "";
      if (json.has("requirements")) {
        JsonObject reqJson = JSONUtils.getJsonObject(json, "requirements");
        requirements = ModifierMatch.deserialize(reqJson);
        requirementsError = JSONUtils.getString(reqJson, "error", "");
      }
      ModifierEntry result = ModifierEntry.fromJson(JSONUtils.getJsonObject(json, "result"));
      int maxLevel = JSONUtils.getInt(json, "max_level", 0);
      if (maxLevel < 0) {
        throw new JsonSyntaxException("max must be non-negative");
      }
      int upgradeSlots = JSONUtils.getInt(json, "upgrade_slots", 0);
      if (upgradeSlots < 0) {
        throw new JsonSyntaxException("upgrade_slots must be non-negative");
      }
      int abilitySlots = JSONUtils.getInt(json, "ability_slots", 0);
      if (abilitySlots < 0) {
        throw new JsonSyntaxException("ability_slots must be non-negative");
      }
      if (upgradeSlots > 0 && abilitySlots > 0) {
        throw new JsonSyntaxException("Cannot set both upgrade_slots and ability_slots");
      }
      return read(id, json, toolRequirement, requirements, requirementsError, result, maxLevel, upgradeSlots, abilitySlots);
    }

    @Override
    public final T read(ResourceLocation id, PacketBuffer buffer) {
      Ingredient toolRequirement = Ingredient.read(buffer);
      ModifierMatch requirements = ModifierMatch.read(buffer);
      String requirementsError = buffer.readString(Short.MAX_VALUE);
      ModifierEntry result = ModifierEntry.read(buffer);
      int maxLevel = buffer.readVarInt();
      int upgradeSlots = buffer.readVarInt();
      int abilitySlots = buffer.readVarInt();
      return read(id, buffer, toolRequirement, requirements, requirementsError, result, maxLevel, upgradeSlots, abilitySlots);
    }

    /** Writes relevant packet data. When overriding, call super first for consistency with {@link #read(ResourceLocation, PacketBuffer)} */
    @Override
    public void write(PacketBuffer buffer, T recipe) {
      recipe.toolRequirement.write(buffer);
      recipe.requirements.write(buffer);
      buffer.writeString(recipe.requirementsError);
      recipe.result.write(buffer);
      buffer.writeVarInt(recipe.getMaxLevel());
      buffer.writeVarInt(recipe.getUpgradeSlots());
      buffer.writeVarInt(recipe.getAbilitySlots());
    }
  }
}
