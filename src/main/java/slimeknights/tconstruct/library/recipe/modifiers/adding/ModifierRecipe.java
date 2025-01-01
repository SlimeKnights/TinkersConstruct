package slimeknights.tconstruct.library.recipe.modifiers.adding;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
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
  protected static final LoadableField<List<SizedIngredient>,ModifierRecipe> INPUTS_FIELD = SizedIngredient.LOADABLE.list(1).requiredField("inputs", r -> r.inputs);
  public static final RecordLoadable<ModifierRecipe> LOADER = RecordLoadable.create(ContextKey.ID.requiredField(), INPUTS_FIELD, TOOLS_FIELD, MAX_TOOL_SIZE_FIELD, RESULT_FIELD, LEVEL_FIELD, SLOTS_FIELD, ALLOW_CRYSTAL_FIELD, CHECK_TRAIT_LEVEL_FIELD, ModifierRecipe::new);

  /**
   * List of input ingredients.
   * Order matters, as if a ingredient matches multiple ingredients it may produce unexpected behavior.
   * Making the most strict first will produce the best behavior
   */
  protected final List<SizedIngredient> inputs;

  public ModifierRecipe(ResourceLocation id, List<SizedIngredient> inputs, Ingredient toolRequirement, int maxToolSize, ModifierId result, IntRange level, @Nullable SlotCount slots, boolean allowCrystal, boolean checkTraitLevel) {
    super(id, toolRequirement, maxToolSize, result, level, slots, allowCrystal, checkTraitLevel);
    this.inputs = inputs;
  }

    /**
     * Creates the bitset used for marking inputs we do not care about
     * @param inv  Alloy tank
     * @return  Bitset
     */
  protected static BitSet makeBitset(ITinkerableContainer inv) {
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
  protected static int findMatch(SizedIngredient ingredient, ITinkerableContainer inv, BitSet used) {
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

  /**
   * Tries to match the given list of ingredients to the inventory
   * @param inv     Inventory to check
   * @param inputs  List of inputs to check
   * @return True if a match
   */
  public static boolean checkMatch(ITinkerableContainer inv, List<SizedIngredient> inputs) {
    if (inputs.isEmpty()) {
      return false;
    }
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

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    // ensure this modifier can be applied
    if (!result.isBound() || !this.toolRequirement.test(inv.getTinkerableStack())) {
      return false;
    }
    return matchesCrystal(inv) || checkMatch(inv, inputs);
  }

  /**
   * Gets the recipe result, or an object containing an error message if the recipe matches but cannot be applied.
   * @return Validated result
   */
  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    ToolStack tool = inv.getTinkerable();

    // common errors
    Component commonError = validatePrerequisites(tool);
    if (commonError != null) {
      return RecipeResult.failure(commonError);
    }

    // consume slots
    tool = tool.copy();
    ModDataNBT persistentData = tool.getPersistentData();
    SlotCount slots = getSlots();
    if (slots != null) {
      persistentData.addSlots(slots.type(), -slots.count());
    }

    // add modifier
    tool.addModifier(result.getId(), 1);

    // ensure no modifier problems
    Component toolValidation = tool.tryValidate();
    if (toolValidation != null) {
      return RecipeResult.failure(toolValidation);
    }

    return RecipeResult.success(LazyToolStack.from(tool, Math.min(inv.getTinkerableSize(), shrinkToolSlotBy())));
  }

  /** Updates all inputs in the given container */
  public static void updateInputs(ITinkerableContainer.Mutable inv, List<SizedIngredient> inputs) {
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
  public void updateInputs(LazyToolStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    // if it's a crystal, just shrink the crystal
    if (matchesCrystal(inv)) {
      super.updateInputs(result, inv, isServer);
    } else {
      updateInputs(inv, inputs);
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
}
