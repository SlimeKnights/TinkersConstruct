package slimeknights.tconstruct.plugin.jei.modifiers;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IDrawableWidget;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayTinkerStationRecipe;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.plugin.jei.util.CategoryUtil;
import slimeknights.tconstruct.plugin.jei.util.RecipeSlotWrapper;
import slimeknights.tconstruct.plugin.jei.util.RecipeSlotsWrapper;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/** Common logic between {@link ModifierRecipeCategory} and {@link ToolTinkeringCategory} */
public abstract class AbstractTinkerStationCategory<T extends IDisplayTinkerStationRecipe> extends AbstractRecipeCategory<T> {
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  protected static final String TOOL_SLOT = "tool_with";
  protected static final String RESULT_TOOL_SLOT = "tool_without";

  /** Icons to draw on empty slots */
  private final IDrawable[] slotIcons;
  /** Arrow to draw when the focus does not produce a valid recipe */
  private final IDrawable errorArrow;

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
    errorArrow = helper.createDrawable(BACKGROUND_LOC, 144, 33, 22, 17);
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

  /** If true, the tool is a catalyst. If false, its either an input or an output. */
  protected abstract boolean isToolCatalyst(T recipe);

  /** Creates the erroring arrow */
  static void createErrorArrow(IRecipeExtrasBuilder builder, IDisplayTinkerStationRecipe recipe, IDrawable errorArrow, int xPos, int yPos) {
    // if the result slot is empty, means we had an error so display that
    List<IRecipeSlotDrawable> slots = builder.getRecipeSlots().getSlots();
    IRecipeSlotDrawable resultSlot = CategoryUtil.findSlot(slots, RESULT_TOOL_SLOT);
    if (resultSlot != null && resultSlot.getDisplayedItemStack().isEmpty()) {
      IDrawableWidget arrow = builder.addDrawableWidget(errorArrow).setPosition(xPos, yPos);
      // need to compute the error message again as no good way to store it between methods
      // fortunately, we can just fetch the item from the input slot; only case we would have such an output error is if that is unique
      IRecipeSlotDrawable toolSlot = CategoryUtil.findSlot(slots, TOOL_SLOT);
      if (toolSlot != null) {
        ItemStack tool = toolSlot.getDisplayedItemStack().orElse(ItemStack.EMPTY);
        if (!tool.isEmpty()) {
          RecipeResult<ItemStack> focusUpdate = recipe.onFocused(tool);
          if (focusUpdate.hasError()) {
            arrow.setTooltip(focusUpdate.getMessage());
          }
        }
      }
    }
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

    createErrorArrow(builder, recipe, errorArrow, 71, 33);
  }

  /** Handles common logic between this and {@link ToolTinkeringExtension} */
  record RecipeLayout(List<ItemStack> toolWithoutModifier, List<ItemStack> toolWithModifier, List<List<ItemStack>> inputs) {
    /**
     * Gets the current layout items from the given recipe
     * @param recipe        Recipe instance
     * @param focuses       Current focuses
     * @return Object containing recipe data
     */
    public static RecipeLayout fromRecipe(IDisplayTinkerStationRecipe recipe, IFocusGroup focuses, int maxInput) {
      // allow the recipe to update based on the focuses
      // usually will
      IFocus<ItemStack> focus = focuses.getItemStackFocuses().findFirst().orElse(null);
      ItemStack focusStack = ItemStack.EMPTY;
      boolean focusOutput = false;
      if (focus != null) {
        focusStack = focus.getTypedValue().getIngredient();
        focusOutput = focus.getRole() == RecipeIngredientRole.OUTPUT;
      }

      // allow tools to directly respond to focus
      List<ItemStack> toolWithoutModifier = recipe.getToolWithoutModifier(focusStack, focusOutput);
      List<ItemStack> toolWithModifier = recipe.getToolWithModifier(focusStack, focusOutput);
      // or use the specialized method
      if (!focusOutput && recipe.isTool(focusStack)) {
        // make the stack count as large as the recipe allows. This should also automatically update the size in the result
        ItemStack toolStack = focusStack.copyWithCount(recipe.getMaxToolSize(focusStack));
        // ask the recipe if it wishes to adjust sizes
        RecipeResult<ItemStack> focusUpdate = recipe.onFocused(toolStack);
        // on success, update the input to the focus stack and the output to the result
        if (focusUpdate.isSuccess()) {
          toolWithoutModifier = List.of(toolStack);
          toolWithModifier = List.of(focusUpdate.getResult());
          // on error, make the input the stack and the output a barrier
        } else if (focusUpdate.hasError()) {
          toolWithoutModifier = List.of(toolStack);
          toolWithModifier = List.of();
        } else if (toolStack.is(TinkerTags.Items.SINGLEPART_TOOL)) {
          // on pass, just filter the items to only show the focus tool
          Item item = toolStack.getItem();
          Predicate<ItemStack> filter = stack -> stack.is(item);
          toolWithoutModifier = toolWithoutModifier.stream().filter(filter).toList();
          toolWithModifier = toolWithModifier.stream().filter(filter).toList();
        }
      }

      // fetch inputs with respect to current focus
      List<List<ItemStack>> inputs = new ArrayList<>(maxInput);
      for (int i = 0; i < maxInput; i++) {
        inputs.add(recipe.getDisplayItems(i, focusStack, focusOutput));
      }

      return new RecipeLayout(toolWithoutModifier, toolWithModifier, inputs);
    }

    /** Adds hidden ingredients for singlepart tools. */
    public void handleSinglepart(IRecipeLayoutBuilder builder, RecipeIngredientRole role) {
      for (ItemStack stack : toolWithoutModifier) {
        if (stack.is(TinkerTags.Items.SINGLEPART_TOOL) && stack.getItem() instanceof IModifiable modifiable) {
          builder.addInvisibleIngredients(role).addItemStacks(getLookupTools(modifiable));
        }
      }
    }
  }

  /** Adds an input slot with the icon */
  private IRecipeSlotBuilder addInput(IRecipeLayoutBuilder builder, List<List<ItemStack>> inputs, int index, int x, int y) {
    List<ItemStack> stacks = inputs.get(index);
    IRecipeSlotBuilder slot = builder.addInputSlot(x, y)
      .addItemStacks(stacks)
      .setStandardSlotBackground();
    // show icon if the slot is empty
    if (stacks.isEmpty()) {
      slot.setOverlay(slotIcons[index+1], 0, 0);
    }
    return slot;
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses) {
    // always grab all 5 slots for simplicity
    RecipeLayout layout = RecipeLayout.fromRecipe(recipe, focuses, 5);

    // inputs
    List<List<ItemStack>> inputs = layout.inputs;
    IRecipeSlotBuilder[] inputSlots = {
      addInput(builder, inputs, 0, 3, 33),
      addInput(builder, inputs, 1, 25, 15),
      addInput(builder, inputs, 2, 47, 33),
      addInput(builder, inputs, 3, 43, 58),
      addInput(builder, inputs, 4, 7, 58)
    };

    // hack: if a single part tool is in the recipe, add variants of it as invisible ingredients
    boolean isCatalyst = isToolCatalyst(recipe);
    RecipeIngredientRole withoutModifierRole = isCatalyst ? RecipeIngredientRole.CATALYST : RecipeIngredientRole.INPUT;
    layout.handleSinglepart(builder, withoutModifierRole);

    // add tool slots
    List<ItemStack> toolWithoutModifier = layout.toolWithoutModifier;
    IRecipeSlotBuilder withoutModifierSlot = builder.addSlot(withoutModifierRole,  25, 38).addItemStacks(toolWithoutModifier).setStandardSlotBackground().setSlotName(TOOL_SLOT);
    RecipeIngredientRole withModifierRole = isCatalyst ? RecipeIngredientRole.RENDER_ONLY : RecipeIngredientRole.OUTPUT;
    List<ItemStack> toolWithModifier = layout.toolWithModifier;
    IRecipeSlotBuilder withModifierSlot = builder.addSlot(withModifierRole, 105, 34).addItemStacks(toolWithModifier).setOutputSlotBackground().setSlotName(RESULT_TOOL_SLOT);

    // apply focus links
    int size = toolWithModifier.size();
    if (size > 1) {
      int[] linkToOutput = recipe.linkToOutput();
      if (linkToOutput.length > 0) {
        // if given a list, filter to ensure they are all valid
        // need input slot size to match output size
        IRecipeSlotBuilder[] linked = Stream.concat(
          // include both tool slots if they are the same size
          toolWithoutModifier.size() == size ? Stream.of(withModifierSlot, withoutModifierSlot) : Stream.of(withModifierSlot),
          Arrays.stream(linkToOutput).filter(i -> i < inputSlots.length && layout.inputs.get(i).size() == size).mapToObj(i -> inputSlots[i])
        ).toArray(IRecipeSlotBuilder[]::new);
        if (linked.length > 0) {
          builder.createFocusLink(linked);
        }
        // if no links, try linking tool to output
      } else if (toolWithoutModifier.size() == size) {
        builder.createFocusLink(withoutModifierSlot, withModifierSlot);
      }
    }
  }

  @Override
  public void onDisplayedIngredientsUpdate(T recipe, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
    // handle dynamic hook
    if (recipe.isSlotsDynamic()) {
      recipe.onDisplayUpdate(
        RecipeSlotWrapper.createItem(recipeSlots, TOOL_SLOT),
        RecipeSlotsWrapper.createItem(recipeSlots.subList(0, 5)),
        RecipeSlotWrapper.createItem(recipeSlots, RESULT_TOOL_SLOT)
      );
    }
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
