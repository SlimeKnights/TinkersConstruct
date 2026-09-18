package slimeknights.tconstruct.plugin.jei.partbuilder;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.ITextWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.layout.Patterns;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.plugin.jei.material.MaterialTitleIngredientRenderer;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.ArrayList;
import java.util.List;

public class PartBuilderCategory extends AbstractRecipeCategory<IDisplayPartBuilderRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "part_builder.title");
  private static final String KEY_COST = TConstruct.makeTranslationKey("jei", "part_builder.cost");

  private final MaterialTitleIngredientRenderer materialTitleRenderer = new MaterialTitleIngredientRenderer(118, 10);

  private final IDrawable patternButton;
  private final IDrawable materialPlaceholder;

  public PartBuilderCategory(IGuiHelper helper) {
    super(TConstructJEIConstants.PART_BUILDER, TITLE, helper.createDrawableItemLike(TinkerTables.partBuilder), 121, 36);
    this.patternButton = helper.createDrawable(BACKGROUND_LOC, 45, 132, 18, 18);
    this.materialPlaceholder = helper.createDrawableIngredient(TConstructJEIConstants.PATTERN_TYPE, Patterns.INGOT);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IDisplayPartBuilderRecipe recipe, IFocusGroup focuses) {
    builder.addRecipeArrowWidget().setPosition(66, 15);
    Component title = recipe.getDisplayTitle();
    if (title != null && recipe.getMaterial().isEmpty()) {
      ITextWidget widget = builder.addText(title, 118, 9)
        .setPosition(3, 2)
        .setColor(-1)
        .setShadow(true);
      List<Component> tooltip = recipe.getTooltip();
      if (!tooltip.isEmpty()) {
        widget.setTooltip(tooltip);
      }
    }
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, IDisplayPartBuilderRecipe recipe, IFocusGroup focuses) {
    // items
    List<ItemStack> materialItems = recipe.getMaterialItems();
    IRecipeSlotBuilder materialSlot = builder.addInputSlot(25, 16)
      .addItemStacks(materialItems).setStandardSlotBackground();
    if (materialItems.isEmpty()) {
      materialSlot.setOverlay(materialPlaceholder, 0, 0);
    }
    List<ItemStack> patternItems = recipe.getPatternItems();
    boolean reusablePattern = !patternItems.isEmpty() && patternItems.stream().allMatch(stack -> stack.is(TinkerTags.Items.REUSABLE_PATTERNS));
    builder.addSlot(reusablePattern ? RecipeIngredientRole.CATALYST : RecipeIngredientRole.INPUT, 4, 16)
      .addItemStacks(patternItems).setStandardSlotBackground();
    // patterns
    List<Pattern> patterns = recipe.getPatterns();
    IRecipeSlotBuilder patternSlot = builder.addInputSlot(46, 16)
      .addIngredients(TConstructJEIConstants.PATTERN_TYPE, patterns)
      .setBackground(patternButton, -1, -1);
    int cost = recipe.getCost();
    if (cost > 0) {
      patternSlot.addRichTooltipCallback(new PatternTooltip(cost));
    }

    // material input
    MaterialVariant material = recipe.getMaterial();
    if (!material.isEmpty()) {
      builder.addInputSlot(3, 2)
        .setCustomRenderer(TConstructJEIConstants.MATERIAL_TYPE, materialTitleRenderer)
        .addIngredient(TConstructJEIConstants.MATERIAL_TYPE, material);
    }

    // output
    List<ItemStack> resultItems = recipe.getResultItems();
    IRecipeSlotBuilder resultSlot = builder.addOutputSlot(96, 15)
      .addItemStacks(resultItems).setOutputSlotBackground();

    // add focus link between materials and result; practically we only the size for result to be >1 for focus link; but better to be safe
    int resultSize = resultItems.size();
    if (resultSize > 1) {
      List<IRecipeSlotBuilder> linked = new ArrayList<>(3);
      linked.add(resultSlot);
      if (resultSize == materialItems.size()) {
        linked.add(materialSlot);
      }
      if (resultSize == patterns.size()) {
        linked.add(patternSlot);
      }
      if (linked.size() > 1) {
        builder.createFocusLink(linked.toArray(IRecipeSlotBuilder[]::new));
      }
    }
  }

  @Override
  public ResourceLocation getRegistryName(IDisplayPartBuilderRecipe recipe) {
    return recipe.getId();
  }

  /** Tooltip for the pattern showing the cost */
  private record PatternTooltip(Component component) implements IRecipeSlotRichTooltipCallback {
    public PatternTooltip(int cost) {
      this(Component.translatable(KEY_COST, cost).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onRichTooltip(IRecipeSlotView slots, ITooltipBuilder tooltip) {
      tooltip.add(component);
    }
  }
}
