package slimeknights.tconstruct.plugin.jei.modifiers;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.worktable.IModifierWorktableRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.List;

public class ModifierWorktableCategory extends AbstractRecipeCategory<IModifierWorktableRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "modifier_worktable.title");

  private final IDrawable toolIcon;
  private final IDrawable[] slotIcons;
  private final IDrawable modifierButton;
  public ModifierWorktableCategory(IGuiHelper helper) {
    super(TConstructJEIConstants.MODIFIER_WORKTABLE, TITLE, helper.createDrawableItemLike(TinkerTables.modifierWorktable), 121, 35);
    this.toolIcon = helper.createDrawable(BACKGROUND_LOC, 128, 0, 16, 16);
    this.slotIcons = new IDrawable[] {
      helper.createDrawable(BACKGROUND_LOC, 176, 0, 16, 16),
      helper.createDrawable(BACKGROUND_LOC, 208, 0, 16, 16)
    };
    // TODO 1.21: relocate the texture
    this.modifierButton = helper.createDrawable(BACKGROUND_LOC, 81, 181, 18, 18);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IModifierWorktableRecipe recipe, IFocusGroup focuses) {
    builder.addText(recipe.getTitle(), 115, 9)
      .setPosition(3, 2)
      .setColor(0x404040);
    builder.addTooltipArea(0, 2, 121, 11).setTooltip(recipe.getDescription(null));
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, IModifierWorktableRecipe recipe, IFocusGroup focuses) {
    // tools
    List<ItemStack> tools = recipe.getInputTools();
    IRecipeSlotBuilder toolSlot = builder.addSlot(recipe.isToolInput() ? RecipeIngredientRole.INPUT : RecipeIngredientRole.CATALYST, 23, 16)
      .addItemStacks(tools).setStandardSlotBackground();
    if (tools.isEmpty()) {
      toolSlot.setOverlay(toolIcon, 0, 0);
    }
    // input items
    for (int i = 0; i < 2; i++) {
      List<ItemStack> stacks = recipe.getDisplayItems(i);
      IRecipeSlotBuilder slot = builder.addInputSlot(43 + i*18, 16)
        .addItemStacks(stacks)
        .setStandardSlotBackground();
      if (stacks.isEmpty()) {
        slot.setOverlay(slotIcons[i], 0, 0);
      }
    }
    // modifier input
    List<ModifierEntry> modifiers = recipe.getModifierOptions(null);
    IRecipeSlotBuilder modifierSlot = builder.addSlot(recipe.isModifierOutput() ? RecipeIngredientRole.OUTPUT : RecipeIngredientRole.CATALYST, 82, 16)
      .addIngredients(TConstructJEIConstants.MODIFIER_TYPE, modifiers)
      .setBackground(modifierButton, -1, -1);
    if (recipe.linkToolsModifiers() && tools.size() == modifiers.size()) {
      builder.createFocusLink(toolSlot, modifierSlot);
    }
  }

  @Override
  public ResourceLocation getRegistryName(IModifierWorktableRecipe recipe) {
    return recipe.getId();
  }
}
