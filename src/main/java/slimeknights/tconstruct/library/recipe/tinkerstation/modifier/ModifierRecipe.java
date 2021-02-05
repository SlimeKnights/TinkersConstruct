package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tinkering.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.List;

/**
 * Standard recipe to add a modifier
 */
@RequiredArgsConstructor
public class ModifierRecipe implements ITinkerStationRecipe {
  private static final String KEY_MAX_LEVEL = Util.makeTranslationKey("recipe", "modifier.max_level");
  private static final String KEY_NOT_ENOUGH_UPGRADES = Util.makeTranslationKey("recipe", "modifier.not_enough_upgrades");
  private static final String KEY_NOT_ENOUGH_ABILITIES = Util.makeTranslationKey("recipe", "modifier.not_enough_abilities");
  private static final String KEY_LOW_HARVEST_LEVEL = Util.makeTranslationKey("recipe", "modifier.low_harvest_level");

  @Getter
  private final ResourceLocation id;
  /**
   * List of input ingredients.
   * Order matters, as if a ingredient matches multiple ingredients it may produce unexpected behavior.
   * Making the most strict first will produce the best behavior
   */
  private final List<SizedIngredient> inputs;
  /** Modifier this recipe is adding */
  private final ModifierEntry result;
  /** Maximum level of this modifier allowed */
  private final int maxLevel;
  /** Required ability slots to add this modifier */
  private final int upgradeSlots;
  /** Required ability slots to add this modifier */
  private final int abilitySlots;
  /** Required harvest level to apply this modifier */
  private final int harvestLevel;

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
    // ensure it can be modified
    if (!(inv.getTinkerableStack().getItem() instanceof IModifiable)) {
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
    // harvest level check
    if (tool.getStats().getHarvestLevel() < harvestLevel) {
      return ValidatedResult.failure(KEY_LOW_HARVEST_LEVEL, HarvestLevels.getHarvestLevelName(harvestLevel));
    }

    // consume slots
    tool = tool.copy();
    ModDataNBT persistentData = tool.getPersistentData();
    persistentData.addUpgrades(-upgradeSlots);
    persistentData.addAbilities(-abilitySlots);

    // add modifier
    tool.addModifier(result.getModifier(), result.getLevel());
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
      int harvestLevel = JSONUtils.getInt(json, "min_harvest_level", -1);
      if (harvestLevel < -1) {
        throw new JsonSyntaxException("harvest_level must be -1 or above");
      }
      return new ModifierRecipe(id, ingredients, result, maxLevel, upgradeSlots, abilitySlots, harvestLevel);
    }

    @Nullable
    @Override
    public ModifierRecipe read(ResourceLocation id, PacketBuffer buffer) {
      int size = buffer.readVarInt();
      ImmutableList.Builder<SizedIngredient> builder = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        builder.add(SizedIngredient.read(buffer));
      }
      ModifierEntry result = ModifierEntry.read(buffer);
      int maxLevel = buffer.readVarInt();
      int upgradeSlots = buffer.readVarInt();
      int abilitySlots = buffer.readVarInt();
      int harvestLevel = buffer.readVarInt();
      return new ModifierRecipe(id, builder.build(), result, maxLevel, upgradeSlots, abilitySlots, harvestLevel);
    }

    @Override
    public void write(PacketBuffer buffer, ModifierRecipe recipe) {
      buffer.writeVarInt(recipe.inputs.size());
      for (SizedIngredient ingredient : recipe.inputs) {
        ingredient.write(buffer);
      }
      recipe.result.write(buffer);
      buffer.writeVarInt(recipe.maxLevel);
      buffer.writeVarInt(recipe.upgradeSlots);
      buffer.writeVarInt(recipe.abilitySlots);
      buffer.writeVarInt(recipe.harvestLevel);
    }
  }
}
