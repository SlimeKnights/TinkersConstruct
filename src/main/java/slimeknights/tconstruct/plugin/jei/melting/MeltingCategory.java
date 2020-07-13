package slimeknights.tconstruct.plugin.jei.melting;

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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.awt.Color;
import java.util.List;

public class MeltingCategory implements IRecipeCategory<MeltingRecipe>, ITooltipCallback<FluidStack> {
  private static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/melting.png");
  private static final String KEY_TITLE = Util.makeTranslationKey("jei", "melting.title");
  private static final String KEY_TEMPERATURE = Util.makeTranslationKey("jei", "melting.temperature");

  @Getter
  private final String title;
  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  private final IDrawableStatic tankOverlay;
  private final IDrawableAnimated heatBar;

  public MeltingCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 132, 40);
    this.icon = helper.createDrawableIngredient(new ItemStack(TinkerSmeltery.searedMelter));
    this.title = ForgeI18n.getPattern(KEY_TITLE);
    this.tankOverlay = helper.createDrawable(BACKGROUND_LOC, 132, 0, 32, 32);
    this.heatBar = helper.drawableBuilder(BACKGROUND_LOC, 164, 0, 3, 16).buildAnimated(200, StartDirection.BOTTOM, false);
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
    ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutput());
  }

  @Override
  public void draw(MeltingRecipe recipe, double mouseX, double mouseY) {
    heatBar.draw(24, 18);

    String tempString = ForgeI18n.parseMessage(KEY_TEMPERATURE, recipe.getTemperature());
    FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    int x = 56 - fontRenderer.getStringWidth(tempString) / 2;
    fontRenderer.drawString(tempString, x, 3, Color.GRAY.getRGB());
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
    fluids.init(1, true, 4, 4, 12, 32, 1, false, null);
    fluids.set(1, MeltingFuelHandler.getUsableFuels(recipe.getTemperature()));
    fluids.addTooltipCallback(this);
  }

  @Override
  public void onTooltip(int index, boolean input, FluidStack stack, List<String> list) {
    String name = list.get(0);
    String modId = list.get(list.size() - 1);
    list.clear();
    list.add(name);

    // outputs show amounts
    if (index == 0) {
      FluidTooltipHandler.appendMaterial(stack, list);
    }

    // fuels show temperature
    if (index == 1) {
      MeltingFuelHandler.getTemperature(stack.getFluid()).ifPresent((temperature) ->
        list.add(TextFormatting.GRAY + ForgeI18n.parseMessage(KEY_TEMPERATURE, temperature)));
    }
    list.add(modId);
  }
}
