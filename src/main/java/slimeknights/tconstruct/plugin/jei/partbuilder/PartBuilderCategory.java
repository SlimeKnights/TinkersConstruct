package slimeknights.tconstruct.plugin.jei.partbuilder;

import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;
import slimeknights.tconstruct.library.tools.layout.Patterns;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tables.TinkerTables;

import java.awt.Color;
import java.util.List;

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
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TinkerTables.partBuilder));
  }

  @Override
  public RecipeType<IDisplayPartBuilderRecipe> getRecipeType() {
    return TConstructJEIConstants.PART_BUILDER;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void draw(IDisplayPartBuilderRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
    MaterialVariant variant = recipe.getMaterial();
    if (!variant.isEmpty()) {
      Font fontRenderer = Minecraft.getInstance().font;
      Component name = MaterialTooltipCache.getColoredDisplayName(variant.getVariant());
      graphics.drawString(fontRenderer, name, 3, 2, -1, true);
      String coolingString = I18n.get(KEY_COST, recipe.getCost());
      graphics.drawString(fontRenderer, coolingString, 3, 35, Color.GRAY.getRGB(), false);
    } else if (recipe.getMaterialItems().isEmpty()) {
      GuiUtil.renderPattern(graphics, Patterns.INGOT, 25, 16);
    }
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, IDisplayPartBuilderRecipe recipe, IFocusGroup focuses) {
    // items
    List<ItemStack> materialItems = recipe.getMaterialItems();
    IRecipeSlotBuilder materialSlot = builder.addSlot(RecipeIngredientRole.INPUT, 25, 16).addItemStacks(materialItems);
    builder.addSlot(RecipeIngredientRole.INPUT,  4, 16).addItemStacks(recipe.getPatternItems());
    // patterns
    builder.addSlot(RecipeIngredientRole.INPUT, 46, 16).addIngredient(TConstructJEIConstants.PATTERN_TYPE, recipe.getPattern());
    // TODO: material ingredient input?

    // output
    List<ItemStack> resultItems = recipe.getResultItems();
    IRecipeSlotBuilder resultSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 15).addItemStacks(resultItems);

    // add focus link between materials and result; practically we only the size for result to be >1 for focus link; but better to be safe
    if (resultItems.size() == materialItems.size()) {
      builder.createFocusLink(materialSlot, resultSlot);
    }
  }

  @Override
  public ResourceLocation getRegistryName(IDisplayPartBuilderRecipe recipe) {
    return recipe.getId();
  }
}
