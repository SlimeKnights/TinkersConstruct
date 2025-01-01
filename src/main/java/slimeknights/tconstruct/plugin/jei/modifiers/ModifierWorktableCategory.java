package slimeknights.tconstruct.plugin.jei.modifiers;

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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
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
  private final IDrawable toolIcon;
  private final IDrawable[] slotIcons;
  public ModifierWorktableCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 166, 121, 35);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TinkerTables.modifierWorktable));
    this.toolIcon = helper.createDrawable(BACKGROUND_LOC, 128, 0, 16, 16);
    this.slotIcons = new IDrawable[] {
      helper.createDrawable(BACKGROUND_LOC, 176, 0, 16, 16),
      helper.createDrawable(BACKGROUND_LOC, 208, 0, 16, 16)
    };
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public RecipeType<IModifierWorktableRecipe> getRecipeType() {
    return TConstructJEIConstants.MODIFIER_WORKTABLE;
  }

  @Override
  public void draw(IModifierWorktableRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
    if (recipe.getInputTools().isEmpty()) {
      toolIcon.draw(graphics, 23, 16);
    }
    for (int i = 0; i < 2; i++) {
      List<ItemStack> stacks = recipe.getDisplayItems(i);
      if (stacks.isEmpty()) {
        slotIcons[i].draw(graphics, 43 + i * 18, 16);
      }
    }
    graphics.drawString(Minecraft.getInstance().font, recipe.getTitle(), 3, 2, 0x404040, false);
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
    builder.addSlot(recipe.isModifierOutput() ? RecipeIngredientRole.OUTPUT : RecipeIngredientRole.CATALYST, 82, 16).addIngredients(TConstructJEIConstants.MODIFIER_TYPE, recipe.getModifierOptions(null));
  }
}
