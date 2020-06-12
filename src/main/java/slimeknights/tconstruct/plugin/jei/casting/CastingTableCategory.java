package slimeknights.tconstruct.plugin.jei.casting;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.CastingBasinRecipe;
import slimeknights.tconstruct.smeltery.recipe.CastingTableRecipe;

public class CastingTableCategory extends AbstractCastingCategory<CastingTableRecipe> {

  private final IDrawable castingTable;
  public CastingTableCategory(IGuiHelper guiHelper) {
    super(guiHelper, TinkerSmeltery.castingTable.get(), "gui.jei.casting.table", 200);
    castingTable = guiHelper.createDrawable(this.getBackgroundLoc(), 141, 0, 16, 16);
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.castingTable;
  }

  @Override
  public Class<? extends CastingTableRecipe> getRecipeClass() {
    return CastingTableRecipe.class;
  }

  @Override
  public void draw(CastingTableRecipe recipe, double mouseX, double mouseY) {
    super.draw(recipe, mouseX, mouseY);
    castingTable.draw(59, 42);
  }
}
