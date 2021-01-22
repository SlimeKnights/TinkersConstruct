package slimeknights.tconstruct.plugin.jei.melting;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public class MeltingCategory implements IRecipeCategory<MeltingRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/melting.png");
  private static final String KEY_TITLE = Util.makeTranslationKey("jei", "melting.title");
  private static final String KEY_COOLING_TIME = Util.makeTranslationKey("jei", "time");
  private static final String KEY_TEMPERATURE = Util.makeTranslationKey("jei", "melting.temperature");
  private static final String KEY_MULTIPLIER = Util.makeTranslationKey("jei", "melting.multiplier");
  private static final ITextComponent TOOLTIP_ORE = new TranslationTextComponent(Util.makeTranslationKey("jei", "melting.ore"));
  private static final ITextComponent SOLID_TEMPERATURE = new TranslationTextComponent(KEY_TEMPERATURE, FuelModule.SOLID_TEMPERATURE).mergeStyle(TextFormatting.GRAY);
  private static final ITextComponent SOLID_MULTIPLIER = new TranslationTextComponent(KEY_MULTIPLIER, FuelModule.SOLID_TEMPERATURE / 1000f).mergeStyle(TextFormatting.GRAY);

  /** Tooltip callback for items */
  private static final ITooltipCallback<ItemStack> ITEM_TOOLTIP = (index, isInput, stack, list) -> {
    // index of 1 is the fuel
    if (index == 1) {
      list.add(1, SOLID_TEMPERATURE);
      list.add(2, SOLID_MULTIPLIER);
    }
  };

  /** Tooltip callback for fluids */
  private static final ITooltipCallback<FluidStack> FLUID_TOOLTIP = (index, isInput, stack, list) -> {
    ITextComponent name = list.get(0);
    ITextComponent modId = list.get(list.size() - 1);
    list.clear();
    list.add(name);

    // outputs show amounts
    if (index == 0) {
      FluidTooltipHandler.appendMaterial(stack, list);
    }

    // fuels show temperature and quality
    if (index == 1) {
      MeltingFuelHandler.getTemperature(stack.getFluid()).ifPresent(temperature -> {
        list.add(new TranslationTextComponent(KEY_TEMPERATURE, temperature).mergeStyle(TextFormatting.GRAY));
        list.add(new TranslationTextComponent(KEY_MULTIPLIER, temperature / 1000f).mergeStyle(TextFormatting.GRAY));
      });
    }
    list.add(modId);
  };

  @Getter
  private final String title;
  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  private final IDrawableStatic tankOverlay;
  private final IDrawableAnimated heatBar;
  private final IDrawableStatic plus;
  private final IDrawableStatic solidFuel;

  public MeltingCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 132, 40);
    this.icon = helper.createDrawableIngredient(new ItemStack(TinkerSmeltery.searedMelter));
    this.title = ForgeI18n.getPattern(KEY_TITLE);
    this.tankOverlay = helper.createDrawable(BACKGROUND_LOC, 132, 0, 32, 32);
    this.heatBar = helper.drawableBuilder(BACKGROUND_LOC, 164, 0, 3, 16).buildAnimated(200, StartDirection.BOTTOM, false);
    this.plus = helper.drawableBuilder(BACKGROUND_LOC, 132, 34, 6, 6).build();
    this.solidFuel = helper.drawableBuilder(BACKGROUND_LOC, 167, 0, 18, 20).build();
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.melting;
  }

  @Override
  public Class<? extends MeltingRecipe> getRecipeClass() {
    return MeltingRecipe.class;
  }

  @Override
  public void setIngredients(MeltingRecipe recipe, IIngredients ingredients) {
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setOutputLists(VanillaTypes.FLUID, recipe.getDisplayOutput());
  }

  @Override
  public void draw(MeltingRecipe recipe, MatrixStack matrices, double mouseX, double mouseY) {
    heatBar.draw(matrices, 24, 18);
    if (recipe.isOre()) {
      plus.draw(matrices, 87, 31);
    }

    // solid fuel slot
    int temperature = recipe.getTemperature();
    if (temperature <= FuelModule.SOLID_TEMPERATURE) {
      solidFuel.draw(matrices, 1, 19);
    }

    // temperature and time
    FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    String tempString = I18n.format(KEY_TEMPERATURE, temperature);
    fontRenderer.drawString(matrices, tempString, 20, 3, Color.GRAY.getRGB());
    String timeString = I18n.format(KEY_COOLING_TIME, recipe.getTime() / 4);
    int x = 92 - fontRenderer.getStringWidth(timeString);
    fontRenderer.drawString(matrices, timeString, x, 3, Color.GRAY.getRGB());
  }

  @Override
  public List<ITextComponent> getTooltipStrings(MeltingRecipe recipe, double mouseX, double mouseY) {
    if (recipe.isOre() && GuiUtil.isHovered((int)mouseX, (int)mouseY, 87, 31, 16, 16)) {
      return Collections.singletonList(TOOLTIP_ORE);
    }
    return Collections.emptyList();
  }

  @Override
  public void setRecipe(IRecipeLayout layout, MeltingRecipe recipe, IIngredients ingredients) {
    // input
    IGuiItemStackGroup items = layout.getItemStacks();
    items.init(0, true, 27, 17);
    items.set(ingredients);

    // output
    IGuiFluidStackGroup fluids = layout.getFluidStacks();
    fluids.init(0, false, 96, 4, 32, 32, MaterialValues.VALUE_Block, false, tankOverlay);
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
    fluids.init(1, true, 4, 4, 12, fluidHeight, 1, false, null);
    fluids.set(1, MeltingFuelHandler.getUsableFuels(recipe.getTemperature()));
    fluids.addTooltipCallback(FLUID_TOOLTIP);
  }
}
