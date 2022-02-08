package slimeknights.tconstruct.plugin.jei.partbuilder;

import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.ResourceColorManager;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.tables.TinkerTables;

import java.awt.Color;
import java.util.Arrays;
import java.util.Objects;

public class PartBuilderCategory implements IRecipeCategory<IDisplayPartBuilderRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "part_builder.title");
  private static final String KEY_COST = TConstruct.makeTranslationKey("jei", "part_builder.cost");

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  public PartBuilderCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 117, 121, 46);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(TinkerTables.partBuilder));
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
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void setIngredients(IDisplayPartBuilderRecipe recipe, IIngredients ingredients) {
    ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(MaterialItemList.getItems(recipe.getMaterial().getVariant()), recipe.getPatternItems()));
    ingredients.setInput(JEIPlugin.PATTERN_TYPE, recipe.getPattern());
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
  }

  @Override
  public void draw(IDisplayPartBuilderRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
    Font fontRenderer = Minecraft.getInstance().font;
    Component name = MaterialTooltipCache.getColoredDisplayName(recipe.getMaterial().getVariant());
    fontRenderer.drawShadow(matrixStack, name.getString(), 3, 2, Objects.requireNonNullElse(name.getStyle().getColor(), ResourceColorManager.WHITE).getValue());
    String coolingString = I18n.get(KEY_COST, recipe.getCost());
    fontRenderer.draw(matrixStack, coolingString, 3, 35, Color.GRAY.getRGB());
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
