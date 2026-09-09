package slimeknights.tconstruct.plugin.jei.modifiers;

import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayToolModification;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tables.TinkerTables;

/** Displays tool modifications that are not adding modifiers, such as part swapping or tool damaging. */
public class ToolModificationCategory extends AbstractTinkerStationCategory<IDisplayToolModification> {
  private static final Component TITLE = TConstruct.makeTranslation("jei", "tool_modification.title");

  public ToolModificationCategory(IGuiHelper helper) {
    super(helper, TConstructJEIConstants.TOOL_MODIFICATION, TITLE, helper.createDrawableItemLike(TinkerTables.tinkersAnvil));
  }

  @Override
  protected boolean isToolCatalyst(IDisplayToolModification recipe) {
    return recipe.isToolCatalyst();
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, IDisplayToolModification recipe, IFocusGroup focuses) {
    super.createRecipeExtras(builder, recipe, focuses);

    // add title and tooltip
    builder.addText(recipe.getTitle(), 124, 10).setTextAlignment(HorizontalAlignment.CENTER).setPosition(3, 3).setTooltip(recipe.getTooltip());
  }
}
