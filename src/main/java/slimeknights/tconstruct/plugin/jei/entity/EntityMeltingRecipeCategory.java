package slimeknights.tconstruct.plugin.jei.entity;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.mantle.plugin.jei.MantleJEIConstants;
import slimeknights.mantle.plugin.jei.entity.EntityIngredientRenderer;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.plugin.jei.util.FluidTooltipCallback;

import java.awt.Color;
import java.util.List;

/**
 * Entity melting display in JEI
 */
public class EntityMeltingRecipeCategory extends AbstractRecipeCategory<EntityMeltingRecipe> {
  public static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/melting.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "entity_melting.title");
  private static final String KEY_PER_HEARTS = TConstruct.makeTranslationKey("jei", "entity_melting.per_hearts");
  private static final Component TOOLTIP_PER_HEART = Component.translatable(TConstruct.makeTranslationKey("jei", "entity_melting.per_heart")).withStyle(ChatFormatting.GRAY);

  /** Renderer instance to use in this category */
  private final EntityIngredientRenderer entityRenderer = new EntityIngredientRenderer(32);

  private final IDrawable background;
  private final IDrawable arrow;
  private final IDrawable tank;

  public EntityMeltingRecipeCategory(IGuiHelper helper) {
    super(TConstructJEIConstants.ENTITY_MELTING, TITLE, helper.createDrawable(BACKGROUND_LOC, 174, 41, 16, 16), 150, 62);
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 41, 150, 62);
    this.arrow = helper.drawableBuilder(BACKGROUND_LOC, 150, 41, 24, 17).buildAnimated(200, StartDirection.LEFT, false);
    this.tank = helper.createDrawable(BACKGROUND_LOC, 150, 74, 16, 16);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, EntityMeltingRecipe recipe, IFocusGroup focuses) {
    builder.addDrawableWidget(arrow).setPosition(71, 21);
    builder.addText(Component.literal(Float.toString(recipe.getDamage() / 2f)), 84, 9)
      .setPosition(0, 8)
      .setColor(Color.RED.getRGB())
      .setTextAlignment(HorizontalAlignment.RIGHT);
  }

  @Override
  public void draw(EntityMeltingRecipe recipe, IRecipeSlotsView slot, GuiGraphics graphics, double mouseX, double mouseY) {
    background.draw(graphics);
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, EntityMeltingRecipe recipe, IFocusGroup focuses) {
    // inputs, filtered by spawn egg item
    EntityIngredient input = recipe.getIngredient();
    IIngredientAcceptor<?> entities = builder.addInputSlot(19, 11)
                                             .setCustomRenderer(MantleJEIConstants.ENTITY_TYPE, entityRenderer)
                                             .addIngredients(MantleJEIConstants.ENTITY_TYPE, input.getDisplay());
    // add spawn eggs as hidden inputs
    IIngredientAcceptor<?> eggs = builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(input.getEggs());
    builder.createFocusLink(entities, eggs);

    // output
    builder.addOutputSlot(115, 11)
           .setFluidRenderer(FluidValues.INGOT * 2, false, 16, 32)
           .addRichTooltipCallback(new FluidTooltip(recipe.getDamage()))
           .addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutput());

    // show fuels that are valid for this recipe
    builder.addSlot(RecipeIngredientRole.CATALYST, 75, 43)
           .setFluidRenderer(1, false, 16, 16)
           .setOverlay(tank, 0, 0)
           .addRichTooltipCallback(FluidTooltipCallback.NO_AMOUNT)
           .addIngredients(ForgeTypes.FLUID_STACK, MeltingFuelHandler.getUsableFuels(1));
  }

  @Override
  public ResourceLocation getRegistryName(EntityMeltingRecipe recipe) {
    return recipe.getId();
  }

  /** Tooltip for relevant damage on the fluid */
  private record FluidTooltip(int damage) implements FluidTooltipCallback {
    @Override
    public void onFluidTooltip(FluidStack fluid, IRecipeSlotView recipeSlotView, List<Component> list) {
      // add fluid units
      FluidTooltipHandler.appendMaterial(fluid, list);
      // output rate
      if (damage == 2) {
        list.add(TOOLTIP_PER_HEART);
      } else {
        list.add(Component.translatable(KEY_PER_HEARTS, damage / 2f).withStyle(ChatFormatting.GRAY));
      }
    }
  }
}
