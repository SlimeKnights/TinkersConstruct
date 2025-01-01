package slimeknights.tconstruct.plugin.jei;

import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.plugin.jei.util.IRecipeTooltipReplacement;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.awt.Color;
import java.util.List;

/**
 * Alloy recipe category for JEI display
 */
public class AlloyRecipeCategory implements IRecipeCategory<AlloyRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/alloy.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "alloy.title");
  private static final String KEY_TEMPERATURE = TConstruct.makeTranslationKey("jei", "temperature");

  /** Tooltip for fluid inputs */
  private static final IRecipeTooltipReplacement FLUID_TOOLTIP = (slot, list) ->
    slot.getDisplayedIngredient(ForgeTypes.FLUID_STACK).ifPresent(stack -> FluidTooltipHandler.appendMaterial(stack, list));

  /** Tooltip for fuel display */
  public static final IRecipeTooltipReplacement FUEL_TOOLTIP = (slot, tooltip) -> {
    //noinspection SimplifyOptionalCallChains  Not for int streams
    slot.getDisplayedIngredient(ForgeTypes.FLUID_STACK)
        .ifPresent(stack -> MeltingFuelHandler.getTemperature(stack.getFluid())
                                              .ifPresent(temperature -> tooltip.add(Component.translatable(KEY_TEMPERATURE, temperature).withStyle(ChatFormatting.GRAY))));
  };

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  private final IDrawable arrow;
  private final IDrawable tank;

  public AlloyRecipeCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 172, 62);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TinkerSmeltery.smelteryController));
    this.arrow = helper.drawableBuilder(BACKGROUND_LOC, 172, 0, 24, 17).buildAnimated(200, StartDirection.LEFT, false);
    this.tank = helper.createDrawable(BACKGROUND_LOC, 172, 17, 16, 16);
  }

  @Override
  public RecipeType<AlloyRecipe> getRecipeType() {
    return TConstructJEIConstants.ALLOY;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void draw(AlloyRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
    arrow.draw(graphics, 90, 21);
    // temperature info
    Font fontRenderer = Minecraft.getInstance().font;
    String tempString = I18n.get(KEY_TEMPERATURE, recipe.getTemperature());
    int x = 102 - (fontRenderer.width(tempString) / 2);
    graphics.drawString(fontRenderer, tempString, x, 5, Color.GRAY.getRGB(), false);
  }

  /**
   * Draws a variable number of fluids
   * @param builder      Builder
   * @param role         Role of the set of fluids in the recipe
   * @param x            X start
   * @param y            Y start
   * @param totalWidth   Total width
   * @param height       Tank height
   * @param fluids       List of fluids to draw
   * @param minAmount    Minimum tank size
   * @param tooltip      Tooltip callback
   * @return Max amount based on fluids
   */
  public static int drawVariableFluids(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y, int totalWidth, int height, List<List<FluidStack>> fluids, int minAmount, IRecipeSlotTooltipCallback tooltip) {
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
        builder.addSlot(role, fluidX, y)
               .addTooltipCallback(tooltip)
               .setFluidRenderer(maxAmount, false, w, height)
               .addIngredients(ForgeTypes.FLUID_STACK, fluids.get(i));
      }
      // for the last, the width is the full remaining width
      int fluidX = x + max * w;
      builder.addSlot(role, fluidX, y)
             .addTooltipCallback(tooltip)
             .setFluidRenderer(maxAmount, false, totalWidth - (w * max), height)
             .addIngredients(ForgeTypes.FLUID_STACK, fluids.get(max));
    }
    return maxAmount;
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, AlloyRecipe recipe, IFocusGroup focuses) {
    // inputs
    int maxAmount = drawVariableFluids(builder, RecipeIngredientRole.INPUT, 19, 11, 48, 32, recipe.getDisplayInputs(), recipe.getOutput().getAmount(), FLUID_TOOLTIP);

    // output
    builder.addSlot(RecipeIngredientRole.OUTPUT, 137, 11)
           .addTooltipCallback(FLUID_TOOLTIP)
           .setFluidRenderer(maxAmount, false, 16, 32)
           .addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutput());

    // fuel
    builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 94, 43)
           .addTooltipCallback(FUEL_TOOLTIP)
           .setFluidRenderer(1, false, 16, 16)
           .setOverlay(tank, 0, 0)
           .addIngredients(ForgeTypes.FLUID_STACK, MeltingFuelHandler.getUsableFuels(recipe.getTemperature()));
  }
}
