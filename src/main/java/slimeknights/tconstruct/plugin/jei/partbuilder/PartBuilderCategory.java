package slimeknights.tconstruct.plugin.jei.partbuilder;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;
import slimeknights.tconstruct.library.tools.layout.Patterns;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tables.TinkerTables;

import java.awt.Color;
import java.util.List;

public class PartBuilderCategory extends AbstractRecipeCategory<IDisplayPartBuilderRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "part_builder.title");
  private static final String KEY_COST = TConstruct.makeTranslationKey("jei", "part_builder.cost");

  private final IDrawable patternButton;
  private final IDrawable materialPlaceholder;

  public PartBuilderCategory(IGuiHelper helper) {
    super(TConstructJEIConstants.PART_BUILDER, TITLE, helper.createDrawableItemLike(TinkerTables.partBuilder), 121, 46);
    this.patternButton = helper.createDrawable(BACKGROUND_LOC, 45, 132, 18, 18);
    this.materialPlaceholder = helper.createDrawableIngredient(TConstructJEIConstants.PATTERN_TYPE, Patterns.INGOT);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IDisplayPartBuilderRecipe recipe, IFocusGroup focuses) {
    builder.addRecipeArrowWidget().setPosition(66, 15);
    MaterialVariant variant = recipe.getMaterial();
    if (!variant.isEmpty()) {
      builder.addText(MaterialTooltipCache.getColoredDisplayName(variant.getVariant()), 118, 9)
        .setPosition(3, 2)
        .setColor(-1)
        .setShadow(true);
      builder.addText(Component.translatable(KEY_COST, recipe.getCost()), 118, 9)
        .setPosition(3, 35)
        .setColor(Color.GRAY.getRGB());
    }
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, IDisplayPartBuilderRecipe recipe, IFocusGroup focuses) {
    // items
    List<ItemStack> materialItems = recipe.getMaterialItems();
    IRecipeSlotBuilder materialSlot = builder.addInputSlot(25, 16)
      .addItemStacks(materialItems).setStandardSlotBackground();
    if (recipe.getMaterial().isEmpty() && materialItems.isEmpty()) {
      materialSlot.setOverlay(materialPlaceholder, 0, 0);
    }
    List<ItemStack> patternItems = recipe.getPatternItems();
    boolean reusablePattern = !patternItems.isEmpty() && patternItems.stream().allMatch(stack -> stack.is(TinkerTags.Items.REUSABLE_PATTERNS));
    builder.addSlot(reusablePattern ? RecipeIngredientRole.CATALYST : RecipeIngredientRole.INPUT, 4, 16)
      .addItemStacks(patternItems).setStandardSlotBackground();
    // patterns
    builder.addInputSlot(46, 16)
      .addIngredient(TConstructJEIConstants.PATTERN_TYPE, recipe.getPattern())
      .setBackground(patternButton, -1, -1);
    // TODO: material ingredient input?

    // output
    List<ItemStack> resultItems = recipe.getResultItems();
    IRecipeSlotBuilder resultSlot = builder.addOutputSlot(96, 15)
      .addItemStacks(resultItems).setOutputSlotBackground();

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
