package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.common.collect.ImmutableList;
import com.google.common.math.IntMath;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.JsonUtils;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IncrementalModifierRecipe extends AbstractModifierRecipe {
  public static final RecordLoadable<IncrementalModifierRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    IngredientLoadable.DISALLOW_EMPTY.requiredField("input", r -> r.input),
    IntLoadable.FROM_ONE.defaultField("amount_per_item", 1, true, r -> r.amountPerInput),
    IntLoadable.FROM_ONE.requiredField("needed_per_level", r -> r.neededPerLevel),
    TOOLS_FIELD, MAX_TOOL_SIZE_FIELD, RESULT_FIELD, LEVEL_FIELD, SLOTS_FIELD,
    ItemOutput.Loadable.OPTIONAL_STACK.emptyField("leftover", r -> r.leftover),
    ALLOW_CRYSTAL_FIELD, CHECK_TRAIT_LEVEL_FIELD,
    IncrementalModifierRecipe::new);


  /** Input ingredient, size controlled by later integers */
  private final Ingredient input;
  /** Number each input item counts as */
  private final int amountPerInput;
  /** Number needed for each level */
  private final int neededPerLevel;
  /** Item stack to use when a partial amount is leftover */
  private final ItemOutput leftover;

  public IncrementalModifierRecipe(ResourceLocation id, Ingredient input, int amountPerInput, int neededPerLevel, Ingredient toolRequirement, int maxToolSize, ModifierId result, IntRange level, @Nullable SlotCount slots, ItemOutput leftover, boolean allowCrystal, boolean checkTraitLevel) {
    super(id, toolRequirement, maxToolSize, result, level, slots, allowCrystal, checkTraitLevel);
    this.input = input;
    this.amountPerInput = amountPerInput;
    this.neededPerLevel = neededPerLevel;
    this.leftover = leftover;
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level level) {
    // ensure this modifier can be applied
    if (!result.isBound() || !this.toolRequirement.test(inv.getTinkerableStack())) {
      return false;
    }
    return matchesCrystal(inv) || containsOnlyIngredient(inv, input);
  }

  @Override
  public RecipeResult<ItemStack> getValidatedResult(ITinkerStationContainer inv) {
    ToolStack tool = inv.getTinkerable();

    // fetch the amount from the modifier, will be 0 if we have a full level
    ModifierId modifier = result.getId();
    boolean newLevel = tool.getUpgrades().getEntry(modifier).getAmount(0) <= 0;

    // can skip validations if we are not adding a new level, crystals always add one
    boolean crystal = matchesCrystal(inv);
    if (crystal || newLevel) {
      Component commonError = validatePrerequisites(tool);
      if (commonError != null) {
        return RecipeResult.failure(commonError);
      }
    }

    // if at the max, add a new level
    tool = tool.copy();

    // if a new level, consume slots now that we copied
    if (crystal || newLevel) {
      SlotCount slots = getSlots();
      if (slots != null) {
        tool.getPersistentData().addSlots(slots.type(), -slots.count());
      }
    }

    // crystal adds 1 level, does not care about amount
    if (crystal) {
      tool.addModifier(modifier, 1);
    } else {
      // for adding amount, we just use the convenient helper method, which will automatically stop at max
      tool.addModifierAmount(modifier, getAvailableAmount(inv, input, amountPerInput), neededPerLevel);
    }

    // successfully added the modifier
    return RecipeResult.success(tool.createStack(Math.min(inv.getTinkerableSize(), shrinkToolSlotBy())));
  }

  /**
   * Updates the input stacks upon crafting this recipe
   * @param result  Result from {@link #assemble(ITinkerStationContainer)}. Generally should not be modified
   * @param inv     Inventory instance to modify inputs
   */
  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    // if its a crystal, just shrink the crystal
    if (matchesCrystal(inv)) {
      super.updateInputs(result, inv, isServer);
      return;
    }

    // fetch the differences
    ToolStack inputTool = inv.getTinkerable();
    ToolStack resultTool = ToolStack.from(result);
    ModifierId modifier = this.result.getId();
    ModifierEntry inputEntry = inputTool.getUpgrades().getEntry(modifier);
    ModifierEntry resultEntry = resultTool.getUpgrades().getEntry(modifier);

    // if we had no partial level before, we just need to consume what we saw on the result
    int inputNeed = inputEntry.getNeeded();
    // if the input had no incremental or it matched the result, life is easy
    if (inputNeed == 0 || inputNeed == neededPerLevel) {
      // just directly consume based on the difference
      updateInputs(inv, input, resultEntry.getAmount(neededPerLevel) - inputEntry.getAmount(0), amountPerInput, leftover.get());
    } else {
      // so the sizes mismatch, need to rescale the input, the result, and the amount consumed per item
      int gcd = IntMath.gcd(inputNeed, neededPerLevel);
      int recipeScale = inputNeed / gcd;
      int used = (resultEntry.getAmount(neededPerLevel) * recipeScale) - (inputEntry.getAmount(0) * neededPerLevel / gcd);
      // we need the final result to be in terms of the input sizes. We could scale amountPerInput, but that will lead to many leftovers
      // instead what we do is a ceiling divide by adding the divisor-1, ensures we consume a bit too much instead of a bit too little with mismatching needs
      updateInputs(inv, input, (used + recipeScale - 1) / recipeScale, amountPerInput, leftover.get());
    }
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.incrementalModifierSerializer.get();
  }


  /* JEI display */

  @Override
  public boolean isIncremental() {
    return true;
  }

  /** Cache of the list of items for each slot */
  private List<List<ItemStack>> slotCache;

  /** Gets the list of input stacks for display */
  private List<List<ItemStack>> getInputs() {
    if (slotCache == null) {
      ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();

      // fill extra item slots
      List<ItemStack> items = Arrays.asList(input.getItems());
      int maxStackSize = items.stream().mapToInt(ItemStack::getMaxStackSize).min().orElse(64);

      // split the stacks out if we need more than 1
      int needed = neededPerLevel / amountPerInput;
      if (neededPerLevel % amountPerInput > 0) {
        needed++;
      }
      Lazy<List<ItemStack>> fullSize = Lazy.of(() -> items.stream().map(stack -> ItemHandlerHelper.copyStackWithSize(stack, maxStackSize)).collect(Collectors.toList()));
      while (needed > maxStackSize) {
        builder.add(fullSize.get());
        needed -= maxStackSize;
      }
      // set proper stack size on remaining
      if (needed > 0) {
        int remaining = needed;
        builder.add(items.stream().map(stack -> ItemHandlerHelper.copyStackWithSize(stack, remaining)).collect(Collectors.toList()));
      }
      slotCache = builder.build();
    }
    return slotCache;
  }

  @Override
  public int getInputCount() {
    return getInputs().size();
  }

  @Override
  public List<ItemStack> getDisplayItems(int slot) {
    List<List<ItemStack>> inputs = getInputs();
    if (slot >= 0 && slot < inputs.size()) {
      return inputs.get(slot);
    }
    return Collections.emptyList();
  }

  /* Helpers */

  /**
   * Checks if the inventory contains only the given ingredient
   * @param inv         Inventory to check
   * @param ingredient  Ingredient to try
   * @return  True if the inventory contains just this item
   */
  public static boolean containsOnlyIngredient(ITinkerableContainer inv, Ingredient ingredient) {
    boolean found = false;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty()) {
        // need at least 1 match
        if (ingredient.test(stack)) {
          found = true;
        } else {
          // any wrong items cause it to fail
          return false;
        }
      }
    }
    // goal of matches is to see if this works for any tool, so ignore current tool NBT
    return found;
  }

  /**
   * Determines how much value there is in the inventory
   * @param inv            Inventory
   * @param ingredient     Ingredient matching items
   * @param amountPerItem  Amount each item in the inventory is worth
   * @return  Total value in the inventory
   */
  public static int getAvailableAmount(ITinkerStationContainer inv, Ingredient ingredient, int amountPerItem) {
    int available = 0;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && ingredient.test(stack)) {
        available += stack.getCount() * amountPerItem;
      }
    }
    return available;
  }

  /**
   * Updates the inputs based on the given ingredient
   * @param inv             Inventory instance
   * @param ingredient      Ingredient
   * @param amountNeeded    Total number needed
   * @param amountPerInput  Number each item gives
   * @param leftover        Itemstack to use if amountNeeded is too much to match amountPerInput
   */
  public static void updateInputs(IMutableTinkerStationContainer inv, Ingredient ingredient, int amountNeeded, int amountPerInput, ItemStack leftover) {
    int itemsNeeded = amountNeeded / amountPerInput;
    int leftoverAmount = amountNeeded % amountPerInput;
    if (leftoverAmount > 0) {
      itemsNeeded++;
      if (!leftover.isEmpty()) {
        // leftoverAmount refers to how many we need to that is does not fit cleanly into amountPerInput
        // but we want to return the amount we did not use, hence the subtraction
        inv.giveItem(ItemHandlerHelper.copyStackWithSize(leftover, (amountPerInput - leftoverAmount) * leftover.getCount()));
      }
    }
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && ingredient.test(stack)) {
        int count = stack.getCount();
        // if this stack fully covers the remaining needs, done
        if (count > itemsNeeded) {
          inv.shrinkInput(i, itemsNeeded);
          break;
        }
        // otherwise, clear stack and try the next stack
        inv.shrinkInput(i, count);
        itemsNeeded -= count;
      }
    }
  }

  /** @deprecated use {@link slimeknights.mantle.data.loadable.common.ItemStackLoadable#REQUIRED_STACK_NBT} */
  @SuppressWarnings("removal")
  @Deprecated(forRemoval = true)
  public static ItemStack deseralizeResultItem(JsonObject parent, String name) {
    return JsonUtils.getAsItemStack(parent, name);
  }
}
