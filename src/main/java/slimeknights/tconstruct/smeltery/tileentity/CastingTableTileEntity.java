package slimeknights.tconstruct.smeltery.tileentity;

import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.AbstractCastingRecipe;

public class CastingTableTileEntity extends CastingTileEntity {

  public CastingTableTileEntity() {
    this(TinkerSmeltery.table.get(), TinkerSmeltery.tableRecipeType);
  }
  public CastingTableTileEntity(TileEntityType<?> tileEntityTypeIn, IRecipeType<? extends AbstractCastingRecipe> recipeType) {
    super(tileEntityTypeIn, recipeType);
  }
}
