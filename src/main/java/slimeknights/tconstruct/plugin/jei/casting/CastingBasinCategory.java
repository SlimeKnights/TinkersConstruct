package slimeknights.tconstruct.plugin.jei.casting;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class CastingBasinCategory extends AbstractCastingCategory {
  private static final Component TITLE = TConstruct.makeTranslation("jei", "casting.basin");
  public CastingBasinCategory(IGuiHelper guiHelper) {
    super(guiHelper, TinkerSmeltery.searedBasin.get(), guiHelper.createDrawable(BACKGROUND_LOC, 117, 16, 16, 16));
  }

  @Override
  public RecipeType<IDisplayableCastingRecipe> getRecipeType() {
    return TConstructJEIConstants.CASTING_BASIN;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }
}
