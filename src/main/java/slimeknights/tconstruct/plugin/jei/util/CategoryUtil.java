package slimeknights.tconstruct.plugin.jei.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.function.Function;

/** Helpers for setting up JEI recipe categories. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryUtil {
  /**
   * Draws a variable number of fluids.
   * @param builder      Builder
   * @param role         Role of the fluids in the recipe
   * @param x            X start
   * @param y            Y start
   * @param totalWidth   Total width
   * @param height       Tank height
   * @param fluids       List of fluids to draw
   * @param minAmount    Minimum tank size
   * @param mapper       Logic to get a fluid list from the object
   * @param tooltip      Tooltip callback
   * @param <T> Object type
   * @return Max amount based on fluids
   */
  public static <T> int drawMultipleFluids(IRecipeLayoutBuilder builder, Function<T,RecipeIngredientRole> role, int x, int y, int totalWidth, int height, List<T> fluids, int minAmount, Function<T,List<FluidStack>> mapper, Function<T,IRecipeSlotRichTooltipCallback> tooltip) {
    int count = fluids.size();
    int maxAmount = minAmount;
    if (count > 0) {
      // first, find maximum used amount in the recipe so relations are correct
      for (T ingredient : fluids) {
        for (FluidStack input : mapper.apply(ingredient)) {
          if (input.getAmount() > maxAmount) {
            maxAmount = input.getAmount();
          }
        }
      }
      // next, draw all fluids but the last
      int width = totalWidth / count;
      int last = count - 1;
      for (int i = 0; i < last; i++) {
        int fluidX = x + i * width;
        T ingredient = fluids.get(i);
        builder.addSlot(role.apply(ingredient), fluidX, y)
               .addRichTooltipCallback(tooltip.apply(ingredient))
               .setFluidRenderer(maxAmount, false, width, height)
               .addIngredients(ForgeTypes.FLUID_STACK, mapper.apply(ingredient));
      }
      // for the last, the width is the full remaining width
      int fluidX = x + last * width;
      T ingredient = fluids.get(last);
      builder.addSlot(role.apply(ingredient), fluidX, y)
             .addRichTooltipCallback(tooltip.apply(ingredient))
             .setFluidRenderer(maxAmount, false, totalWidth - (width * last), height)
             .addIngredients(ForgeTypes.FLUID_STACK, mapper.apply(ingredient));
    }
    return maxAmount;
  }
}
