package slimeknights.tconstruct.plugin.jei.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.mantle.plugin.jei.MantleJEIConstants;
import slimeknights.mantle.plugin.jei.entity.EntityIngredientRenderer;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.plugin.jei.util.IRecipeTooltipReplacement;

import java.awt.Color;
import java.util.List;

/**
 * Entity melting display in JEI
 */
public class EntityMeltingRecipeCategory implements IRecipeCategory<EntityMeltingRecipe> {
  public static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/melting.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "entity_melting.title");
  private static final String KEY_PER_HEARTS = TConstruct.makeTranslationKey("jei", "entity_melting.per_hearts");
  private static final Component TOOLTIP_PER_HEART = Component.translatable(TConstruct.makeTranslationKey("jei", "entity_melting.per_heart")).withStyle(ChatFormatting.GRAY);

  /** Map of damage value to tooltip callbacks */
  private static final Int2ObjectMap<IRecipeSlotTooltipCallback> TOOLTIP_MAP = new Int2ObjectOpenHashMap<>();

  /** Renderer instance to use in this category */
  private final EntityIngredientRenderer entityRenderer = new EntityIngredientRenderer(32);

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  private final IDrawable arrow;
  private final IDrawable tank;

  public EntityMeltingRecipeCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 41, 150, 62);
    this.icon = helper.createDrawable(BACKGROUND_LOC, 174, 41, 16, 16);
    this.arrow = helper.drawableBuilder(BACKGROUND_LOC, 150, 41, 24, 17).buildAnimated(200, StartDirection.LEFT, false);
    this.tank = helper.createDrawable(BACKGROUND_LOC, 150, 74, 16, 16);
  }

  @Override
  public RecipeType<EntityMeltingRecipe> getRecipeType() {
    return TConstructJEIConstants.ENTITY_MELTING;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void draw(EntityMeltingRecipe recipe, IRecipeSlotsView slot, GuiGraphics graphics, double mouseX, double mouseY) {
    arrow.draw(graphics, 71, 21);

    // draw damage string next to the heart icon
    String damage = Float.toString(recipe.getDamage() / 2f);
    Font fontRenderer = Minecraft.getInstance().font;
    int x = 84 - fontRenderer.width(damage);
    graphics.drawString(fontRenderer, damage, x, 8, Color.RED.getRGB(), false);
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, EntityMeltingRecipe recipe, IFocusGroup focuses) {
    // inputs, filtered by spawn egg item
    EntityIngredient input = recipe.getIngredient();
    IIngredientAcceptor<?> entities = builder.addSlot(RecipeIngredientRole.INPUT, 19, 11)
                                             .setCustomRenderer(MantleJEIConstants.ENTITY_TYPE, entityRenderer)
                                             .addIngredients(MantleJEIConstants.ENTITY_TYPE, input.getDisplay());
    // add spawn eggs as hidden inputs
    IIngredientAcceptor<?> eggs = builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(input.getEggs());
    builder.createFocusLink(entities, eggs);

    // output
    builder.addSlot(RecipeIngredientRole.OUTPUT, 115, 11)
           .setFluidRenderer(FluidValues.INGOT * 2, false, 16, 32)
           .addTooltipCallback(TOOLTIP_MAP.computeIfAbsent(recipe.getDamage(), FluidTooltip::new))
           .addIngredient(ForgeTypes.FLUID_STACK, recipe.getOutput());

    // show fuels that are valid for this recipe
    builder.addSlot(RecipeIngredientRole.CATALYST, 75, 43)
           .setFluidRenderer(1, false, 16, 16)
           .setOverlay(tank, 0, 0)
           .addTooltipCallback(IRecipeTooltipReplacement.EMPTY)
           .addIngredients(ForgeTypes.FLUID_STACK, MeltingFuelHandler.getUsableFuels(1));
  }

  /** Tooltip for relevant damage on the fluid */
  private record FluidTooltip(int damage) implements IRecipeTooltipReplacement {
    @Override
    public void addMiddleLines(IRecipeSlotView recipeSlotView, List<Component> list) {
      // add fluid units
      recipeSlotView.getDisplayedIngredient(ForgeTypes.FLUID_STACK).ifPresent(fluid -> FluidTooltipHandler.appendMaterial(fluid, list));
      // output rate
      if (damage == 2) {
        list.add(TOOLTIP_PER_HEART);
      } else {
        list.add(Component.translatable(KEY_PER_HEARTS, damage / 2f).withStyle(ChatFormatting.GRAY));
      }
    }
  }
}
