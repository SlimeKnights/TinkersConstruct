package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Casting recipe that takes an arbitrary fluid of a given amount and set the material on the output based on that fluid
 */
public abstract class MaterialCastingRecipe extends AbstractCastingRecipe implements IMultiRecipe<ItemCastingRecipe> {
  private final int fluidAmount;
  private final IMaterialItem result;
  private List<ItemCastingRecipe> multiRecipes;

  public MaterialCastingRecipe(IRecipeType<?> type, ResourceLocation id, String group, Ingredient cast, int fluidAmount, IMaterialItem result, boolean consumed, boolean switchSlots) {
    super(type, id, group, cast, consumed, switchSlots);
    this.fluidAmount = fluidAmount;
    this.result = result;
  }

  @Override
  public int getCoolingTime(ICastingInventory inv) {
    return ICastingRecipe.calcCoolingTime(MaterialRegistry.getMaterial(inv.getFluid()).getTemperature(), this.fluidAmount);
  }

  @Override
  public boolean matches(ICastingInventory inv, World worldIn) {
    return this.cast.test(inv.getStack()) && MaterialRegistry.getInstance().getMaterial(inv.getFluid()) != IMaterial.UNKNOWN;
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

  /**
   * Base logic to get and cache a list of recipes for the given {@link ItemCastingRecipe} factory
   * @param factory  Factory instance
   * @return  Display recipe list
   */
  @SuppressWarnings("WeakerAccess")
  protected List<ItemCastingRecipe> getRecipes(ItemCastingRecipe.IFactory<? extends ItemCastingRecipe> factory) {
    if (multiRecipes == null) {
      multiRecipes = MaterialRegistry.getMaterials().stream()
                                     .filter(mat -> mat.getFluid() != Fluids.EMPTY)
                                     .map(mat -> {
          ResourceLocation matId = mat.getIdentifier();
          return factory.create(
            new ResourceLocation(id.getNamespace(), String.format("%s/%s/%s", id.getPath(), matId.getNamespace(), matId.getPath())),
            group, cast, FluidIngredient.of(mat.getFluid(), fluidAmount), ItemOutput.fromStack(result.getItemstackWithMaterial(mat)),
            ICastingRecipe.calcCoolingTime(mat.getTemperature(), fluidAmount), consumed, switchSlots);
        }).collect(Collectors.toList());
    }
    return multiRecipes;
  }

  /** Basin implementation */
  public static class Basin extends MaterialCastingRecipe {

    public Basin(ResourceLocation id, String group, Ingredient cast, int fluidAmount, IMaterialItem result, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_BASIN, id, group, cast, fluidAmount, result, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinMaterialSerializer.get();
    }

    @Override
    public List<ItemCastingRecipe> getRecipes() {
      return getRecipes(ItemCastingRecipe.Basin::new);
    }
  }

  /** Table implementation */
  public static class Table extends MaterialCastingRecipe {

    public Table(ResourceLocation id, String group, Ingredient cast, int fluidAmount, IMaterialItem result, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_TABLE, id, group, cast, fluidAmount, result, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableMaterialSerializer.get();
    }

    @Override
    public List<ItemCastingRecipe> getRecipes() {
      return getRecipes(ItemCastingRecipe.Table::new);
    }
  }

  /**
   * Interface representing a material casting recipe constructor
   * @param <T>  Recipe class type
   */
  public interface IFactory<T extends MaterialCastingRecipe> {
    T create(ResourceLocation id, String group, Ingredient cast, int fluidAmount, IMaterialItem result,
             boolean consumed, boolean switchSlots);
  }

  @RequiredArgsConstructor
  public static class Serializer<T extends MaterialCastingRecipe> extends AbstractCastingRecipe.Serializer<T> {
    private final IFactory<T> factory;

    @Override
    protected T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, JsonObject json) {
      int fluidAmount = JSONUtils.getInt(json, "fluid_amount");
      IMaterialItem result = RecipeHelper.deserializeItem(JSONUtils.getString(json, "result"), "result", IMaterialItem.class);
      return this.factory.create(idIn, groupIn, cast, fluidAmount, result, consumed, switchSlots);
    }

    @Override
    protected T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, PacketBuffer buffer) {
      int fluidAmount = buffer.readInt();
      IMaterialItem result = RecipeHelper.readItem(buffer, IMaterialItem.class);
      return this.factory.create(idIn, groupIn, cast, fluidAmount, result, consumed, switchSlots);
    }

    @Override
    protected void writeExtra(PacketBuffer buffer, MaterialCastingRecipe recipe) {
      buffer.writeInt(recipe.fluidAmount);
      RecipeHelper.writeItem(buffer, recipe.result);
    }
  }
}
