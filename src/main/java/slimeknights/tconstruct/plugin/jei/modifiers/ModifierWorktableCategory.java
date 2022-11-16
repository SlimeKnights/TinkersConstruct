package slimeknights.tconstruct.plugin.jei.modifiers;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.ResourceColorManager;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.worktable.IModifierWorktableRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.Collections;
import java.util.List;

public class ModifierWorktableCategory implements IRecipeCategory<IModifierWorktableRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "modifier_worktable.title");

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  private final IDrawable[] slotIcons;
  public ModifierWorktableCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 166, 121, 35);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TinkerTables.modifierWorktable));
    this.slotIcons = new IDrawable[] {
      helper.createDrawable(BACKGROUND_LOC, 176, 0, 16, 16),
      helper.createDrawable(BACKGROUND_LOC, 208, 0, 16, 16)
    };
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @SuppressWarnings("removal")
  @Override
  public ResourceLocation getUid() {
    return TConstructJEIConstants.MODIFIER_WORKTABLE.getUid();
  }

  @SuppressWarnings("removal")
  @Override
  public Class<? extends IModifierWorktableRecipe> getRecipeClass() {
    return TConstructJEIConstants.MODIFIER_WORKTABLE.getRecipeClass();
  }

  @Override
  public RecipeType<IModifierWorktableRecipe> getRecipeType() {
    return TConstructJEIConstants.MODIFIER_WORKTABLE;
  }

  /** Draws a single slot icon */
  private void drawSlot(PoseStack matrices, IModifierWorktableRecipe recipe, int slot, int x, int y) {
    List<ItemStack> stacks = recipe.getDisplayItems(slot);
    if (stacks.isEmpty()) {
      // -1 as the item list includes the output slot, we skip that
      slotIcons[slot].draw(matrices, x, y);
    }
  }

  @Override
  public void draw(IModifierWorktableRecipe recipe, IRecipeSlotsView slots, PoseStack matrixStack, double mouseX, double mouseY) {
    for (int i = 0; i < 2; i++) {
      drawSlot(matrixStack, recipe, i, 43 + i * 18, 16);
    }
    Minecraft.getInstance().font.drawShadow(matrixStack, recipe.getTitle(), 3, 2, ResourceColorManager.WHITE.getValue());
  }

  @Override
  public List<Component> getTooltipStrings(IModifierWorktableRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
    if (mouseY >= 2 && mouseY <= 12) {
      return List.of(recipe.getDescription(null));
    }
    return Collections.emptyList();
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, IModifierWorktableRecipe recipe, IFocusGroup focuses) {
    // items
    builder.addSlot(RecipeIngredientRole.CATALYST, 23, 16).addItemStacks(recipe.getInputTools());
    int max = Math.min(2, recipe.getInputCount());
    for (int i = 0; i < max; i++) {
      builder.addSlot(RecipeIngredientRole.INPUT, 43 + i*18, 16).addItemStacks(recipe.getDisplayItems(i));
    }
    // modifier input
    builder.addSlot(RecipeIngredientRole.CATALYST, 82, 16).addIngredients(TConstructJEIConstants.MODIFIER_TYPE, recipe.getModifierOptions(null));
  }
}
