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

import javax.annotation.Nullable;
import java.util.function.Supplier;

/** Extension of item recipe that sets the result block to the input block */
public class RetexturedCastingRecipe extends ItemCastingRecipe {
  public RetexturedCastingRecipe(RecipeType<?> type, RecipeSerializer<? extends ItemCastingRecipe> serializer, ResourceLocation id, String group, @Nullable Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
    super(type, serializer, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
  }

  @Override
  public ItemStack assemble(ICastingContainer inv) {
    ItemStack result = getResultItem().copy();
    if (inv.getStack().getItem() instanceof BlockItem blockItem ) {
      return RetexturedBlockItem.setTexture(result, blockItem.getBlock());
    }
    return result;
  }

  public static class Serializer extends ItemCastingRecipe.Serializer {
    public Serializer(Supplier<RecipeType<ICastingRecipe>> typeSupplier) {
      super(typeSupplier);
    }

    @Override
    protected ItemCastingRecipe create(ResourceLocation id, String group, @Nullable Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
      return new RetexturedCastingRecipe(type.get(), this, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
    }
  }
}
