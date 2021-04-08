package slimeknights.tconstruct.plugin.jei.casting;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.util.Identifier;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class CastingTableCategory extends AbstractCastingCategory {

  public CastingTableCategory(IGuiHelper guiHelper) {
    super(guiHelper, TinkerSmeltery.castingTable.get(), "jei.tconstruct.casting.table", guiHelper.createDrawable(BACKGROUND_LOC, 117, 0, 16, 16));
  }

  @Override
  public Identifier getUid() {
    return TConstructRecipeCategoryUid.castingTable;
  }

  @Override
  public boolean isHandled(IDisplayableCastingRecipe recipe) {
    return recipe.getType() == RecipeTypes.CASTING_TABLE;
  }
}
