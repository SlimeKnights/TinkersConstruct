package slimeknights.tconstruct.plugin.jei.casting;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.materials.Material;

public class CastingRecipeCategory implements IRecipeCategory<CastingRecipeWrapper> {

  public static String CATEGORY = Util.prefix("casting_table");
  public static ResourceLocation background_loc = Util.getResource("textures/gui/jei/casting.png");

  protected final IDrawable background;
  protected final IDrawableAnimated arrow;

  public final IDrawable castingTable;
  public final IDrawable castingBasin;

  public CastingRecipeCategory(IGuiHelper guiHelper) {
    this.background = guiHelper.createDrawable(background_loc, 0, 0, 141, 61);

    IDrawableStatic arrowDrawable = guiHelper.createDrawable(background_loc, 141, 32, 24, 17);
    this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

    this.castingTable = guiHelper.createDrawable(background_loc, 141, 0, 16, 16);
    this.castingBasin = guiHelper.createDrawable(background_loc, 141, 16, 16, 16);
  }

  @Nonnull
  @Override
  public String getUid() {
    return CATEGORY;
  }

  @Nonnull
  @Override
  public String getTitle() {
    return Util.translate("gui.jei.casting.title");
  }

  @Nonnull
  @Override
  public IDrawable getBackground() {
    return this.background;
  }

  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {
    arrow.draw(minecraft, 79, 25);
  }

  @Override
  public void setRecipe(IRecipeLayout recipeLayout, CastingRecipeWrapper recipe, IIngredients ingredients) {
    IGuiItemStackGroup items = recipeLayout.getItemStacks();
    IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
    fluids.addTooltipCallback(GuiUtil::onFluidTooltip);

    List<FluidStack> input = ingredients.getInputs(FluidStack.class).get(0);
    List<List<ItemStack>> castsList = ingredients.getInputs(ItemStack.class);

    // if there is no cast then the size of the inputs is 0, producing an index error
    List<ItemStack> casts = null;
    if(castsList.size() > 0) {
      casts = castsList.get(0);
    }

    int cap = input.get(0).amount;

    items.init(0, true, 58, 25);
    items.init(1, false, 113, 24);
    items.set(ingredients);

    fluids.init(0, true, 22, 10, 18, 32, Material.VALUE_Block, false, null);
    fluids.set(ingredients);

    // no cast, bigger fluid
    int h = 11;
    if(casts == null || casts.isEmpty()) {
      h += 16;
    }
    fluids.init(1, true, 64, 15, 6, h, cap, false, null);
    // otherwise it tries to get the second input fluidstack
    fluids.set(1, input);
  }

  @Override
  public List<String> getTooltipStrings(int mouseX, int mouseY) {
    return ImmutableList.of();
  }

  @Override
  public IDrawable getIcon() {
    // use the default icon
    return null;
  }

  @Override
  public String getModName() {
    return TConstruct.modName;
  }
}
