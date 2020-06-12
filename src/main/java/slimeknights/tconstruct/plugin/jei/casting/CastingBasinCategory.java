package slimeknights.tconstruct.plugin.jei.casting;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.CastingBasinRecipe;

public class CastingBasinCategory extends AbstractCastingCategory<CastingBasinRecipe> {

  private final IDrawable castingBasin;
  public CastingBasinCategory(IGuiHelper guiHelper) {
    super(guiHelper, TinkerSmeltery.castingBasin.get(), "gui.jei.casting.basin", 200);
    castingBasin = guiHelper.createDrawable(this.getBackgroundLoc(), 141, 16, 16, 16);
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.castingBasin;
  }

  @Override
  public Class<? extends CastingBasinRecipe> getRecipeClass() {
    return CastingBasinRecipe.class;
  }

  @Override
  public void draw(CastingBasinRecipe recipe, double mouseX, double mouseY) {
    super.draw(recipe, mouseX, mouseY);
    castingBasin.draw(59, 42);
  }
}
