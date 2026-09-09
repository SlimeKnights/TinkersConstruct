package slimeknights.tconstruct.plugin.jei.modifiers;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayTinkerStationRecipe;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/** Common logic between {@link ModifierRecipeCategory} and {@link ToolModificationCategory} */
public abstract class AbstractTinkerStationCategory<T extends IDisplayTinkerStationRecipe> extends AbstractRecipeCategory<T> {
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");

  /** Icons to draw on empty slots */
  private final IDrawable[] slotIcons;

  /**
   * @param recipeType  JEI recipe type for the category
   * @param title       Category title
   * @param icon        Category icon
   */
  public AbstractTinkerStationCategory(IGuiHelper helper, RecipeType<T> recipeType, Component title, IDrawable icon) {
    super(recipeType, title, icon, 128, 77);
    this.slotIcons = new IDrawable[6];
    for (int i = 0; i < 6; i++) {
      slotIcons[i] = helper.createDrawable(BACKGROUND_LOC, 128 + i * 16, 0, 16, 16);
    }
    clearLookupCache();
  }

  @Nullable
  @Override
  public ResourceLocation getRegistryName(T recipe) {
    return recipe.getRecipeId();
  }

  @Nullable
  protected Component getVariantText(T recipe) {
    return recipe.getVariant();
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, T recipe, IFocusGroup focuses) {
    builder.addRecipeArrowWidget().setPosition(71, 33);

    Component levelText = getVariantText(recipe);
    if (levelText != null) {
      builder.addText(levelText, 85, 9)
        .setPosition(43, 16)
        .setColor(Color.GRAY.getRGB())
        .setTextAlignment(HorizontalAlignment.CENTER);
    }
  }

  /** Adds an input slot with the icon */
  private void addInput(IRecipeLayoutBuilder builder, T recipe, int index, int x, int y) {
    List<ItemStack> stacks = recipe.getDisplayItems(index);
    IRecipeSlotBuilder slot = builder.addInputSlot(x, y)
      .addItemStacks(stacks)
      .setStandardSlotBackground();
    // show icon if the slot is empty
    if (stacks.isEmpty()) {
      slot.setOverlay(slotIcons[index], 0, 0);
    }
  }

  /** If true, the tool is a catalyst. If false, its either an input or an output. */
  protected abstract boolean isToolCatalyst(T recipe);

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
    // inputs
    addInput(builder, recipe, 0,  3, 33);
    addInput(builder, recipe, 1, 25, 15);
    addInput(builder, recipe, 2, 47, 33);
    addInput(builder, recipe, 3, 43, 58);
    addInput(builder, recipe, 4,  7, 58);

    // tool
    List<ItemStack> toolWithoutModifier = recipe.getToolWithoutModifier();
    List<ItemStack> toolWithModifier = recipe.getToolWithModifier();

    // hack: if a single part tool is in the recipe, add variants of it as invisible ingredients
    boolean isCatalyst = isToolCatalyst(recipe);
    RecipeIngredientRole withoutModifierRole = isCatalyst ? RecipeIngredientRole.CATALYST : RecipeIngredientRole.INPUT;
    for (ItemStack stack : toolWithoutModifier) {
      if (stack.is(TinkerTags.Items.SINGLEPART_TOOL) && stack.getItem() instanceof IModifiable modifiable) {
        builder.addInvisibleIngredients(withoutModifierRole).addItemStacks(getLookupTools(modifiable));
      }
    }

    IRecipeSlotBuilder withoutModifierSlot = builder.addSlot(withoutModifierRole,  25, 38).addItemStacks(toolWithoutModifier).setStandardSlotBackground();
    RecipeIngredientRole withModifierRole = isCatalyst ? RecipeIngredientRole.CATALYST : RecipeIngredientRole.OUTPUT;
    IRecipeSlotBuilder withModifierSlot = builder.addSlot(withModifierRole, 105, 34).addItemStacks(toolWithModifier).setOutputSlotBackground();
    builder.createFocusLink(withoutModifierSlot, withModifierSlot);
  }

  /* Single part tools hack */
  /** Cache of each list of lookup items for each tool */
  private static final Map<IModifiable,List<ItemStack>> LOOKUP_CACHE = new ConcurrentHashMap<>();
  /** Function to compute lookup items for each tool */
  private static final Function<IModifiable,List<ItemStack>> LOOKUP_GETTER = modifiable -> {
    List<ItemStack> variants = new ArrayList<>();
    // TODO: for double part tools (e.g. travelers), this does leave out a lot of materials. But the size of options will quicky explode. Worth fixing?
    ToolBuildHandler.addVariants(variants::add, modifiable, "");
    return variants;
  };

  /** Gets the tools for lookup for single part tools */
  private static List<ItemStack> getLookupTools(IModifiable modifiable) {
    return LOOKUP_CACHE.computeIfAbsent(modifiable, LOOKUP_GETTER);
  }

  /** TODO 1.21: rename to be more appropiate */
  @Internal
  protected static void clearLookupCache() {
    LOOKUP_CACHE.clear();
  }
}
