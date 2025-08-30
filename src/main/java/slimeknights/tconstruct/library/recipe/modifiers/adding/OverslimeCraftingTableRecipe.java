package slimeknights.tconstruct.library.recipe.modifiers.adding;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/** Recipe for applying overslime in the crafting table */
public class OverslimeCraftingTableRecipe extends CustomRecipe {
  public static final RecordLoadable<OverslimeCraftingTableRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    IngredientLoadable.DISALLOW_EMPTY.defaultField("tools", Ingredient.of(TinkerTags.Items.DURABILITY), r -> r.tools),
    IngredientLoadable.DISALLOW_EMPTY.requiredField("ingredient", r -> r.ingredient),
    IntLoadable.FROM_ONE.requiredField("restore_amount", r -> r.restoreAmount),
    OverslimeCraftingTableRecipe::new);

  private final Ingredient tools;
  private final Ingredient ingredient;
  private final int restoreAmount;

  public OverslimeCraftingTableRecipe(ResourceLocation id, Ingredient tools, Ingredient ingredient, int restoreAmount) {
    super(id, CraftingBookCategory.EQUIPMENT);
    this.tools = tools;
    this.ingredient = ingredient;
    this.restoreAmount = restoreAmount;
  }

  /** Result from {@link #findTool(CraftingContainer, Predicate, Ingredient)} */
  public record ToolFound(ItemStack tool, int itemsFound) {}

  /**
   * Checks if the recipe matches and returns the located tool and the number of ingredient matches.
   * @param inv         Crafting inventory
   * @param tools       Tool predicate
   * @param ingredient  Allowed non-tool ingredient
   * @return  Found tool, or null if either the tool or overslime ingredient is absent
   */
  @Nullable
  public static ToolFound findTool(CraftingContainer inv, Predicate<ItemStack> tools, Ingredient ingredient) {
    ItemStack foundTool = null;
    int itemsFound = 0;
    for (int i = 0; i < inv.getContainerSize(); i++) {
      ItemStack stack = inv.getItem(i);
      if (stack.isEmpty()) {
        continue;
      }
      // stack is allowed to be either our tool or our ingredient, anything else fails to match
      if (tools.test(stack)) {
        // can't have two tools
        if (foundTool != null) {
          return null;
        }
        foundTool = stack;
      } else if (ingredient.test(stack)) {
        itemsFound++;
      } else {
        // unknown item input
        return null;
      }
    }
    // didn't find a match
    if (itemsFound == 0 || foundTool == null) {
      return null;
    }
    return new ToolFound(foundTool, itemsFound);
  }

  @Override
  public boolean matches(CraftingContainer inv, Level level) {
    ToolFound match = findTool(inv, tools, ingredient);
    if (match == null) {
      return false;
    }
    // found both tool and ingredient, ensure we need overslime
    ToolStack tool = ToolStack.from(match.tool);
    // no adding overslime via this recipe, only refilling it
    // mostly simplifies some of the craft remainder logic
    return tool.getModifierLevel(TinkerModifiers.overslime.getId()) > 0 || OverslimeModule.INSTANCE.getAmount(tool) < OverslimeModule.getCapacity(tool);
  }

  @Override
  public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
    ToolFound match = findTool(inv, tools, ingredient);
    if (match == null) {
      TConstruct.LOG.error("Overslime crafting table recipe {} failed to find tool after matching", getId());
      return ItemStack.EMPTY;
    }
    ToolStack tool = ToolStack.copyFrom(match.tool);
    OverslimeModule.INSTANCE.addAmount(tool, match.itemsFound * restoreAmount);
    return tool.copyStack(match.tool);
  }

  /** Gets the remaining items after repairing the necessary number of times */
  public static NonNullList<ItemStack> getRemainingItems(CraftingContainer inv, Ingredient ingredient, int repairNeeded, int repairPerItem) {
    NonNullList<ItemStack> list = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);
    for (int i = 0; i < inv.getContainerSize(); i++) {
      ItemStack stack = inv.getItem(i);
      if (ingredient.test(stack)) {
        // if done repairing, leave the items
        if (repairNeeded <= 0) {
          list.set(i, stack.copyWithCount(1));
          continue;
        }
        repairNeeded -= repairPerItem;
      }
      if (stack.hasCraftingRemainingItem()) {
        list.set(i, stack.getCraftingRemainingItem());
      }
    }
    return list;
  }

  @Override
  public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
    // step 1: find out how much we need to repair
    ToolFound inputs = findTool(inv, tools, ingredient);
    int repairNeeded = 0;
    int repairPerItem = restoreAmount;
    if (inputs != null) {
      ToolStack tool = ToolStack.from(inputs.tool);
      repairNeeded = OverslimeModule.getCapacity(tool) - OverslimeModule.INSTANCE.getAmount(tool);
      repairPerItem *= OverslimeModule.getOverworkedBonus(tool);
    }

    // step 2: consume as many items as are needed to do the repair
    return getRemainingItems(inv, ingredient, repairNeeded, repairPerItem);
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.craftingOverslimeSerializer.get();
  }
}
