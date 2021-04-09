package slimeknights.tconstruct.library.recipe.casting.material;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Casting recipe that takes an arbitrary fluid of a given amount and set the material on the output based on that fluid
 */
public abstract class MaterialCastingRecipe extends AbstractCastingRecipe implements IMultiRecipe<IDisplayableCastingRecipe> {
  private final int itemCost;
  private final IMaterialItem result;
  private List<IDisplayableCastingRecipe> multiRecipes;

  public MaterialCastingRecipe(RecipeType<?> type, Identifier id, String group, Ingredient cast, int itemCost, IMaterialItem result, boolean consumed, boolean switchSlots) {
    super(type, id, group, cast, consumed, switchSlots);
    this.itemCost = itemCost;
    this.result = result;
  }

  @Override
  public int getCoolingTime(ICastingInventory inv) {
    IMaterial material = MaterialRegistry.getMaterial(inv.getFluid());
    return ICastingRecipe.calcCoolingTime(material.getTemperature(), material.getFluidPerUnit() * this.itemCost);
  }

  @Override
  public boolean matches(ICastingInventory inv, World worldIn) {
    return this.cast.test(inv.getStack()) && MaterialRegistry.getInstance().getMaterial(inv.getFluid()) != IMaterial.UNKNOWN;
  }

  @Override
  public DefaultedList<Ingredient> getPreviewInputs() {
    return DefaultedList.copyOf(Ingredient.EMPTY, this.cast);
  }

  @Override
  public ItemStack getOutput() {
    return new ItemStack(result);
  }

  @Override
  public int getFluidAmount(ICastingInventory inv) {
    return MaterialRegistry.getMaterial(inv.getFluid()).getFluidPerUnit() * this.itemCost;
  }

  @Override
  public ItemStack getCraftingResult(ICastingInventory inv) {
    IMaterial material = MaterialRegistry.getInstance().getMaterial(inv.getFluid());
    return result.getItemstackWithMaterial(material);
  }

  @Override
  @SuppressWarnings("WeakerAccess")
  public List<IDisplayableCastingRecipe> getRecipes() {
    if (multiRecipes == null) {
      RecipeType<?> type = getType();
      List<ItemStack> castItems = Arrays.asList(cast.getMatchingStacksClient());
      multiRecipes = MaterialRegistry
        .getMaterials().stream()
        .filter(mat -> mat.getFluid() != Fluids.EMPTY)
        .map(mat -> new DisplayCastingRecipe(type, castItems, Collections.singletonList(new FluidVolume(mat.getFluid(), itemCost * mat.getFluidPerUnit())),
                                             result.getItemstackWithMaterial(mat), ICastingRecipe.calcCoolingTime(mat.getTemperature(), itemCost * mat.getFluidPerUnit()), consumed))
        .collect(Collectors.toList());
    }
    return multiRecipes;
  }

  /** Basin implementation */
  public static class Basin extends MaterialCastingRecipe {

    public Basin(Identifier id, String group, Ingredient cast, int fluidAmount, IMaterialItem result, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_BASIN, id, group, cast, fluidAmount, result, consumed, switchSlots);
      MaterialItemCostLookup.registerBasin(result, fluidAmount);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinMaterialSerializer.get();
    }
  }

  /** Table implementation */
  public static class Table extends MaterialCastingRecipe {

    public Table(Identifier id, String group, Ingredient cast, int fluidAmount, IMaterialItem result, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_TABLE, id, group, cast, fluidAmount, result, consumed, switchSlots);
      MaterialItemCostLookup.registerTable(result, fluidAmount);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableMaterialSerializer.get();
    }
  }

  /**
   * Interface representing a material casting recipe constructor
   * @param <T>  Recipe class type
   */
  public interface IFactory<T extends MaterialCastingRecipe> {
    T create(Identifier id, String group, @Nullable Ingredient cast, int fluidAmount, IMaterialItem result,
             boolean consumed, boolean switchSlots);
  }

  @RequiredArgsConstructor
  public static class Serializer<T extends MaterialCastingRecipe> extends AbstractCastingRecipe.Serializer<T> {
    private final IFactory<T> factory;

    @Override
    protected T create(Identifier idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, JsonObject json) {
      int fluidAmount = JsonHelper.getInt(json, "item_cost");
      IMaterialItem result = RecipeHelper.deserializeItem(JsonHelper.getString(json, "result"), "result", IMaterialItem.class);
      return this.factory.create(idIn, groupIn, cast, fluidAmount, result, consumed, switchSlots);
    }

    @Override
    protected T create(Identifier idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, PacketByteBuf buffer) {
      int fluidAmount = buffer.readInt();
      IMaterialItem result = RecipeHelper.readItem(buffer, IMaterialItem.class);
      return this.factory.create(idIn, groupIn, cast, fluidAmount, result, consumed, switchSlots);
    }

    @Override
    protected void writeExtra(PacketByteBuf buffer, MaterialCastingRecipe recipe) {
      buffer.writeInt(recipe.itemCost);
      RecipeHelper.writeItem(buffer, recipe.result);
    }
  }
}
