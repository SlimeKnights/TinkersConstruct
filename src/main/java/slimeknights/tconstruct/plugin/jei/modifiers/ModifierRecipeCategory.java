package slimeknights.tconstruct.plugin.jei.modifiers;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.plugin.jei.util.RecipeTooltipWidget;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.item.CreativeSlotItem;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ModifierRecipeCategory extends AbstractRecipeCategory<IDisplayModifierRecipe> {
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "modifiers.title");

  // translation
  private static final Component TEXT_INCREMENTAL = TConstruct.makeTranslation("jei", "modifiers.incremental");
  private static final String KEY_MIN = TConstruct.makeTranslationKey("jei", "modifiers.level.min");
  private static final String KEY_MAX = TConstruct.makeTranslationKey("jei", "modifiers.level.max");
  private static final String KEY_RANGE = TConstruct.makeTranslationKey("jei", "modifiers.level.range");
  private static final String KEY_EXACT = TConstruct.makeTranslationKey("jei", "modifiers.level.exact");

  /** Draws the slotless input icon. */
  private static final IDrawable SLOTLESS = new IDrawable() {
    @Override
    public int getWidth() {
      return SlotIngredientRenderer.INPUT.getWidth();
    }

    @Override
    public int getHeight() {
      return SlotIngredientRenderer.INPUT.getHeight();
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
      SlotIngredientRenderer.INPUT.render(graphics, null, xOffset, yOffset);
    }
  };

  private final ModifierIngredientRenderer modifierRenderer = new ModifierIngredientRenderer(124, 10);

  private final IDrawable requirements, incremental;
  private final IDrawable[] slotIcons;
  public ModifierRecipeCategory(IGuiHelper helper) {
    super(TConstructJEIConstants.MODIFIERS, TITLE, helper.createDrawableItemStack(CreativeSlotItem.withSlot(new ItemStack(TinkerModifiers.creativeSlotItem), SlotType.UPGRADE)), 128, 77);
    this.slotIcons = new IDrawable[6];
    for (int i = 0; i < 6; i++) {
      slotIcons[i] = helper.createDrawable(BACKGROUND_LOC, 128 + i * 16, 0, 16, 16);
    }
    this.requirements = helper.createDrawable(BACKGROUND_LOC, 128, 17, 16, 16);
    this.incremental = helper.createDrawable(BACKGROUND_LOC, 128, 33, 16, 16);
    clearSlimeskullCache();
  }

  /** Gets the text describing the required modifier level, if any. */
  @Nullable
  private static Component getLevelText(IDisplayModifierRecipe recipe) {
    Component variant = recipe.getVariant();
    if (variant != null) {
      return variant;
    }
    IntRange level = recipe.getLevel();
    int min = level.min();
    int max = level.max();
    // min being 1 means we only have a max level, we check this first as Max Level is better than exact typically
    if (min == 1) {
      if (max < ModifierEntry.VALID_LEVEL.max()) {
        return Component.translatable(KEY_MAX, max);
      }
    } else if (min == max) {
      return Component.translatable(KEY_EXACT, min);
    } else if (max == ModifierEntry.VALID_LEVEL.max()) {
      return Component.translatable(KEY_MIN, min);
    } else {
      return Component.translatable(KEY_RANGE, min, max);
    }
    return null;
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IDisplayModifierRecipe recipe, IFocusGroup focuses) {
    builder.addRecipeArrow().setPosition(71, 33);
    ModifierEntry result = recipe.getDisplayResult();
    Component requirementsError = result.getHook(ModifierHooks.REQUIREMENTS).requirementsError(result);
    if (requirementsError != null) {
      builder.addWidget(new RecipeTooltipWidget(requirements, 66, 58, requirementsError));
    }
    if (recipe.isIncremental()) {
      builder.addWidget(new RecipeTooltipWidget(incremental, 83, 59, TEXT_INCREMENTAL));
    }
    if (recipe.getSlots() == null) {
      List<Component> slotlessTooltip = SlotIngredientRenderer.INPUT.getTooltip(null, TooltipFlag.NORMAL);
      builder.addWidget(new RecipeTooltipWidget(SLOTLESS, 102, 58, slotlessTooltip));
    }
    Component levelText = getLevelText(recipe);
    if (levelText != null) {
      builder.addText(levelText, 85, 9)
        .setPosition(43, 16)
        .setColor(Color.GRAY.getRGB())
        .setTextAlignment(HorizontalAlignment.CENTER);
    }
  }

  /** Adds an input slot with the icon */
  private void addInput(IRecipeLayoutBuilder builder, IDisplayModifierRecipe recipe, int index, int x, int y) {
    List<ItemStack> stacks = recipe.getDisplayItems(index);
    IRecipeSlotBuilder slot = builder.addInputSlot(x, y)
      .addItemStacks(stacks)
      .setStandardSlotBackground();
    // show icon if the slot is empty
    if (stacks.isEmpty()) {
      slot.setOverlay(slotIcons[index], 0, 0);
    }
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, IDisplayModifierRecipe recipe, IFocusGroup focuses) {
    // inputs
    addInput(builder, recipe, 0,  3, 33);
    addInput(builder, recipe, 1, 25, 15);
    addInput(builder, recipe, 2, 47, 33);
    addInput(builder, recipe, 3, 43, 58);
    addInput(builder, recipe, 4,  7, 58);

    // modifiers
    builder.addOutputSlot(3, 3)
      .setCustomRenderer(TConstructJEIConstants.MODIFIER_TYPE, modifierRenderer)
      .addIngredient(TConstructJEIConstants.MODIFIER_TYPE, recipe.getDisplayResult());

    // tool
    List<ItemStack> toolWithoutModifier = recipe.getToolWithoutModifier();
    List<ItemStack> toolWithModifier = recipe.getToolWithModifier();

    // hack: if a single part tool is in the recipe, add variants of it as invisible ingredients
    for (ItemStack stack : toolWithoutModifier) {
      if (stack.is(TinkerTags.Items.SINGLEPART_TOOL) && stack.getItem() instanceof IModifiable modifiable) {
        builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST).addItemStacks(getLookupTools(modifiable));
      }
    }

    // JEI is currently being dumb and using ingredient subtypes within recipe focuses
    // we use a more strict subtype for tools in ingredients so they all show in JEI, but do not care in recipes
    // thus, manually handle the focuses
    IFocus<ItemStack> focus = focuses.getItemStackFocuses(RecipeIngredientRole.CATALYST).findFirst().orElse(null);
    if (focus != null) {
      Item item = focus.getTypedValue().getIngredient().getItem();
      for (ItemStack stack : toolWithoutModifier) {
        if (stack.is(item)) {
          toolWithoutModifier = List.of(stack);
          break;
        }
      }
      for (ItemStack stack : toolWithModifier) {
        if (stack.is(item)) {
          toolWithModifier = List.of(stack);
          break;
        }
      }
    }
    builder.addSlot(RecipeIngredientRole.CATALYST,  25, 38)
      .addItemStacks(toolWithoutModifier).setStandardSlotBackground();
    builder.addSlot(RecipeIngredientRole.CATALYST, 105, 34)
      .addItemStacks(toolWithModifier).setOutputSlotBackground();

    // modifier slots
    SlotCount slots = recipe.getSlots();
    if (slots != null) {
      builder.addInputSlot(102, 58)
        .setCustomRenderer(TConstructJEIConstants.SLOT_TYPE, SlotIngredientRenderer.INPUT)
        .addIngredient(TConstructJEIConstants.SLOT_TYPE, recipe.getSlots());
    }
    // result slots is determined based on the volatile data hook. Its a bit of a heuristic, but is good enough for our usecases
    builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addIngredients(TConstructJEIConstants.SLOT_TYPE, recipe.getResultSlots());
  }

  @Nullable
  @Override
  public ResourceLocation getRegistryName(IDisplayModifierRecipe recipe) {
    return recipe.getRecipeId();
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
  public static void clearSlimeskullCache() {
    LOOKUP_CACHE.clear();
  }
}
