package slimeknights.tconstruct.library.recipe.casting.material;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Casting recipe that takes an arbitrary fluid of a given amount and set the material on the output based on that fluid
 */
public abstract class MaterialCastingRecipe extends AbstractCastingRecipe implements IMultiRecipe<IDisplayableCastingRecipe> {
  protected final int itemCost;
  protected final IMaterialItem result;

  public MaterialCastingRecipe(IRecipeType<?> type, ResourceLocation id, String group, Ingredient cast, int itemCost, IMaterialItem result, boolean consumed, boolean switchSlots) {
    super(type, id, group, cast, consumed, switchSlots);
    this.itemCost = itemCost;
    this.result = result;
    MaterialCastingLookup.registerItemCost(result, itemCost);
  }

  /** Gets the material fluid recipe for the given recipe */
  protected Optional<MaterialFluidRecipe> getMaterialFluid(ICastingInventory inv) {
    return MaterialCastingLookup.getCastingFluid(inv.getFluid());
  }

  @Override
  public boolean matches(ICastingInventory inv, World worldIn) {
    if (!this.cast.test(inv.getStack())) {
      return false;
    }
    return getMaterialFluid(inv).filter(recipe -> result.canUseMaterial(recipe.getOutput())).isPresent();
  }

  @Override
  public int getCoolingTime(ICastingInventory inv) {
    return getMaterialFluid(inv)
      .map(recipe -> ICastingRecipe.calcCoolingTime(recipe.getTemperature(), recipe.getFluidAmount(inv.getFluid()) * itemCost))
      .orElse(1);
  }

  @Override
  public int getFluidAmount(ICastingInventory inv) {
    return getMaterialFluid(inv)
             .map(recipe -> recipe.getFluidAmount(inv.getFluid()))
             .orElse(1) * this.itemCost;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return new ItemStack(result);
  }

  @Override
  public ItemStack getCraftingResult(ICastingInventory inv) {
    IMaterial material = getMaterialFluid(inv).map(MaterialFluidRecipe::getOutput).orElse(IMaterial.UNKNOWN);
    return result.withMaterial(material);
  }

  /* JEI display */
  protected List<IDisplayableCastingRecipe> multiRecipes;

  /** Resizes the list of the fluids with respect to the item cost */
  protected List<FluidStack> resizeFluids(List<FluidStack> fluids) {
    if (itemCost != 1) {
      return fluids.stream()
                   .map(fluid -> new FluidStack(fluid, fluid.getAmount() * itemCost))
                   .collect(Collectors.toList());
    }
    return fluids;
  }

  @Override
  public List<IDisplayableCastingRecipe> getRecipes() {
    if (multiRecipes == null) {
      IRecipeType<?> type = getType();
      List<ItemStack> castItems = Arrays.asList(cast.getMatchingStacks());
      multiRecipes = MaterialCastingLookup
        .getAllCastingFluids().stream()
        .filter(recipe -> !recipe.getOutput().isHidden() && result.canUseMaterial(recipe.getOutput()))
        .map(recipe -> {
          List<FluidStack> fluids = resizeFluids(recipe.getFluids());
          int fluidAmount = fluids.stream().mapToInt(FluidStack::getAmount).max().orElse(0);
          return new DisplayCastingRecipe(type, castItems, fluids, result.withMaterial(recipe.getOutput()),
                                          ICastingRecipe.calcCoolingTime(recipe.getTemperature(), itemCost * fluidAmount), consumed);
        })
        .collect(Collectors.toList());
    }
    return multiRecipes;
  }

  /** Basin implementation */
  public static class Basin extends MaterialCastingRecipe {
    public Basin(ResourceLocation id, String group, Ingredient cast, int itemCost, IMaterialItem result, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_BASIN, id, group, cast, itemCost, result, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinMaterialSerializer.get();
    }
  }

  /** Table implementation */
  public static class Table extends MaterialCastingRecipe {
    public Table(ResourceLocation id, String group, Ingredient cast, int itemCost, IMaterialItem result, boolean consumed, boolean switchSlots) {
      super(RecipeTypes.CASTING_TABLE, id, group, cast, itemCost, result, consumed, switchSlots);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableMaterialSerializer.get();
    }
  }

  /**
   * Interface representing a material casting recipe constructor
   * @param <T>  Recipe class type
   */
  public interface IFactory<T extends MaterialCastingRecipe> {
    T create(ResourceLocation id, String group, @Nullable Ingredient cast, int itemCost, IMaterialItem result,
             boolean consumed, boolean switchSlots);
  }

  @RequiredArgsConstructor
  public static class Serializer<T extends MaterialCastingRecipe> extends AbstractCastingRecipe.Serializer<T> {
    private final IFactory<T> factory;

    @Override
    protected T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, JsonObject json) {
      int itemCost = JSONUtils.getInt(json, "item_cost");
      IMaterialItem result = RecipeHelper.deserializeItem(JSONUtils.getString(json, "result"), "result", IMaterialItem.class);
      return this.factory.create(idIn, groupIn, cast, itemCost, result, consumed, switchSlots);
    }

    @Override
    protected T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, PacketBuffer buffer) {
      int fluidAmount = buffer.readInt();
      IMaterialItem result = RecipeHelper.readItem(buffer, IMaterialItem.class);
      return this.factory.create(idIn, groupIn, cast, fluidAmount, result, consumed, switchSlots);
    }

    @Override
    protected void writeExtra(PacketBuffer buffer, MaterialCastingRecipe recipe) {
      buffer.writeInt(recipe.itemCost);
      RecipeHelper.writeItem(buffer, recipe.result);
    }
  }
}
