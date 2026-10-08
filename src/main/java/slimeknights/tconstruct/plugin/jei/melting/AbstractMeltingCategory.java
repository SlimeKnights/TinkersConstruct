package slimeknights.tconstruct.plugin.jei.melting;

import lombok.RequiredArgsConstructor;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IDrawableWidget;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.gui.widgets.IRecipeWidgetTooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelLookup;
import slimeknights.tconstruct.library.recipe.melting.IDisplayableMeltingRecipe;
import slimeknights.tconstruct.plugin.jei.util.CategoryUtil;
import slimeknights.tconstruct.plugin.jei.util.FluidTooltipCallback;

import java.awt.Color;
import java.util.List;

/** Shared logic between melting and foundry */
public abstract class AbstractMeltingCategory extends AbstractRecipeCategory<IDisplayableMeltingRecipe> {
  public static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/melting.png");
  protected static final String KEY_COOLING_TIME = TConstruct.makeTranslationKey("jei", "melting.time");
  protected static final String KEY_TEMPERATURE = TConstruct.makeTranslationKey("jei", "temperature");
  protected static final String KEY_MULTIPLIER = TConstruct.makeTranslationKey("jei", "melting.multiplier");
  protected static final Component TOOLTIP_ORE = Component.translatable(TConstruct.makeTranslationKey("jei", "melting.ore"));
  /** Name of the fluid slot to fetch for the time tooltips. */
  protected static final String FLUID_SLOT = "fluid";

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

  public AbstractMeltingCategory(IGuiHelper helper, RecipeType<IDisplayableMeltingRecipe> recipeType, Component title, IDrawable icon) {
    super(recipeType, title, icon, 132, 40);
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 132, 40);
    this.tankOverlay = helper.createDrawable(BACKGROUND_LOC, 132, 0, 32, 32);
    this.plus = helper.drawableBuilder(BACKGROUND_LOC, 132, 32, 8, 8)
                      .addPadding(2, 2, 2, 2)
                      .build();
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IDisplayableMeltingRecipe recipe, IFocusGroup focuses) {
    IRecipeSlotDrawable fluid = null;
    if (recipe.isTimeDynamic() || recipe.isTemperatureDynamic()) {
      fluid = CategoryUtil.findSlot(builder.getRecipeSlots().getSlots(), FLUID_SLOT);
    }

    // includes both the static arrow background and animated foreground
    int time = recipe.getTime();
    IDrawableWidget arrow = builder.addAnimatedRecipeArrowWidget(time * 5).setPosition(56, 18);
    // add time as a tooltip on the arrow
    if (recipe.isTimeDynamic() && fluid != null) {
      arrow.setTooltip(new MeltingArrowTooltip(recipe, fluid));
    } else {
      // not dynamic or fail to find the slot? static tooltip is fine
      arrow.setTooltip(Component.translatable(KEY_COOLING_TIME, time / 4));
    }

    if (recipe.getOreType() != null) {
      builder.addDrawableWidget(plus).setPosition(83, 26).setTooltip(TOOLTIP_ORE);
    }
    // draw temperature above the recipe, animated if requested
    if (recipe.isTemperatureDynamic() && fluid != null) {
      builder.addWidget(new TemperatureWidget(new ScreenPosition(0, 3), 113, recipe, fluid, Minecraft.getInstance().font));
    } else {
      builder.addText(Component.translatable(KEY_TEMPERATURE, recipe.getTemperature()), 113, 9)
        .setPosition(0, 3)
        .setColor(Color.GRAY.getRGB())
        .setTextAlignment(HorizontalAlignment.CENTER);
    }
  }

  @Override
  public void draw(IDisplayableMeltingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
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
  public ResourceLocation getRegistryName(IDisplayableMeltingRecipe recipe) {
    return recipe.getRecipeId();
  }

  /** Handles the tooltip for the arrow */
  private record MeltingArrowTooltip(IDisplayableMeltingRecipe recipe, IRecipeSlotDrawable fluidSlot) implements IRecipeWidgetTooltipCallback {
    @Override
    public void onTooltip(ITooltipBuilder tooltip) {
      FluidStack fluid = fluidSlot.getDisplayedIngredient(ForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY);
      tooltip.add(Component.translatable(KEY_COOLING_TIME, recipe.getTime(fluid) / 4));
    }
  }

  /** Widget to display dynamic temperature with respect to the recipe */
  private record TemperatureWidget(ScreenPosition getPosition, int width, IDisplayableMeltingRecipe recipe, IRecipeSlotDrawable fluidSlot, Font font) implements IRecipeWidget {
    @Override
    public void drawWidget(GuiGraphics graphics, double mouseX, double mouseY) {
      FluidStack fluid = fluidSlot.getDisplayedIngredient(ForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY);
      Component temperature = Component.translatable(KEY_TEMPERATURE, recipe.getTemperature(fluid));
      graphics.drawString(font, temperature, (width - font.width(temperature)) / 2, 0, Color.GRAY.getRGB(), false);
    }
  }
}
