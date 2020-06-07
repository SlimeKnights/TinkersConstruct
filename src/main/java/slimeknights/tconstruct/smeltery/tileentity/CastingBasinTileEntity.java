package slimeknights.tconstruct.smeltery.tileentity;

import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public class CastingBasinTileEntity extends AbstractCastingTileEntity {

  public CastingBasinTileEntity() {
    super(TinkerSmeltery.basin.get(), RecipeTypes.CASTING_BASIN);
  }

}
