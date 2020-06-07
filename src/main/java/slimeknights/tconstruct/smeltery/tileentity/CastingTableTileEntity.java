package slimeknights.tconstruct.smeltery.tileentity;

import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class CastingTableTileEntity extends AbstractCastingTileEntity {

  public CastingTableTileEntity() {
    super(TinkerSmeltery.table.get(), RecipeTypes.CASTING_TABLE);
  }
}
