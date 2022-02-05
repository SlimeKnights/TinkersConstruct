package slimeknights.tconstruct.plugin.jei.melting;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.fluid.FluidTooltipHandler;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;

import java.util.List;

/** Shared by melter and smeltery */
public class MeltingCategory extends AbstractMeltingCategory {
  private static final Component TITLE = TConstruct.makeTranslation("jei", "melting.title");
  private static final String KEY_TEMPERATURE = TConstruct.makeTranslationKey("jei", "temperature");
  private static final String KEY_MULTIPLIER = TConstruct.makeTranslationKey("jei", "melting.multiplier");
  private static final Component SOLID_TEMPERATURE = new TranslatableComponent(KEY_TEMPERATURE, FuelModule.SOLID_TEMPERATURE).withStyle(ChatFormatting.GRAY);
  private static final Component SOLID_MULTIPLIER = new TranslatableComponent(KEY_MULTIPLIER, FuelModule.SOLID_TEMPERATURE / 1000f).withStyle(ChatFormatting.GRAY);
  private static final Component TOOLTIP_SMELTERY = TConstruct.makeTranslation("jei", "melting.smeltery").withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);
  private static final Component TOOLTIP_MELTER = TConstruct.makeTranslation("jei", "melting.melter").withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);

  /** Tooltip callback for items */
  private static final ITooltipCallback<ItemStack> ITEM_TOOLTIP = (index, isInput, stack, list) -> {
    // index of 1 is the fuel
    if (index == 1) {
      list.add(1, SOLID_TEMPERATURE);
      list.add(2, SOLID_MULTIPLIER);
    }
  };

  /** Tooltip callback for ores */
  private static final ITooltipCallback<FluidStack> METAL_ORE_TOOLTIP = new MeltingFluidCallback(OreRateType.METAL);
  private static final ITooltipCallback<FluidStack> GEM_ORE_TOOLTIP = new MeltingFluidCallback(OreRateType.GEM);

  @Getter
  private final IDrawable icon;
  private final IDrawableStatic solidFuel;

  public MeltingCategory(IGuiHelper helper) {
    super(helper);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(TinkerSmeltery.searedMelter));
    this.solidFuel = helper.drawableBuilder(BACKGROUND_LOC, 164, 0, 18, 20).build();
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.melting;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void draw(MeltingRecipe recipe, PoseStack matrices, double mouseX, double mouseY) {
    super.draw(recipe, matrices, mouseX, mouseY);

    // solid fuel slot
    int temperature = recipe.getTemperature();
    if (temperature <= FuelModule.SOLID_TEMPERATURE) {
      solidFuel.draw(matrices, 1, 19);
    }
  }

  @Override
  public void setRecipe(IRecipeLayout layout, MeltingRecipe recipe, IIngredients ingredients) {
    // input
    IGuiItemStackGroup items = layout.getItemStacks();
    items.init(0, true, 23, 17);
    items.set(ingredients);

    // output
    IGuiFluidStackGroup fluids = layout.getFluidStacks();
    fluids.init(0, false, 96, 4, 32, 32, FluidValues.METAL_BLOCK, false, tankOverlay);
    fluids.set(ingredients);

    // show fuels that are valid for this recipe
    int fluidHeight = 32;
    // solid fuel
    if (recipe.getTemperature() <= FuelModule.SOLID_TEMPERATURE) {
      fluidHeight = 15;
      items.init(1, true, 1, 21);
      items.set(1, MeltingFuelHandler.SOLID_FUELS.get());
      items.addTooltipCallback(ITEM_TOOLTIP);
    }

    // liquid fuel
    fluids.init(-1, true, 4, 4, 12, fluidHeight, 1, false, null);
    fluids.set(-1, MeltingFuelHandler.getUsableFuels(recipe.getTemperature()));

    // change tooltip for ore boosted recipes
    OreRateType oreType = recipe.getOreType();
    if (oreType == OreRateType.METAL) {
      fluids.addTooltipCallback(METAL_ORE_TOOLTIP);
    } else if (oreType == OreRateType.GEM) {
      fluids.addTooltipCallback(GEM_ORE_TOOLTIP);
    } else {
      fluids.addTooltipCallback(MeltingFluidCallback.INSTANCE);
    }
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
        list.add(TextComponent.EMPTY);
        list.add(TOOLTIP_SMELTERY);
        shift = FluidTooltipHandler.appendMaterialNoShift(fluid, smelteryAmount, list) || shift;
        return shift;
      } else {
        return FluidTooltipHandler.appendMaterialNoShift(fluid, smelteryAmount, list);
      }
    }
  }
}
