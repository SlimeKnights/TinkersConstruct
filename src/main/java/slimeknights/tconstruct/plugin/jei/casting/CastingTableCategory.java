package slimeknights.tconstruct.plugin.jei.casting;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class CastingTableCategory extends AbstractCastingCategory {
  private static final Component TITLE = TConstruct.makeTranslation("jei", "casting.table");

  public CastingTableCategory(IGuiHelper guiHelper) {
    super(guiHelper, TinkerSmeltery.searedTable.get(), guiHelper.createDrawable(BACKGROUND_LOC, 117, 0, 16, 16));
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.castingTable;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }
}
