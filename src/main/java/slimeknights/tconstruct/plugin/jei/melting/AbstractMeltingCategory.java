package slimeknights.tconstruct.plugin.jei.melting;

import lombok.RequiredArgsConstructor;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.plugin.jei.util.FluidTooltipCallback;

import java.awt.Color;
import java.util.List;

/** Shared logic between melting and foundry */
public abstract class AbstractMeltingCategory extends AbstractRecipeCategory<MeltingRecipe> {
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/melting.png");
  protected static final String KEY_COOLING_TIME = TConstruct.makeTranslationKey("jei", "melting.time");
  protected static final String KEY_TEMPERATURE = TConstruct.makeTranslationKey("jei", "temperature");
  protected static final String KEY_MULTIPLIER = TConstruct.makeTranslationKey("jei", "melting.multiplier");
  protected static final Component TOOLTIP_ORE = Component.translatable(TConstruct.makeTranslationKey("jei", "melting.ore"));

  /** Tooltip for fuel display */
  public static final FluidTooltipCallback FUEL_TOOLTIP = (fluid, slot, tooltip) -> {
    MeltingFuel fuel = MeltingFuelLookup.findFuel(fluid.getFluid());
    if (fuel != null) {
      tooltip.add(Component.translatable(KEY_TEMPERATURE, fuel.getTemperature()).withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.translatable(KEY_MULTIPLIER, fuel.getRate() / 10f).withStyle(ChatFormatting.GRAY));
    }
  };

  private final IDrawable background;
  protected final IDrawableStatic tankOverlay;
  protected final IDrawableStatic plus;

  public AbstractMeltingCategory(IGuiHelper helper, RecipeType<MeltingRecipe> recipeType, Component title, IDrawable icon) {
    super(recipeType, title, icon, 132, 40);
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 132, 40);
    this.tankOverlay = helper.createDrawable(BACKGROUND_LOC, 132, 0, 32, 32);
    this.plus = helper.drawableBuilder(BACKGROUND_LOC, 132, 32, 8, 8)
                      .addPadding(2, 2, 2, 2)
                      .build();
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, MeltingRecipe recipe, IFocusGroup focuses) {
    // includes both the static arrow background and animated foreground
    builder.addAnimatedRecipeArrowWidget(recipe.getTime() * 5)
      .setPosition(56, 18)
      .setTooltip(Component.translatable(KEY_COOLING_TIME, recipe.getTime() / 4));
    if (recipe.getOreType() != null) {
      builder.addDrawableWidget(plus).setPosition(83, 26).setTooltip(TOOLTIP_ORE);
    }
    builder.addText(Component.translatable(KEY_TEMPERATURE, recipe.getTemperature()), 113, 9)
      .setPosition(0, 3)
      .setColor(Color.GRAY.getRGB())
      .setTextAlignment(HorizontalAlignment.CENTER);
  }

  @Override
  public void draw(MeltingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
    background.draw(graphics);
  }

  /** Adds amounts to outputs and temperatures to fuels */
  @RequiredArgsConstructor
  public static class MeltingFluidCallback implements FluidTooltipCallback {
    public static final MeltingFluidCallback INSTANCE = new MeltingFluidCallback();

    /**
     * Adds teh tooltip for ores
     *
     * @param stack  Fluid to draw
     * @param list   Tooltip so far
     * @return true if the amount is not in buckets
     */
    protected boolean appendMaterial(FluidStack stack, List<Component> list) {
      return FluidTooltipHandler.appendMaterialNoShift(stack.getFluid(), stack.getAmount(), list);
    }

    @Override
    public void onFluidTooltip(FluidStack fluid, IRecipeSlotView recipeSlotView, List<Component> tooltip) {
      if (appendMaterial(fluid, tooltip)) {
        FluidTooltipHandler.appendShift(tooltip);
      }
    }
  }

  @Override
  public ResourceLocation getRegistryName(MeltingRecipe recipe) {
    return recipe.getId();
  }
}
