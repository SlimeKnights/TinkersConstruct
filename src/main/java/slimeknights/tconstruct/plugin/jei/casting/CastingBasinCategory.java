package slimeknights.tconstruct.plugin.jei.casting;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class CastingBasinCategory extends AbstractCastingCategory<ItemCastingRecipe.Basin> {

  public CastingBasinCategory(IGuiHelper guiHelper) {
    super(guiHelper, TinkerSmeltery.castingBasin.get(), "jei.tconstruct.casting.basin", guiHelper.createDrawable(BACKGROUND_LOC, 117, 16, 16, 16));
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.castingBasin;
  }

  @Override
  public Class<? extends ItemCastingRecipe.Basin> getRecipeClass() {
    return ItemCastingRecipe.Basin.class;
  }
}
