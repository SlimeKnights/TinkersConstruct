package slimeknights.tconstruct.library.recipe.modifiers.adding;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class IncrementalModifierRecipe extends AbstractModifierRecipe {
  /** Input ingredient, size controled by later integers */
  private final Ingredient input;
  /** Number each input item counts as */
  private final int amountPerInput;
  /** Number needed for each level */
  private final int neededPerLevel;
  /** Item stack to use when a partial amount is leftover */
  private final ItemStack leftover;

  public IncrementalModifierRecipe(ResourceLocation id, Ingredient input, int amountPerInput, int neededPerLevel, Ingredient toolRequirement, int maxToolSize, ModifierMatch requirements, String requirementsError, ModifierEntry result, int maxLevel, @Nullable SlotCount slots, ItemStack leftover) {
    super(id, toolRequirement, maxToolSize, requirements, requirementsError, result, maxLevel, slots);
    this.input = input;
    this.amountPerInput = amountPerInput;
    this.neededPerLevel = neededPerLevel;
    this.leftover = leftover;
    ModifierRecipeLookup.setNeededPerLevel(result.getModifier(), neededPerLevel);
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level level) {
    // ensure this modifier can be applied
    if (!this.toolRequirement.test(inv.getTinkerableStack())) {
      return false;
    }
    return containsOnlyIngredient(inv, input);
  }

  @Override
  public ValidatedResult getValidatedResult(ITinkerStationContainer inv) {
    ItemStack tinkerable = inv.getTinkerableStack();
    ToolStack tool = ToolStack.from(tinkerable);

    // if the tool lacks the modifier, treat current as maxLevel, means we will add a new level
    Modifier modifier = result.getModifier();
    int current;
    if (tool.getUpgrades().getLevel(modifier) == 0) {
      current = neededPerLevel;
    } else {
      current = IncrementalModifier.getAmount(tool, modifier);
    }

    // can skip validations if we are not adding a new level
    if (current >= neededPerLevel) {
      ValidatedResult commonError = validatePrerequisites(tool);
      if (commonError.hasError()) {
        return commonError;
      }
    }

    // see how much value is available
    int available = getAvailableAmount(inv, input, amountPerInput);

    // if at the max, add a new level
    tool = tool.copy();
    ModDataNBT persistentData = tool.getPersistentData();
    if (current >= neededPerLevel) {
      // consume slots as we are adding a new level
      SlotCount slots = getSlots();
      if (slots != null) {
        persistentData.addSlots(slots.getType(), -slots.getCount());
      }

      // add up to 1 level of this to the tool
      IncrementalModifier.setAmount(persistentData, modifier, Math.min(available + current - neededPerLevel, neededPerLevel));
      tool.addModifier(result.getModifier(), result.getLevel());
    } else {
      // boost original based on the new level, and rebuild data so stats adjust
      IncrementalModifier.setAmount(persistentData, modifier, Math.min(current + available, neededPerLevel));
      tool.rebuildStats();
    }

    // successfully added the modifier
    return ValidatedResult.success(tool.createStack(Math.min(tinkerable.getCount(), shrinkToolSlotBy())));
  }

  /**
   * Updates the input stacks upon crafting this recipe
   * @param result  Result from {@link #assemble(ITinkerStationContainer)}. Generally should not be modified
   * @param inv     Inventory instance to modify inputs
   */
  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    ToolStack inputTool = ToolStack.from(inv.getTinkerableStack());
    ToolStack resultTool = ToolStack.from(result);

    // start by checking amount
    Modifier modifier = this.result.getModifier();
    int needed = IncrementalModifier.getAmount(resultTool, modifier);
    // if we had this modifier before, we can exclude what the original tool had
    int originalLevel = inputTool.getModifierLevel(modifier);
    if (originalLevel > 0) {
      needed -= IncrementalModifier.getAmount(inputTool, modifier);
    } else {
      needed -= neededPerLevel; // correction factor as adding a level counts as an extra neededPerLevel
    }
    // add in extra need if we increased levels
    int levelChange = resultTool.getModifierLevel(modifier) - originalLevel;
    if (levelChange > 0) {
      needed += levelChange * neededPerLevel / this.result.getLevel();
    }
    // subtract the inputs
    if (needed > 0) {
      updateInputs(inv, input, needed, amountPerInput, leftover);
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

  @Override
  protected void addIngredients(Consumer<List<ItemStack>> builder) {
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
      builder.accept(fullSize.get());
      needed -= maxStackSize;
    }
    // set proper stack size on remaining
    if (needed > 0) {
      int remaining = needed;
      builder.accept(items.stream().map(stack -> ItemHandlerHelper.copyStackWithSize(stack, remaining)).collect(Collectors.toList()));
    }
  }


  /* Helpers */

  /**
   * Checks if the inventory contains only the given ingredient
   * @param inv         Inventory to check
   * @param ingredient  Ingredient to try
   * @return  True if the inventory contains just this item
   */
  public static boolean containsOnlyIngredient(ITinkerStationContainer inv, Ingredient ingredient) {
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
        inv.giveItem(ItemHandlerHelper.copyStackWithSize(leftover, leftoverAmount * leftover.getCount()));
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

  /**
   * Reads the result from the given JSON
   * @param parent  Parent JSON
   * @param name    Tag name
   * @return  Item stack result
   * @throws com.google.gson.JsonSyntaxException If the syntax is invalid
   */
  public static ItemStack deseralizeResultItem(JsonObject parent, String name) {
    JsonElement element = JsonHelper.getElement(parent, name);
    if (element.isJsonPrimitive()) {
      return new ItemStack(GsonHelper.convertToItem(element, name));
    } else {
      return CraftingHelper.getItemStack(GsonHelper.convertToJsonObject(element, name), true);
    }
  }

  public static class Serializer extends AbstractModifierRecipe.Serializer<IncrementalModifierRecipe> {
    @Override
    public IncrementalModifierRecipe fromJson(ResourceLocation id, JsonObject json, Ingredient toolRequirement, int maxToolSize, ModifierMatch requirements,
                                          String requirementsError, ModifierEntry result, int maxLevel, @Nullable SlotCount slots) {
      Ingredient input = Ingredient.fromJson(JsonHelper.getElement(json, "input"));
      int amountPerInput = GsonHelper.getAsInt(json, "amount_per_item", 1);
      if (amountPerInput < 1) {
        throw new JsonSyntaxException("amount_per_item must be positive");
      }
      int neededPerLevel = GsonHelper.getAsInt(json, "needed_per_level");
      if (neededPerLevel <= amountPerInput) {
        throw new JsonSyntaxException("needed_per_level must be greater than amount_per_item");
      }
      ItemStack leftover = ItemStack.EMPTY;
      if (amountPerInput > 1 && json.has("leftover")) {
        leftover = deseralizeResultItem(json, "leftover");
      }
      return new IncrementalModifierRecipe(id, input, amountPerInput, neededPerLevel, toolRequirement, maxToolSize, requirements, requirementsError, result, maxLevel, slots, leftover);
    }

    @Override
    public IncrementalModifierRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer, Ingredient toolRequirement, int maxToolSize, ModifierMatch requirements,
                                          String requirementsError, ModifierEntry result, int maxLevel, @Nullable SlotCount slots) {
      Ingredient input = Ingredient.fromNetwork(buffer);
      int amountPerInput = buffer.readVarInt();
      int neededPerLevel = buffer.readVarInt();
      ItemStack leftover = buffer.readItem();
      return new IncrementalModifierRecipe(id, input, amountPerInput, neededPerLevel, toolRequirement, maxToolSize, requirements, requirementsError, result, maxLevel, slots, leftover);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, IncrementalModifierRecipe recipe) {
      super.toNetworkSafe(buffer, recipe);
      recipe.input.toNetwork(buffer);
      buffer.writeVarInt(recipe.amountPerInput);
      buffer.writeVarInt(recipe.neededPerLevel);
      buffer.writeItem(recipe.leftover);
    }
  }
}
