package slimeknights.tconstruct.plugin.jei.casting;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IDrawableWidget;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidgetTooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.display.RecipeSlot;
import slimeknights.tconstruct.plugin.jei.util.CategoryUtil;
import slimeknights.tconstruct.plugin.jei.util.FluidTooltipCallback;
import slimeknights.tconstruct.plugin.jei.util.RecipeSlotWrapper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Shared base logic for the two casting recipe types */
public abstract class AbstractCastingCategory extends AbstractRecipeCategory<IDisplayableCastingRecipe> {
  private static final String KEY_COOLING_TIME = TConstruct.makeTranslationKey("jei", "casting.time");
  private static final Component CAST_KEPT = TConstruct.makeTranslation("jei", "casting.cast_kept");
  private static final Component CAST_CONSUMED = TConstruct.makeTranslation("jei", "casting.cast_consumed");
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/casting.png");
  private static final String CAST_SLOT = "cast";
  private static final String RESULT_SLOT = "result";
  private static final String FLUID_SLOT = "fluid";
  private static final String FAUCET_SLOT = "faucet";

  private final IDrawable background;
  private final IDrawable tankOverlay;
  private final IDrawable castConsumed;
  private final IDrawable castKept;
  private final IDrawable block;
  private final IGuiHelper guiHelper;

  protected AbstractCastingCategory(IGuiHelper guiHelper, RecipeType<IDisplayableCastingRecipe> recipeType, Component title, Block icon, IDrawable block) {
    super(recipeType, title, guiHelper.createDrawableItemLike(icon), 117, 54);
    this.background = guiHelper.createDrawable(BACKGROUND_LOC, 0, 0, 117, 54);
    this.tankOverlay = guiHelper.createDrawable(BACKGROUND_LOC, 133, 0, 32, 32);
    this.castConsumed = guiHelper.createDrawable(BACKGROUND_LOC, 141, 32, 13, 11);
    this.castKept = guiHelper.createDrawable(BACKGROUND_LOC, 141, 43, 13, 11);
    this.block = block;
    this.guiHelper = guiHelper;
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IDisplayableCastingRecipe recipe, IFocusGroup focuses) {
    builder.addDrawableWidget(block).setPosition(38, 35);
    int coolingTime = recipe.getCoolingTime();
    IDrawable arrow = guiHelper.drawableBuilder(BACKGROUND_LOC, 117, 32, 24, 17)
                                  .buildAnimated(Math.max(5, coolingTime), StartDirection.LEFT, false);
    IDrawableWidget arrowWidget = builder.addDrawableWidget(arrow).setPosition(58, 18);
    arrowTooltip:
    {
      if (recipe.isCoolingTimeDynamic()) {
        IRecipeSlotDrawable fluid = CategoryUtil.findSlot(builder.getRecipeSlots().getSlots(), FLUID_SLOT);
        if (fluid != null) {
          arrowWidget.setTooltip(new CoolingArrowTooltip(recipe, fluid));
          break arrowTooltip;
        }
      }
      arrowWidget.setTooltip(Component.translatable(KEY_COOLING_TIME, coolingTime / 20));
    }
    if (recipe.hasCast()) {
      boolean consumed = recipe.isConsumed();
      builder.addDrawableWidget(consumed ? castConsumed : castKept)
        .setPosition(63, 39)
        .setTooltip(consumed ? CAST_CONSUMED : CAST_KEPT);
    }
  }

  @Override
  public void draw(IDisplayableCastingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
    background.draw(graphics);
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, IDisplayableCastingRecipe recipe, IFocusGroup focuses) {
    // fetch focus data
    IFocus<ItemStack> focus = focuses.getItemStackFocuses().findFirst().orElse(null);
    ItemStack focusStack = ItemStack.EMPTY;
    boolean focusOutput = false;
    if (focus != null) {
      focusStack = focus.getTypedValue().getIngredient();
      focusOutput = focus.getRole() == RecipeIngredientRole.OUTPUT;
    }

    List<ItemStack> outputs = recipe.getOutputs(focusStack, focusOutput);
    IRecipeSlotBuilder output = builder.addOutputSlot(93, 18).addItemStacks(outputs).setSlotName(RESULT_SLOT);
    List<IRecipeSlotBuilder> linked = new ArrayList<>(4);
    int outputSize = outputs.size();
    if (outputSize > 1) {
      linked.add(output);
    }

    // items
    List<ItemStack> casts = recipe.getCastItems(focusStack, focusOutput);
    if (!casts.isEmpty()) {
      IRecipeSlotBuilder cast = builder.addSlot(recipe.isConsumed() ? RecipeIngredientRole.INPUT : RecipeIngredientRole.CATALYST, 38, 19).addItemStacks(casts).setSlotName(CAST_SLOT);
      // if the same size, tie a focus link to the output and cast; means we have material variants on both
      if (recipe.linkCastToOutput() && !linked.isEmpty() && casts.size() == outputSize) {
        linked.add(cast);
      }
    }

    // fluids
    // tank fluids
    int capacity = FluidValues.METAL_BLOCK;
    List<FluidStack> inputs = recipe.getFluids(focusStack, focusOutput);
    IRecipeSlotBuilder tank = builder.addInputSlot(3, 3)
           .addRichTooltipCallback(FluidTooltipCallback.UNITS)
           .setFluidRenderer(capacity, false, 32, 32)
           .setOverlay(tankOverlay, 0, 0)
           .addIngredients(ForgeTypes.FLUID_STACK, inputs)
      .setSlotName(FLUID_SLOT);
    // pouring fluid
    int h = 11;
    if (!recipe.hasCast()) {
      h += 16;
    }
    IRecipeSlotBuilder faucet = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 43, 8)
      .addRichTooltipCallback(FluidTooltipCallback.UNITS)
      .setFluidRenderer(1, false, 6, h)
      .addIngredients(ForgeTypes.FLUID_STACK, inputs)
      .setSlotName(FAUCET_SLOT);

    // if requested, and they are the same size, link output and fluid
    if (recipe.linkFluidsToOutput() && !linked.isEmpty() && inputs.size() == outputSize) {
      linked.add(faucet);
      linked.add(tank);
    } else if (inputs.size() > 1) {
      // otherwise, just link two fluid slots together
      builder.createFocusLink(tank, faucet);
    }
    // apply links
    if (linked.size() > 1) {
      builder.createFocusLink(linked.toArray(IRecipeSlotBuilder[]::new));
    }
  }

  @Override
  public void onDisplayedIngredientsUpdate(IDisplayableCastingRecipe recipe, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
    // require the recipe to opt in as it saves a lot of lookups on recipes that don't need this feature
    if (recipe.isSlotsDynamic()) {
      // combine both fluids together into a single slot
      IRecipeSlotDrawable fluid = CategoryUtil.findSlot(recipeSlots, FLUID_SLOT);
      RecipeSlot<FluidStack> fluidSlot;
      if (fluid != null) {
        IRecipeSlotDrawable faucet = CategoryUtil.findSlot(recipeSlots, FAUCET_SLOT);
        if (faucet != null) {
          fluidSlot = new RecipeSlotWrapper<>(fluid, ForgeTypes.FLUID_STACK, FluidStack.EMPTY, faucet);
        } else {
          fluidSlot = new RecipeSlotWrapper<>(fluid, ForgeTypes.FLUID_STACK, FluidStack.EMPTY);
        }
      } else {
        fluidSlot = RecipeSlot.EMPTY_FLUID;
      }

      recipe.onDisplayUpdate(
        recipe.hasCast() ? RecipeSlotWrapper.createItem(recipeSlots, CAST_SLOT) : RecipeSlot.EMPTY_ITEM,
        fluidSlot,
        RecipeSlotWrapper.createItem(recipeSlots, RESULT_SLOT)
      );
    }
  }

  @Nullable
  @Override
  public ResourceLocation getRegistryName(IDisplayableCastingRecipe recipe) {
    return recipe.getRecipeId();
  }

  /** Handles the tooltip for the arrow */
  private record CoolingArrowTooltip(IDisplayableCastingRecipe recipe, IRecipeSlotDrawable fluidSlot) implements IRecipeWidgetTooltipCallback {
    @Override
    public void onTooltip(ITooltipBuilder tooltip) {
      FluidStack fluid = fluidSlot.getDisplayedIngredient(ForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY);
      tooltip.add(Component.translatable(KEY_COOLING_TIME, recipe.getCoolingTime(fluid) / 20));
    }
  }
}
