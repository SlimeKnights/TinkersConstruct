package slimeknights.tconstruct.library.recipe.tinkerstation.modifier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.BitSet;
import java.util.List;

/**
 * Standard recipe to add a modifier
 */
public class ModifierRecipe extends AbstractModifierRecipe {
  /**
   * List of input ingredients.
   * Order matters, as if a ingredient matches multiple ingredients it may produce unexpected behavior.
   * Making the most strict first will produce the best behavior
   */
  private final List<SizedIngredient> inputs;

  public ModifierRecipe(ResourceLocation id, List<SizedIngredient> inputs, Ingredient toolRequirement, ModifierMatch requirements, String requirementsError, ModifierEntry result, int maxLevel, int upgradeSlots, int abilitySlots) {
    super(id, toolRequirement, requirements, requirementsError, result, maxLevel, upgradeSlots, abilitySlots);
    this.inputs = inputs;

    // add all inputs to the modifier listing
    for (SizedIngredient ingredient : inputs) {
      ModifierRecipeLookup.addIngredient(ingredient);
    }
  }

  /**
   * Creates the bitset used for marking inputs we do not care about
   * @param inv  Alloy tank
   * @return  Bitset
   */
  private static BitSet makeBitset(ITinkerStationInventory inv) {
    int inputs = inv.getInputCount();
    BitSet used = new BitSet(inputs);
    // mark empty as used to save a bit of effort
    for (int i = 0; i < inputs; i++) {
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
    if (!this.toolRequirement.test(inv.getTinkerableStack())) {
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

    // ensure there are no unused inputs, makes recipes work together awkwardly
    for (int i = 0; i < inv.getInputCount(); i++) {
      if (!used.get(i) && !inv.getInput(i).isEmpty()) {
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

    // common errors
    ValidatedResult commonError = validatePrerequisites(tool);
    if (commonError.hasError()) {
      return commonError;
    }

    // consume slots
    tool = tool.copy();
    ModDataNBT persistentData = tool.getPersistentData();
    persistentData.addUpgrades(-getUpgradeSlots());
    persistentData.addAbilities(-getAbilitySlots());

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

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.modifierSerializer.get();
  }


  /* JEI display */

  @Override
  protected void addIngredients(Builder<List<ItemStack>> builder) {
    for (SizedIngredient ingredient : inputs) {
      builder.add(ingredient.getMatchingStacks());
    }
  }

  public static class Serializer extends AbstractModifierRecipe.Serializer<ModifierRecipe> {
    @Override
    public ModifierRecipe read(ResourceLocation id, JsonObject json, Ingredient toolRequirement, ModifierMatch requirements,
                               String requirementsError, ModifierEntry result, int maxLevel, int upgradeSlots, int abilitySlots) {
      List<SizedIngredient> ingredients = JsonHelper.parseList(json, "inputs", SizedIngredient::deserialize);
      return new ModifierRecipe(id, ingredients, toolRequirement, requirements, requirementsError, result, maxLevel, upgradeSlots, abilitySlots);
    }

    @Override
    public ModifierRecipe read(ResourceLocation id, PacketBuffer buffer, Ingredient toolRequirement, ModifierMatch requirements,
                               String requirementsError, ModifierEntry result, int maxLevel, int upgradeSlots, int abilitySlots) {
      int size = buffer.readVarInt();
      ImmutableList.Builder<SizedIngredient> builder = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        builder.add(SizedIngredient.read(buffer));
      }
      return new ModifierRecipe(id, builder.build(), toolRequirement, requirements, requirementsError, result, maxLevel, upgradeSlots, abilitySlots);
    }

    @Override
    public void write(PacketBuffer buffer, ModifierRecipe recipe) {
      super.write(buffer, recipe);
      buffer.writeVarInt(recipe.inputs.size());
      for (SizedIngredient ingredient : recipe.inputs) {
        ingredient.write(buffer);
      }
    }
  }
}
