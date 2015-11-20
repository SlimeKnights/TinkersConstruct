package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;

public class BlockEnumSmeltery<T extends Enum<T> & EnumBlock.IEnumMeta> extends EnumBlock<T> implements ITileEntityProvider {

  public BlockEnumSmeltery(Material material, PropertyEnum prop, Class<T> clazz) {
    super(material, prop, clazz);

    this.isBlockContainer = true; // has TE
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileSmelteryComponent();
  }

  /* BlockContainer TE handling */
  @Override
  public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
    TileEntity te = worldIn.getTileEntity(pos);
    if(te instanceof TileSmelteryComponent) {
      ((TileSmelteryComponent) te).notifyMasterOfChange();
    }

    super.breakBlock(worldIn, pos, state);
    worldIn.removeTileEntity(pos);
  }

  @Override
  public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
    TileEntity te = worldIn.getTileEntity(pos);
    if(te instanceof TileSmelteryComponent) {
      ((TileSmelteryComponent) te).notifyMasterOfChange();
    }
  }

  @Override
  public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
    super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
    TileEntity tileentity = worldIn.getTileEntity(pos);
    return tileentity != null && tileentity.receiveClientEvent(eventID, eventParam);
  }
}
