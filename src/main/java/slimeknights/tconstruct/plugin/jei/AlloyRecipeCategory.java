package slimeknights.tconstruct.plugin.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.fluid.FluidTooltipHandler;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.awt.Color;
import java.util.List;

/**
 * Alloy recipe category for JEI display
 */
public class AlloyRecipeCategory implements IRecipeCategory<AlloyRecipe>, ITooltipCallback<FluidStack> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/alloy.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "alloy.title");
  private static final String KEY_TEMPERATURE = TConstruct.makeTranslationKey("jei", "temperature");

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  private final IDrawable arrow;
  private final IDrawable tank;

  public AlloyRecipeCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 172, 62);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(TinkerSmeltery.smelteryController));
    this.arrow = helper.drawableBuilder(BACKGROUND_LOC, 172, 0, 24, 17).buildAnimated(200, StartDirection.LEFT, false);
    this.tank = helper.createDrawable(BACKGROUND_LOC, 172, 17, 16, 16);
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.alloy;
  }

  @Override
  public Class<? extends AlloyRecipe> getRecipeClass() {
    return AlloyRecipe.class;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void setIngredients(AlloyRecipe recipe, IIngredients ingredients) {
    ingredients.setInputLists(VanillaTypes.FLUID, recipe.getDisplayInputs());
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutput());
  }

  @Override
  public void draw(AlloyRecipe recipe, PoseStack matrices, double mouseX, double mouseY) {
    arrow.draw(matrices, 90, 21);
    // temperature info
    Font fontRenderer = Minecraft.getInstance().font;
    String tempString = I18n.get(KEY_TEMPERATURE, recipe.getTemperature());
    int x = 102 - (fontRenderer.width(tempString) / 2);
    fontRenderer.draw(matrices, tempString, x, 5, Color.GRAY.getRGB());
  }

  /**
   * Draws a variable number of fluids
   * @param fluidGroup   JEI fluid group
   * @param x            X start
   * @param y            Y start
   * @param totalWidth   Total width
   * @param height       Tank height
   * @param fluids       List of fluids to draw
   * @param indexOffset  Amount to offset the index by
   * @param minAmount    Minimum tank size
   * @return Max amount based on fluids
   */
  public static int drawVariableFluids(IGuiFluidStackGroup fluidGroup, int indexOffset, boolean isInput, int x, int y, int totalWidth, int height, List<List<FluidStack>> fluids, int minAmount) {
    int count = fluids.size();
    int maxAmount = minAmount;
    if (count > 0) {
      // first, find maximum used amount in the recipe so relations are correct
      for(List<FluidStack> list : fluids) {
        for(FluidStack input : list) {
          if (input.getAmount() > maxAmount) {
            maxAmount = input.getAmount();
          }
        }
      }
      // next, draw all fluids but the last
      int w = totalWidth / count;
      int max = count - 1;
      for (int i = 0; i < max; i++) {
        int fluidX = x + i * w;
        fluidGroup.init(i + indexOffset, isInput, fluidX, y, w, height, maxAmount, false, null);
      }
      // for the last, the width is the full remaining width
      int fluidX = x + max * w;
      fluidGroup.init(max + indexOffset, isInput, fluidX, y, totalWidth - (w * max), height, maxAmount, false, null);
    }
    return maxAmount;
  }

  @Override
  public void setRecipe(IRecipeLayout layout, AlloyRecipe recipe, IIngredients ingredients) {
    // inputs
    IGuiFluidStackGroup fluids = layout.getFluidStacks();
    fluids.addTooltipCallback(this);
    int maxAmount = drawVariableFluids(fluids, 2, true, 19, 11, 48, 32, recipe.getDisplayInputs(), recipe.getOutput().getAmount());

    // output
    fluids.init(0, false, 137, 11, 16, 32, maxAmount, false, null);
    fluids.set(ingredients);

    // show fuels that are valid for this recipe
    fluids.init(1, true, 94, 43, 16, 16, 1, false, tank);
    fluids.set(1, MeltingFuelHandler.getUsableFuels(recipe.getTemperature()));
  }

  @Override
  public void onTooltip(int index, boolean input, FluidStack stack, List<Component> list) {
    Fluid fluid = stack.getFluid();
    if (fluid != null) {
      Component name = list.get(0);
      Component modId = list.get(list.size() - 1);
      list.clear();
      list.add(name);

      // add amount to inputs
      if (index != 1) {
        FluidTooltipHandler.appendMaterial(stack, list);
      } else {
        // add temperature to fuels
        MeltingFuelHandler.getTemperature(stack.getFluid())
                          .ifPresent(temperature -> list.add(new TranslatableComponent(KEY_TEMPERATURE, temperature).withStyle(ChatFormatting.GRAY)));
      }
      list.add(modId);
    }
  }
}
