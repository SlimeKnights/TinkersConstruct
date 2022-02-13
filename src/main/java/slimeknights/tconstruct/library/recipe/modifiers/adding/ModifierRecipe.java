package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.Collections;
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
  protected final List<SizedIngredient> inputs;

  public ModifierRecipe(ResourceLocation id, List<SizedIngredient> inputs, Ingredient toolRequirement, int maxToolSize, ModifierMatch requirements, String requirementsError, ModifierEntry result, int maxLevel, @Nullable SlotCount slots) {
    super(id, toolRequirement, maxToolSize, requirements, requirementsError, result, maxLevel, slots);
    this.inputs = inputs;
  }

  /**
   * Creates the bitset used for marking inputs we do not care about
   * @param inv  Alloy tank
   * @return  Bitset
   */
  protected static BitSet makeBitset(ITinkerStationContainer inv) {
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
  protected static int findMatch(SizedIngredient ingredient, ITinkerStationContainer inv, BitSet used) {
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
  public boolean matches(ITinkerStationContainer inv, Level world) {
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
  public ValidatedResult getValidatedResult(ITinkerStationContainer inv) {
    ItemStack tinkerable = inv.getTinkerableStack();
    ToolStack tool = ToolStack.from(tinkerable);

    // common errors
    ValidatedResult commonError = validatePrerequisites(tool);
    if (commonError.hasError()) {
      return commonError;
    }

    // consume slots
    tool = tool.copy();
    ModDataNBT persistentData = tool.getPersistentData();
    SlotCount slots = getSlots();
    if (slots != null) {
      persistentData.addSlots(slots.getType(), -slots.getCount());
    }

    // add modifier
    tool.addModifier(result.getModifier(), result.getLevel());

    // ensure no modifier problems
    ValidatedResult toolValidation = tool.validate();
    if (toolValidation.hasError()) {
      return toolValidation;
    }

    return ValidatedResult.success(tool.createStack(Math.min(tinkerable.getCount(), shrinkToolSlotBy())));
  }

  /**
   * Updates the input stacks upon crafting this recipe
   * @param result  Result from {@link #assemble(ITinkerStationContainer)}. Generally should not be modified
   * @param inv     Inventory instance to modify inputs
   */
  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    // bit corresponding to items that are already found
    BitSet used = makeBitset(inv);
    // just shrink each input
    for (SizedIngredient ingredient : inputs) {
      // care about size, if too small just skip the recipe
      int index = findMatch(ingredient, inv, used);
      if (index != -1) {
        inv.shrinkInput(index, ingredient.getAmountNeeded());
      } else {
        TConstruct.LOG.warn("Missing ingredient in modifier recipe input consume");
      }
    }
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.modifierSerializer.get();
  }


  /* JEI display */

  @Override
  public int getInputCount() {
    return inputs.size();
  }

  @Override
  public List<ItemStack> getDisplayItems(int slot) {
    if (slot >= 0 && slot < inputs.size()) {
      return inputs.get(slot).getMatchingStacks();
    }
    return Collections.emptyList();
  }

  public static class Serializer extends AbstractModifierRecipe.Serializer<ModifierRecipe> {
    @Override
    public ModifierRecipe fromJson(ResourceLocation id, JsonObject json, Ingredient toolRequirement, int maxToolSize, ModifierMatch requirements,
                               String requirementsError, ModifierEntry result, int maxLevel, @Nullable SlotCount slots) {
      List<SizedIngredient> ingredients = JsonHelper.parseList(json, "inputs", SizedIngredient::deserialize);
      return new ModifierRecipe(id, ingredients, toolRequirement, maxToolSize, requirements, requirementsError, result, maxLevel, slots);
    }

    @Override
    public ModifierRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer, Ingredient toolRequirement, int maxToolSize, ModifierMatch requirements,
                               String requirementsError, ModifierEntry result, int maxLevel, @Nullable SlotCount slots) {
      int size = buffer.readVarInt();
      ImmutableList.Builder<SizedIngredient> builder = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        builder.add(SizedIngredient.read(buffer));
      }
      return new ModifierRecipe(id, builder.build(), toolRequirement, maxToolSize, requirements, requirementsError, result, maxLevel, slots);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, ModifierRecipe recipe) {
      super.toNetworkSafe(buffer, recipe);
      buffer.writeVarInt(recipe.inputs.size());
      for (SizedIngredient ingredient : recipe.inputs) {
        ingredient.write(buffer);
      }
    }
  }
}
