package slimeknights.tconstruct.smeltery.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CastingBasinRecipe extends AbstractCastingRecipe {

  public CastingBasinRecipe(ResourceLocation idIn, String groupIn, @Nullable Ingredient ingredient, @Nonnull FluidStack fluidIn, ItemStack result, int coolingTime, boolean consumed, boolean switchSlots) {
    super(TinkerSmeltery.basinRecipeType, idIn, groupIn, ingredient, fluidIn, result, coolingTime, consumed, switchSlots);
  }

  public ItemStack getIcon() {
    return new ItemStack(TinkerSmeltery.castingBasin);
  }

  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.basinRecipeSerializer.get();
  }
}
