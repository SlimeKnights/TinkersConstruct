package slimeknights.tconstruct.library.recipe.casting.material;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Display recipe logic for material casting recipes.
 * @see AbstractMaterialCastingRecipe
 */
@Getter
public sealed abstract class DisplayMaterialCastingRecipe implements IDisplayableCastingRecipe {
  @Nullable
  private final ResourceLocation recipeId;
  private final RecipeType<?> type;
  private final List<ItemStack> castItems;
  private final List<FluidStack> fluids;
  private final List<ItemStack> outputs;
  protected final int coolingTime;
  private final boolean consumed;

  private DisplayMaterialCastingRecipe(@Nullable ResourceLocation recipeId, RecipeType<?> type, List<ItemStack> castItems, List<FluidStack> fluids, List<ItemStack> outputs, int maxCoolingTime, boolean consumed) {
    this.recipeId = recipeId;
    this.type = type;
    this.castItems = castItems;
    this.fluids = fluids;
    this.outputs = outputs;
    this.coolingTime = maxCoolingTime;
    this.consumed = consumed;
  }

  @Override
  public boolean hasCast() {
    return !castItems.isEmpty();
  }

  @Deprecated
  @Override
  public ItemStack getOutput() {
    return outputs.get(0);
  }

  @Override
  public boolean linkFluidsToOutput() {
    return true;
  }

  @Override
  public boolean isCoolingTimeDynamic() {
    return true;
  }

  /** Instance for a casting recipe, were each fluid input is unique */
  private static final class Casting extends DisplayMaterialCastingRecipe {
    private final Object2IntMap<Fluid> coolingTimes;
    private Casting(@Nullable ResourceLocation recipeId, RecipeType<?> type, List<ItemStack> castItems, List<FluidStack> fluids, List<ItemStack> outputs, Object2IntMap<Fluid> coolingTimes, int maxCoolingTime, boolean consumed) {
      super(recipeId, type, castItems, fluids, outputs, maxCoolingTime, consumed);
      this.coolingTimes = coolingTimes;
    }

    @Override
    public boolean linkCastToOutput() {
      return false;
    }

    @Override
    public int getCoolingTime(FluidStack fluid) {
      return coolingTimes.getOrDefault(fluid, coolingTime);
    }
  }

  /** Fluid key in the composite cooling time lookup. Works under the assumption that the temperature for a given fluid is consistent. */
  public record CompositeFluid(Fluid fluid, int amount) {
    public CompositeFluid(FluidStack fluid) {
      this(fluid.getFluid(), fluid.getAmount());
    }
  }

  /** Instance for a composite recipe, were we are unique over pairs of input material and fluid */
  private static final class Composite extends DisplayMaterialCastingRecipe {
    private final Object2IntMap<CompositeFluid> coolingTimes;
    private Composite(@Nullable ResourceLocation recipeId, RecipeType<?> type, List<ItemStack> castItems, List<FluidStack> fluids, List<ItemStack> outputs, Object2IntMap<CompositeFluid> coolingTimes, int maxCoolingTime, boolean consumed) {
      super(recipeId, type, castItems, fluids, outputs, maxCoolingTime, consumed);
      this.coolingTimes = coolingTimes;
    }

    @Override
    public int getCoolingTime(FluidStack fluid) {
      return coolingTimes.getOrDefault(new CompositeFluid(fluid), coolingTime);
    }
  }


  /* Builder */

  /** Creates a new builder instance */
  public static Builder type(RecipeType<?> type) {
    return new Builder(type);
  }

  /** Creates a new builder instance with the type and ID from the given recipe */
  public static Builder from(ICastingRecipe recipe) {
    return type(recipe.getType()).id(recipe.getId());
  }

  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor
  public static class Builder {
    private final RecipeType<?> type;
    @Nullable
    private ResourceLocation id;
    private List<ItemStack> casts = List.of();
    private List<FluidStack> fluids = List.of();
    private List<ItemStack> results = List.of();
    private int maxCoolingTime = 0;
    private boolean consumed = false;

    /** Sets the given ingredient as the cast */
    public Builder cast(Ingredient cast) {
      return casts(List.of(cast.getItems()));
    }

    /** Sets the given cast to the builder */
    public Builder cast(ItemStack cast) {
      return casts(List.of(cast));
    }

    /** Sets the cast to consumed */
    public Builder consumed() {
      return consumed(true);
    }


    /** Gets the max cooling time from the builder, using the map if not set */
    private int getMaxCoolingTime(Object2IntMap<?> coolingTimes) {
      if (this.maxCoolingTime == 0) {
        return coolingTimes.values().intStream().max().orElse(1);
      }
      return this.maxCoolingTime;
    }

    /** Builds the final casting recipe */
    public IDisplayableCastingRecipe casting(Object2IntMap<Fluid> coolingTimes) {
      return new DisplayMaterialCastingRecipe.Casting(id, type, casts, fluids, results, coolingTimes, getMaxCoolingTime(coolingTimes), consumed);
    }

    /** Builds the final recipe */
    public IDisplayableCastingRecipe composite(Object2IntMap<CompositeFluid> coolingTimes) {
      return new DisplayMaterialCastingRecipe.Composite(id, type, casts, fluids, results, coolingTimes, getMaxCoolingTime(coolingTimes), consumed);
    }
  }
}
