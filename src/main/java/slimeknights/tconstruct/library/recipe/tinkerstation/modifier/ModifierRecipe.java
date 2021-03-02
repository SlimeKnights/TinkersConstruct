package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Standard recipe to add a modifier
 */
public class ModifierRecipe implements ITinkerStationRecipe, IDisplayModifierRecipe {
  private static final String KEY_MAX_LEVEL = Util.makeTranslationKey("recipe", "modifier.max_level");
  private static final String KEY_NOT_ENOUGH_UPGRADES = Util.makeTranslationKey("recipe", "modifier.not_enough_upgrades");
  private static final String KEY_NOT_ENOUGH_ABILITIES = Util.makeTranslationKey("recipe", "modifier.not_enough_abilities");
  private static final ValidatedResult REQUIREMENTS_ERROR = ValidatedResult.failure(ModifierRequirementLookup.DEFAULT_ERROR_KEY);

  @Getter
  private final ResourceLocation id;
  /**
   * List of input ingredients.
   * Order matters, as if a ingredient matches multiple ingredients it may produce unexpected behavior.
   * Making the most strict first will produce the best behavior
   */
  private final List<SizedIngredient> inputs;
  /** Ingredient representing the required tool, typically a tag */
  private final Ingredient toolRequirement;
  /** Modifiers that must match for this recipe */
  private final ModifierMatch requirements;
  /** Error message to display if the requirements do not match */
  private final String requirementsError;
  /** Modifier this recipe is adding */
  private final ModifierEntry result;
  /** Maximum level of this modifier allowed */
  @Getter
  private final int maxLevel;
  /** Required ability slots to add this modifier */
  @Getter
  private final int upgradeSlots;
  /** Required ability slots to add this modifier */
  @Getter
  private final int abilitySlots;

  public ModifierRecipe(ResourceLocation id, List<SizedIngredient> inputs, Ingredient toolRequirement, ModifierMatch requirements, String requirementsError, ModifierEntry result, int maxLevel, int upgradeSlots, int abilitySlots) {
    this.id = id;
    this.inputs = inputs;
    this.toolRequirement = toolRequirement;
    this.requirements = requirements;
    this.requirementsError = requirementsError;
    this.result = result;
    this.maxLevel = maxLevel;
    this.upgradeSlots = upgradeSlots;
    this.abilitySlots = abilitySlots;
    // if this recipe depends on the same modifier as the output, may cause inconsistencies in the requirements check
    // main reason this is true is when each level of a modifier requires a different item, in that case the requirement is just for internal calculations
    if (requirements != ModifierMatch.ALWAYS && requirements.getMinLevel(result.getModifier()) == 0) {
      ModifierRequirementLookup.addRequirement(result.getModifier(), requirements, requirementsError);
    }
  }

  /**
   * Creates the bitset used for marking inputs we do not care about
   * @param inv  Alloy tank
   * @return  Bitset
   */
  private static BitSet makeBitset(ITinkerStationInventory inv) {
    int tanks = inv.getInputCount();
    BitSet used = new BitSet(tanks);
    // mark empty as used to save a bit of effort
    for (int i = 0; i < tanks; i++) {
      if (inv.getInput(i).isEmpty()) {
        used.set(i);
      }
    }
    return used;
  }

  /**
   * Finds a match for the given ingredient
   * @param ingredient  Ingredient to check
   * @param inv         Alloy tank to search
   * @param used        Bitset for already used matches, will be modified
   * @return  Index of found match, or -1 if match not found
   */
  private static int findMatch(SizedIngredient ingredient, ITinkerStationInventory inv, BitSet used) {
    ItemStack stack;
    for (int i = 0; i < inv.getInputCount(); i++) {
      // must not have used that fluid yet
      if (!used.get(i)) {
        stack = inv.getInput(i);
        if (ingredient.test(stack)) {
          used.set(i);
          return i;
        }
      }
    }
    return -1;
  }

  @Override
  public boolean matches(ITinkerStationInventory inv, World world) {
    // ensure this modifier can be applied
    ItemStack tinkerable = inv.getTinkerableStack();
    if (this.toolRequirement == Ingredient.EMPTY) {
      // if not specified, match anything modifiable
      if (!TinkerTags.Items.MODIFIABLE.contains(tinkerable.getItem())) {
        return false;
      }
    } else if (!this.toolRequirement.test(tinkerable)) {
      return false;
    }

    // check inputs
    BitSet used = makeBitset(inv);
    for (SizedIngredient ingredient : inputs) {
      int index = findMatch(ingredient, inv, used);
      if (index == -1) {
        return false;
      }
    }
    // goal of matches is to see if this works for any tool, so ignore current tool NBT
    return true;
  }

  /**
   * Gets the recipe result, or an object containing an error message if the recipe matches but cannot be applied.
   * @return Validated result
   */
  @Override
  public ValidatedResult getValidatedResult(ITinkerStationInventory inv) {
    // TODO: this only works for tool core, generalize
    ToolStack tool = ToolStack.from(inv.getTinkerableStack());

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

    // consume slots
    tool = tool.copy();
    ModDataNBT persistentData = tool.getPersistentData();
    persistentData.addUpgrades(-upgradeSlots);
    persistentData.addAbilities(-abilitySlots);

    // add modifier
    tool.addModifier(result.getModifier(), result.getLevel());

    // ensure no modifier problems
    ValidatedResult toolValidation = tool.validate();
    if (toolValidation.hasError()) {
      return toolValidation;
    }

    return ValidatedResult.success(tool.createStack());
  }

  /**
   * Updates the input stacks upon crafting this recipe
   * @param result  Result from {@link #getCraftingResult(ITinkerStationInventory)}. Generally should not be modified
   * @param inv     Inventory instance to modify inputs
   */
  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationInventory inv) {
    // bit corresponding to items that are already found
    BitSet used = makeBitset(inv);
    // just shrink each input
    for (SizedIngredient ingredient : inputs) {
      // care about size, if too small just skip the recipe
      int index = findMatch(ingredient, inv, used);
      if (index != -1) {
        inv.shrinkInput(index, ingredient.getAmountNeeded());
      } else {
        TConstruct.log.warn("Missing ingredient in modifier recipe input consume");
      }
    }
  }


  /* JEI display */
  /** Cache of display inputs */
  private List<List<ItemStack>> displayItems = null;
  /** Cache of display tool with modifier */
  private List<List<ItemStack>> displayTool = null;
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

  @Override
  public List<List<ItemStack>> getDisplayItems() {
    if (displayItems == null) {
      // if empty requirement, assume any modifiable
      Stream<Item> itemStream;
      if (this.toolRequirement == Ingredient.EMPTY) {
        itemStream = IDisplayModifierRecipe.getAllModifiable();
      } else {
        itemStream = Arrays.stream(this.toolRequirement.getMatchingStacks()).map(ItemStack::getItem);
      }
      List<ItemStack> toolInputs = itemStream.map(MAP_TOOL_FOR_RENDERING).collect(Collectors.toList());
      List<ItemStack> inputTools = toolInputs.stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, requirements, null)).collect(Collectors.toList());
      List<ItemStack> outputTools = toolInputs.stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, requirements, result)).collect(Collectors.toList());
      // stream of itemstack lists
      displayItems = Streams.concat(Stream.of(outputTools), Stream.of(inputTools), inputs.stream().map(SizedIngredient::getMatchingStacks)).collect(Collectors.toList());
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
      return ModifierRequirementLookup.DEFAULT_ERROR_KEY;
    }
    return requirementsError;
  }

  /** @deprecated */
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.modifierSerializer.get();
  }

  public static class Serializer extends RecipeSerializer<ModifierRecipe> {
    @Override
    public ModifierRecipe read(ResourceLocation id, JsonObject json) {
      List<SizedIngredient> ingredients = JsonHelper.parseList(json, "inputs", SizedIngredient::deserialize);
      Ingredient toolRequirement = Ingredient.EMPTY;
      if (json.has("tools")) {
        toolRequirement = Ingredient.deserialize(json.get("tools"));
      }
      ModifierMatch requirements = ModifierMatch.ALWAYS;
      String requirementsError = "";
      if (json.has("requirements")) {
        JsonObject reqJson = JSONUtils.getJsonObject(json, "requirements");
        requirements = ModifierMatch.deserialize(reqJson);
        requirementsError = JSONUtils.getString(reqJson, "error", "");
      }
      ModifierEntry result = ModifierEntry.fromJson(JSONUtils.getJsonObject(json, "result"));
      int upgradeSlots = JSONUtils.getInt(json, "upgrade_slots", 0);
      if (upgradeSlots < 0) {
        throw new JsonSyntaxException("upgrade_slots must be non-negative");
      }
      int maxLevel = JSONUtils.getInt(json, "max_level", 0);
      if (maxLevel < 0) {
        throw new JsonSyntaxException("max must be non-negative");
      }
      int abilitySlots = JSONUtils.getInt(json, "ability_slots", 0);
      if (abilitySlots < 0) {
        throw new JsonSyntaxException("ability_slots must be non-negative");
      }
      if (upgradeSlots > 0 && abilitySlots > 0) {
        throw new JsonSyntaxException("Cannot set both upgrade_slots and ability_slots");
      }
      return new ModifierRecipe(id, ingredients, toolRequirement, requirements, requirementsError, result, maxLevel, upgradeSlots, abilitySlots);
    }

    @Nullable
    @Override
    public ModifierRecipe read(ResourceLocation id, PacketBuffer buffer) {
      int size = buffer.readVarInt();
      ImmutableList.Builder<SizedIngredient> builder = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        builder.add(SizedIngredient.read(buffer));
      }
      Ingredient toolRequirement = Ingredient.read(buffer);
      ModifierMatch requirements = ModifierMatch.read(buffer);
      String requirementsError = buffer.readString(Short.MAX_VALUE);
      ModifierEntry result = ModifierEntry.read(buffer);
      int maxLevel = buffer.readVarInt();
      int upgradeSlots = buffer.readVarInt();
      int abilitySlots = buffer.readVarInt();
      return new ModifierRecipe(id, builder.build(), toolRequirement, requirements, requirementsError, result, maxLevel, upgradeSlots, abilitySlots);
    }

    @Override
    public void write(PacketBuffer buffer, ModifierRecipe recipe) {
      buffer.writeVarInt(recipe.inputs.size());
      for (SizedIngredient ingredient : recipe.inputs) {
        ingredient.write(buffer);
      }
      recipe.toolRequirement.write(buffer);
      recipe.requirements.write(buffer);
      buffer.writeString(recipe.requirementsError);
      recipe.result.write(buffer);
      buffer.writeVarInt(recipe.maxLevel);
      buffer.writeVarInt(recipe.upgradeSlots);
      buffer.writeVarInt(recipe.abilitySlots);
    }
  }
}
