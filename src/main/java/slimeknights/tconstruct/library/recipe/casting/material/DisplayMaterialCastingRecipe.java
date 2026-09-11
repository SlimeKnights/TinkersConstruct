package slimeknights.tconstruct.library.recipe.casting.material;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
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

@Getter
public class DisplayMaterialCastingRecipe implements IDisplayableCastingRecipe {
  @Nullable
  private final ResourceLocation recipeId;
  private final RecipeType<?> type;
  private final List<ItemStack> castItems;
  private final List<FluidStack> fluids;
  private final List<ItemStack> outputs;
  private final int coolingTime;
  private final Object2IntMap<Fluid> coolingTimes;
  private final boolean consumed;
  private final boolean linkCastToOutput;

  private DisplayMaterialCastingRecipe(@Nullable ResourceLocation recipeId, RecipeType<?> type, List<ItemStack> castItems, List<FluidStack> fluids, List<ItemStack> outputs, Object2IntMap<Fluid> coolingTimes, int maxCoolingTime, boolean consumed, boolean linkCastToOutput) {
    this.recipeId = recipeId;
    this.type = type;
    this.castItems = castItems;
    this.fluids = fluids;
    this.outputs = outputs;
    this.coolingTimes = coolingTimes;
    this.coolingTime = maxCoolingTime;
    this.consumed = consumed;
    this.linkCastToOutput = linkCastToOutput;
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

  @Override
  public int getCoolingTime(FluidStack fluid) {
    return coolingTimes.getOrDefault(fluid, coolingTime);
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
    private Object2IntMap<Fluid> coolingTimes = Object2IntMaps.emptyMap();
    private int maxCoolingTime = 0;
    private boolean consumed = false;
    private boolean linkCastToOutput = false;

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


    /** Sets the builder to link the casts to output */
    public Builder linkCastToOutput() {
      return linkCastToOutput(true);
    }

    /** Builds the final recipe */
    public IDisplayableCastingRecipe build() {
      int maxCoolingTime = this.maxCoolingTime;
      if (maxCoolingTime == 0) {
        maxCoolingTime = coolingTimes.values().intStream().max().orElse(1);
      }
      return new DisplayMaterialCastingRecipe(id, type, casts, fluids, results, coolingTimes, maxCoolingTime, consumed, linkCastToOutput);
    }
  }
}
