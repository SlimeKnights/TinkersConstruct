package slimeknights.tconstruct.plugin.jei.casting;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class CastingBasinCategory extends AbstractCastingCategory {

  public CastingBasinCategory(IGuiHelper guiHelper) {
    super(guiHelper, TinkerSmeltery.searedBasin.get(), "jei.tconstruct.casting.basin", guiHelper.createDrawable(BACKGROUND_LOC, 117, 16, 16, 16));
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.castingBasin;
  }

  @Override
  public boolean isHandled(IDisplayableCastingRecipe recipe) {
    return recipe.getType() == RecipeTypes.CASTING_BASIN;
  }
}
