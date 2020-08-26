package slimeknights.tconstruct.plugin.jei.alloying;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
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
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.alloy.recipe.AlloyRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.awt.Color;
import java.util.List;

public class AlloyingCategory implements IRecipeCategory<AlloyRecipe>, ITooltipCallback<FluidStack> {
  private static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/alloying.png");
  private static final String KEY_TITLE = Util.makeTranslationKey("jei", "alloying.title");
  private static final String KEY_TEMPERATURE = Util.makeTranslationKey("jei", "alloying.temperature");

  @Getter
  private final String title;
  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  private final IDrawableStatic tankOverlay;

  public AlloyingCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 132, 40);
    this.icon = helper.createDrawableIngredient(new ItemStack(TinkerSmeltery.alloyTank));
    this.title = ForgeI18n.getPattern(KEY_TITLE);
    this.tankOverlay = helper.createDrawable(BACKGROUND_LOC, 132, 0, 32, 32);
  }

  @Override
  public void onTooltip(int index, boolean input, FluidStack stack, List<ITextComponent> list) {
    ITextComponent name = list.get(0);
    ITextComponent modId = list.get(list.size() - 1);
    list.clear();
    list.add(name);

    // input and output shows amounts
    if (index == 0 || index == 1) {
      FluidTooltipHandler.appendMaterial(stack, list);
    }

    if (index == 2) {
      MeltingFuelHandler.getTemperature(stack.getFluid()).ifPresent((temperature) ->
        list.add(new TranslationTextComponent(KEY_TEMPERATURE, temperature).mergeStyle(TextFormatting.GRAY)));
    }
    list.add(modId);
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.alloying;
  }

  @Override
  public Class<? extends AlloyRecipe> getRecipeClass() {
    return AlloyRecipe.class;
  }

  @Override
  public void setIngredients(AlloyRecipe recipe, IIngredients ingredients) {
    ingredients.setInputLists(VanillaTypes.FLUID, ImmutableList.of(recipe.getFluids()));
    ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutput());
  }

  @Override
  public void draw(AlloyRecipe recipe, MatrixStack matrices, double mouseX, double mouseY) {
    String tempString = I18n.format(KEY_TEMPERATURE, recipe.getTemperature());
    FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    int x = 72 - fontRenderer.getStringWidth(tempString) / 2;
    fontRenderer.drawString(matrices, tempString, x, 3, Color.GRAY.getRGB());
  }

  @Override
  public void setRecipe(IRecipeLayout layout, AlloyRecipe recipe, IIngredients ingredients) {
    // input
    IGuiFluidStackGroup fluids = layout.getFluidStacks();
    fluids.init(0, true, 21, 4, 32, 32, MaterialValues.VALUE_Ingot, false, tankOverlay);
    fluids.set(ingredients);

    // output
    fluids.init(1, false, 96, 4, 32, 32, MaterialValues.VALUE_Ingot, false, tankOverlay);
    fluids.set(ingredients);

    // show fuels that are valid for this recipe
    fluids.init(2, true, 4, 4, 12, 32, 1, false, null);
    fluids.set(2, MeltingFuelHandler.getUsableFuels(recipe.getTemperature()));
    fluids.addTooltipCallback(this);
  }
}
