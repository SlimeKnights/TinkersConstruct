package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.AbstractCastingRecipe;

public class CastingBasinTileEntity extends CastingTileEntity {

  public CastingBasinTileEntity() {
    this(TinkerSmeltery.basin.get(), TinkerSmeltery.basinRecipeType);
  }

  public CastingBasinTileEntity(TileEntityType<?> tileEntityTypeIn, IRecipeType<? extends AbstractCastingRecipe> recipeType) {
    super(tileEntityTypeIn, recipeType);
  }
}
