package slimeknights.tconstruct.plugin.jei.melting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;

import java.util.List;

/** Shared by melter and smeltery */
public class MeltingCategory extends AbstractMeltingCategory {
  private static final Component TITLE = TConstruct.makeTranslation("jei", "melting.title");
  private static final String KEY_TEMPERATURE = TConstruct.makeTranslationKey("jei", "temperature");
  private static final String KEY_MULTIPLIER = TConstruct.makeTranslationKey("jei", "melting.multiplier");
  private static final Component SOLID_TEMPERATURE = Component.translatable(KEY_TEMPERATURE, FuelModule.SOLID_TEMPERATURE).withStyle(ChatFormatting.GRAY);
  private static final Component SOLID_MULTIPLIER = Component.translatable(KEY_MULTIPLIER, FuelModule.SOLID_TEMPERATURE / 1000f).withStyle(ChatFormatting.GRAY);
  private static final Component TOOLTIP_SMELTERY = TConstruct.makeTranslation("jei", "melting.smeltery").withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);
  private static final Component TOOLTIP_MELTER = TConstruct.makeTranslation("jei", "melting.melter").withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);

  /** Tooltip callback for items */
  private static final IRecipeSlotTooltipCallback ITEM_FUEL_TOOLTIP = (slot, list) -> {
    list.add(1, SOLID_TEMPERATURE);
    list.add(2, SOLID_MULTIPLIER);
  };

  /** Tooltip callback for ores */
  private static final IRecipeSlotTooltipCallback METAL_ORE_TOOLTIP = new MeltingFluidCallback(OreRateType.METAL);
  private static final IRecipeSlotTooltipCallback GEM_ORE_TOOLTIP = new MeltingFluidCallback(OreRateType.GEM);

  @Getter
  private final IDrawable icon;
  private final IDrawableStatic solidFuel;

  public MeltingCategory(IGuiHelper helper) {
    super(helper);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TinkerSmeltery.searedMelter));
    this.solidFuel = helper.drawableBuilder(BACKGROUND_LOC, 164, 0, 18, 20).build();
  }

  @Override
  public RecipeType<MeltingRecipe> getRecipeType() {
    return TConstructJEIConstants.MELTING;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void draw(MeltingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
    super.draw(recipe, slots, graphics, mouseX, mouseY);

    // solid fuel slot
    int temperature = recipe.getTemperature();
    if (temperature <= FuelModule.SOLID_TEMPERATURE) {
      solidFuel.draw(graphics, 1, 19);
    }
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, MeltingRecipe recipe, IFocusGroup focuses) {
    // input
    builder.addSlot(RecipeIngredientRole.INPUT, 24, 18).addIngredients(recipe.getInput());

    // output
    OreRateType oreType = recipe.getOreType();
    IRecipeSlotTooltipCallback tooltip;
    if (oreType == OreRateType.METAL) {
      tooltip = METAL_ORE_TOOLTIP;
    } else if (oreType == OreRateType.GEM) {
      tooltip = GEM_ORE_TOOLTIP;
    } else {
      tooltip = MeltingFluidCallback.INSTANCE;
    }
    builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 4)
      .addTooltipCallback(tooltip)
      .setFluidRenderer(FluidValues.METAL_BLOCK, false, 32, 32)
      .setOverlay(tankOverlay, 0, 0)
      .addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutput());

    // show fuels that are valid for this recipe
    int fuelHeight = 32;
    // solid fuel
    if (recipe.getTemperature() <= FuelModule.SOLID_TEMPERATURE) {
      fuelHeight = 15;
      builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 2, 22)
             .addTooltipCallback(ITEM_FUEL_TOOLTIP)
             .addItemStacks(MeltingFuelHandler.SOLID_FUELS.get());
    }

    // liquid fuel
    builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 4, 4)
           .addTooltipCallback(FUEL_TOOLTIP)
           .setFluidRenderer(1, false, 12, fuelHeight)
           .addIngredients(ForgeTypes.FLUID_STACK, MeltingFuelHandler.getUsableFuels(recipe.getTemperature()));
  }

  /** Adds amounts to outputs and temperatures to fuels */
  @RequiredArgsConstructor
  private static class MeltingFluidCallback extends AbstractMeltingCategory.MeltingFluidCallback {
    @Getter
    private final OreRateType oreType;

    @Override
    protected boolean appendMaterial(FluidStack stack, List<Component> list) {
      Fluid fluid = stack.getFluid();
      int amount = stack.getAmount();
      int smelteryAmount = Config.COMMON.smelteryOreRate.applyOreBoost(oreType, amount);
      int melterAmount = Config.COMMON.melterOreRate.applyOreBoost(oreType, amount);
      if (smelteryAmount != melterAmount) {
        list.add(TOOLTIP_MELTER);
        boolean shift = FluidTooltipHandler.appendMaterialNoShift(fluid, melterAmount, list);
        list.add(Component.empty());
        list.add(TOOLTIP_SMELTERY);
        shift = FluidTooltipHandler.appendMaterialNoShift(fluid, smelteryAmount, list) || shift;
        return shift;
      } else {
        return FluidTooltipHandler.appendMaterialNoShift(fluid, smelteryAmount, list);
      }
    }
  }
}
