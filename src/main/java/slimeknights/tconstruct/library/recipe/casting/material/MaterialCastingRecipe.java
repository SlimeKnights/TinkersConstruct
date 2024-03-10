package slimeknights.tconstruct.library.recipe.casting.material;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Casting recipe that takes an arbitrary fluid of a given amount and set the material on the output based on that fluid
 */
public class MaterialCastingRecipe extends AbstractCastingRecipe implements IMultiRecipe<IDisplayableCastingRecipe> {
  @Getter
  private final RecipeSerializer<?> serializer;
  protected final int itemCost;
  protected final IMaterialItem result;
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  protected Optional<MaterialFluidRecipe> cachedFluidRecipe = Optional.empty();

  public MaterialCastingRecipe(RecipeType<?> type, RecipeSerializer<?> serializer, ResourceLocation id, String group, @Nullable Ingredient cast, int itemCost, IMaterialItem result, boolean consumed, boolean switchSlots) {
    super(type, id, group, cast, consumed, switchSlots);
    this.serializer = serializer;
    this.itemCost = itemCost;
    this.result = result;
    MaterialCastingLookup.registerItemCost(result, itemCost);
  }

  /** Gets the material fluid recipe for the given recipe */
  protected Optional<MaterialFluidRecipe> getMaterialFluid(ICastingContainer inv) {
    return MaterialCastingLookup.getCastingFluid(inv);
  }

  /** Gets the cached fluid recipe if it still matches, refetches if not */
  protected Optional<MaterialFluidRecipe> getCachedMaterialFluid(ICastingContainer inv) {
    Optional<MaterialFluidRecipe> fluidRecipe = cachedFluidRecipe;
    if (fluidRecipe.filter(recipe -> recipe.matches(inv)).isEmpty()) {
      fluidRecipe = getMaterialFluid(inv);
      if (fluidRecipe.isPresent()) {
        cachedFluidRecipe = fluidRecipe;
      }
    }
    return fluidRecipe;
  }

  @Override
  public boolean matches(ICastingContainer inv, Level worldIn) {
    if (!this.cast.test(inv.getStack())) {
      return false;
    }
    return getCachedMaterialFluid(inv).filter(recipe -> result.canUseMaterial(recipe.getOutput().getId())).isPresent();
  }

  @Override
  public int getCoolingTime(ICastingContainer inv) {
    return getCachedMaterialFluid(inv)
      .map(recipe -> ICastingRecipe.calcCoolingTime(recipe.getTemperature(), recipe.getFluidAmount(inv.getFluid()) * itemCost))
      .orElse(1);
  }

  @Override
  public int getFluidAmount(ICastingContainer inv) {
    return getCachedMaterialFluid(inv)
             .map(recipe -> recipe.getFluidAmount(inv.getFluid()))
             .orElse(1) * this.itemCost;
  }

  @Override
  public ItemStack getResultItem() {
    return new ItemStack(result);
  }

  @Override
  public ItemStack assemble(ICastingContainer inv) {
    MaterialVariant material = getCachedMaterialFluid(inv).map(MaterialFluidRecipe::getOutput).orElse(MaterialVariant.UNKNOWN);
    return result.withMaterial(material.getVariant());
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
      RecipeType<?> type = getType();
      List<ItemStack> castItems = Arrays.asList(cast.getItems());
      multiRecipes = MaterialCastingLookup
        .getAllCastingFluids().stream()
        .filter(recipe -> {
          MaterialVariant output = recipe.getOutput();
          return !output.isUnknown() && !output.get().isHidden() && result.canUseMaterial(output.getId());
        })
        .map(recipe -> {
          List<FluidStack> fluids = resizeFluids(recipe.getFluids());
          int fluidAmount = fluids.stream().mapToInt(FluidStack::getAmount).max().orElse(0);
          return new DisplayCastingRecipe(type, castItems, fluids, result.withMaterial(recipe.getOutput().getVariant()),
                                          ICastingRecipe.calcCoolingTime(recipe.getTemperature(), itemCost * fluidAmount), consumed);
        })
        .collect(Collectors.toList());
    }
    return multiRecipes;
  }

  @RequiredArgsConstructor
  public static class Serializer extends AbstractCastingRecipe.Serializer<MaterialCastingRecipe> {
    private final Supplier<RecipeType<ICastingRecipe>> type;


    @Override
    protected MaterialCastingRecipe create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, JsonObject json) {
      int itemCost = GsonHelper.getAsInt(json, "item_cost");
      IMaterialItem result = RecipeHelper.deserializeItem(GsonHelper.getAsString(json, "result"), "result", IMaterialItem.class);
      return new MaterialCastingRecipe(type.get(), this, idIn, groupIn, cast, itemCost, result, consumed, switchSlots);
    }

    @Override
    protected MaterialCastingRecipe create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, boolean consumed, boolean switchSlots, FriendlyByteBuf buffer) {
      int fluidAmount = buffer.readInt();
      IMaterialItem result = RecipeHelper.readItem(buffer, IMaterialItem.class);
      return new MaterialCastingRecipe(type.get(), this, idIn, groupIn, cast, fluidAmount, result, consumed, switchSlots);
    }

    @Override
    protected void writeExtra(FriendlyByteBuf buffer, MaterialCastingRecipe recipe) {
      buffer.writeInt(recipe.itemCost);
      RecipeHelper.writeItem(buffer, recipe.result);
    }
  }
}
