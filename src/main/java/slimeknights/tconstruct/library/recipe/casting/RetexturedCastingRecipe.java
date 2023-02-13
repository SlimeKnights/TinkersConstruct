package slimeknights.tconstruct.library.recipe.casting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.mantle.item.RetexturedBlockItem;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

/** Extension of item recipe that sets the result block to the input block */
public abstract class RetexturedCastingRecipe extends ItemCastingRecipe {
  public RetexturedCastingRecipe(RecipeType<?> type, ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
    super(type, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
  }

  @Override
  public ItemStack assemble(ICastingContainer inv) {
    ItemStack result = getResultItem().copy();
    if (inv.getStack().getItem() instanceof BlockItem blockItem ) {
      return RetexturedBlockItem.setTexture(result, blockItem.getBlock());
    }
    return result;
  }

  /** Subclass for basin recipes */
  public static class Basin extends RetexturedCastingRecipe {
    public Basin(ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
      super(TinkerRecipeTypes.CASTING_BASIN.get(), id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinRecipeSerializer.get();
    }
  }

  /** Subclass for table recipes */
  public static class Table extends RetexturedCastingRecipe {
    public Table(ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
      super(TinkerRecipeTypes.CASTING_TABLE.get(), id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableRecipeSerializer.get();
    }
  }
}
