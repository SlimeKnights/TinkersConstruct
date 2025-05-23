package slimeknights.tconstruct.library.recipe.modifiers.adding;

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
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

import javax.annotation.Nullable;

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

  /**
   * Checks if the recipe matches and returns the located tool.
   * @param inv  Crafting inventory
   * @return  Found tool, or null if either the tool or overslime ingredient is absent
   */
  @Nullable
  private ItemStack findMatch(CraftingContainer inv) {
    boolean foundIngredient = false;
    ItemStack foundTool = null;
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
        // can't have two ingredients
        if (foundIngredient) {
          return null;
        }
        foundIngredient = true;
      } else {
        return null;
      }
    }
    // didn't find a match
    if (!foundIngredient || foundTool == null) {
      return null;
    }
    return foundTool;
  }

  @Override
  public boolean matches(CraftingContainer inv, Level level) {
    ItemStack match = findMatch(inv);
    if (match == null) {
      return false;
    }
    // found both tool and ingredient, ensure we need overslime
    ToolStack tool = ToolStack.from(match);
    OverslimeModifier overslime = TinkerModifiers.overslime.get();
    return overslime.getShield(tool) < overslime.getShieldCapacity(tool, tool.getModifier(overslime));
  }

  @Override
  public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
    ItemStack match = findMatch(inv);
    if (match == null) {
      TConstruct.LOG.error("Overslime crafting table recipe {} failed to find tool after matching", getId());
      return ItemStack.EMPTY;
    }
    ToolStack tool = ToolStack.copyFrom(match);
    ModifierId overslime = TinkerModifiers.overslime.getId();
    if (tool.getUpgrades().getLevel(overslime) == 0) {
      tool.addModifier(overslime, 1);
    }
    TinkerModifiers.overslime.get().addOverslime(tool, tool.getModifier(overslime), restoreAmount);
    return tool.copyStack(match);
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
