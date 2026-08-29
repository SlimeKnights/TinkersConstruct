package slimeknights.tconstruct.plugin.jei.casting;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.plugin.jei.util.FluidTooltipCallback;
import slimeknights.tconstruct.plugin.jei.util.RecipeTooltipWidget;

import javax.annotation.Nullable;
import java.awt.Color;
import java.util.List;

/** Shared base logic for the two casting recipe types */
public abstract class AbstractCastingCategory extends AbstractRecipeCategory<IDisplayableCastingRecipe> {
  private static final String KEY_COOLING_TIME = TConstruct.makeTranslationKey("jei", "time");
  private static final String KEY_CAST_KEPT = TConstruct.makeTranslationKey("jei", "casting.cast_kept");
  private static final String KEY_CAST_CONSUMED = TConstruct.makeTranslationKey("jei", "casting.cast_consumed");
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/casting.png");

  private final IDrawable background;
  private final IDrawable tankOverlay;
  private final IDrawable castConsumed;
  private final IDrawable castKept;
  private final IDrawable block;
  private final IGuiHelper guiHelper;

  protected AbstractCastingCategory(IGuiHelper guiHelper, RecipeType<IDisplayableCastingRecipe> recipeType, Component title, Block icon, IDrawable block) {
    super(recipeType, title, guiHelper.createDrawableItemLike(icon), 117, 54);
    this.guiHelper = guiHelper;
    this.background = guiHelper.createDrawable(BACKGROUND_LOC, 0, 0, 117, 54);
    this.tankOverlay = guiHelper.createDrawable(BACKGROUND_LOC, 133, 0, 32, 32);
    this.castConsumed = guiHelper.createDrawable(BACKGROUND_LOC, 141, 32, 13, 11);
    this.castKept = guiHelper.createDrawable(BACKGROUND_LOC, 141, 43, 13, 11);
    this.block = block;
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IDisplayableCastingRecipe recipe, IFocusGroup focuses) {
    int coolingTime = recipe.getCoolingTime();
    builder.addDrawable(guiHelper.drawableBuilder(BACKGROUND_LOC, 117, 32, 24, 17)
                                 .buildAnimated(Math.max(1, coolingTime), StartDirection.LEFT, false), 58, 18);
    if (recipe.hasCast()) {
      boolean consumed = recipe.isConsumed();
      IDrawable drawable = consumed ? castConsumed : castKept;
      MutableComponent tooltip = Component.translatable(consumed ? KEY_CAST_CONSUMED : KEY_CAST_KEPT);
      builder.addWidget(new RecipeTooltipWidget(drawable, 63, 39, tooltip));
    }
    builder.addText(Component.translatable(KEY_COOLING_TIME, coolingTime / 20), 89, 9)
      .setPosition(28, 2)
      .setColor(Color.GRAY.getRGB())
      .setTextAlignment(HorizontalAlignment.CENTER);
  }

  @Override
  public void draw(IDisplayableCastingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
    background.draw(graphics);
    block.draw(graphics, 38, 35);
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, IDisplayableCastingRecipe recipe, IFocusGroup focuses) {
    List<ItemStack> outputs = recipe.getOutputs();
    IRecipeSlotBuilder output = builder.addOutputSlot(93, 18).addItemStacks(recipe.getOutputs());
    // items
    List<ItemStack> casts = recipe.getCastItems();
    if (!casts.isEmpty()) {
      IRecipeSlotBuilder cast = builder.addSlot(recipe.isConsumed() ? RecipeIngredientRole.INPUT : RecipeIngredientRole.CATALYST, 38, 19).addItemStacks(casts);
      // if the same size, tie a focus link to the output and cast; means we have material variants on both
      if (outputs.size() > 1 && casts.size() == outputs.size()) {
        builder.createFocusLink(output, cast);
      }
    }

    // fluids
    // tank fluids
    int capacity = FluidValues.METAL_BLOCK;
    List<FluidStack> inputs = recipe.getFluids();
    IRecipeSlotBuilder tank = builder.addInputSlot(3, 3)
           .addTooltipCallback(FluidTooltipCallback.UNITS)
           .setFluidRenderer(capacity, false, 32, 32)
           .setOverlay(tankOverlay, 0, 0)
           .addIngredients(ForgeTypes.FLUID_STACK, inputs);
    // pouring fluid
    int h = 11;
    if (!recipe.hasCast()) {
      h += 16;
    }
    IRecipeSlotBuilder faucet = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 43, 8)
           .addTooltipCallback(FluidTooltipCallback.UNITS)
           .setFluidRenderer(1, false, 6, h)
           .addIngredients(ForgeTypes.FLUID_STACK, inputs);

    builder.createFocusLink(tank, faucet);
  }

  @Nullable
  @Override
  public ResourceLocation getRegistryName(IDisplayableCastingRecipe recipe) {
    return recipe.getRecipeId();
  }
}
