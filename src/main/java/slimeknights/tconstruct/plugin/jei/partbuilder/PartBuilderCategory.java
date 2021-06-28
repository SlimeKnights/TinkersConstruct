package slimeknights.tconstruct.plugin.jei.partbuilder;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.tables.TinkerTables;

import java.awt.Color;
import java.util.Arrays;

public class PartBuilderCategory implements IRecipeCategory<IDisplayPartBuilderRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/tinker_station.png");
  private static final String KEY_TITLE = Util.makeTranslationKey("jei", "part_builder.title");
  private static final String KEY_COST = Util.makeTranslationKey("jei", "part_builder.cost");

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  @Getter
  private final String title;
  public PartBuilderCategory(IGuiHelper helper) {
    this.title = ForgeI18n.getPattern(KEY_TITLE);
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 117, 121, 46);
    this.icon = helper.createDrawableIngredient(new ItemStack(TinkerTables.partBuilder));
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.partBuilder;
  }

  @Override
  public Class<? extends IDisplayPartBuilderRecipe> getRecipeClass() {
    return IDisplayPartBuilderRecipe.class;
  }

  @Override
  public void setIngredients(IDisplayPartBuilderRecipe recipe, IIngredients ingredients) {
    ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(MaterialItemList.getItems(recipe.getMaterialId()), recipe.getPatternItems()));
    ingredients.setInput(JEIPlugin.PATTERN_TYPE, recipe.getPattern());
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
  }

  @Override
  public void draw(IDisplayPartBuilderRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
    FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    IMaterial material = recipe.getMaterial();
    fontRenderer.drawStringWithShadow(matrixStack, I18n.format(material.getTranslationKey()), 3, 2, material.getColor().color);
    String coolingString = I18n.format(KEY_COST, recipe.getCost());
    fontRenderer.drawString(matrixStack, coolingString, 3, 35, Color.GRAY.getRGB());
  }

  @Override
  public void setRecipe(IRecipeLayout layout, IDisplayPartBuilderRecipe recipe, IIngredients ingredients) {
    IGuiItemStackGroup items = layout.getItemStacks();
    items.init(0, true, 24, 15);
    items.init(1, true,  3, 15);
    items.init(2, false, 95, 14);
    items.set(ingredients);

    IGuiIngredientGroup<Pattern> patterns = layout.getIngredientsGroup(JEIPlugin.PATTERN_TYPE);
    patterns.init(0, true, 46, 16);
    patterns.set(ingredients);
  }
}
