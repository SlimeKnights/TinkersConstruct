package slimeknights.tconstruct.library.recipe.casting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

@AllArgsConstructor
public abstract class MaterialCastingRecipe implements ICastingRecipe {
  @Getter
  protected final IRecipeType<?> type;
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final Ingredient cast;
  @Getter
  protected final int fluidAmount;
  @Getter // For JEI usage
  protected final IMaterialItem result;
  @Getter
  protected final boolean consumed;
  protected final boolean switchSlots;

  @Override
  public boolean switchSlots() {
    return this.switchSlots;
  }

  @Override
  public int getCoolingTime(ICastingInventory inv) {
    return ICastingRecipe.calcCoolingTime(MaterialRegistry.getMaterial(inv.getFluid()).getTemperature(), this.fluidAmount);
  }

  @Override
  public boolean matches(ICastingInventory inv, World worldIn) {
    return this.cast.test(inv.getStack()) && MaterialRegistry.getInstance() .getMaterial(inv.getFluid()) != IMaterial.UNKNOWN;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.from(Ingredient.EMPTY, this.cast);
  }

  @Override
  public ItemStack getRecipeOutput() {
    return new ItemStack(result);
  }

  @Override
  public int getFluidAmount(ICastingInventory inv) {
    return this.fluidAmount;
  }

  @Override
  public ItemStack getCraftingResult(ICastingInventory inv) {
    IMaterial material = MaterialRegistry.getInstance().getMaterial(inv.getFluid());
    return result.getItemstackWithMaterial(material);
  }

  public static class Basin extends MaterialCastingRecipe {

    public Basin(ResourceLocation id, String group, Ingredient cast, int fluidAmount, IMaterialItem result, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_BASIN, id, group, cast, fluidAmount, result, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinMaterialSerializer.get();
    }
  }

  public static class Table extends MaterialCastingRecipe {

    public Table(ResourceLocation id, String group, Ingredient cast, int fluidAmount, IMaterialItem result, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_TABLE, id, group, cast, fluidAmount, result, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableMaterialSerializer.get();
    }
  }
}
