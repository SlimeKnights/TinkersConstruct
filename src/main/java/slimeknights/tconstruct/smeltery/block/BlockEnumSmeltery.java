package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;

public class BlockEnumSmeltery<T extends Enum<T> & EnumBlock.IEnumMeta & IStringSerializable> extends EnumBlock<T> implements ITileEntityProvider {

  public BlockEnumSmeltery(PropertyEnum<T> prop, Class<T> clazz) {
    this(Material.rock, prop, clazz);
  }

  public BlockEnumSmeltery(Material material, PropertyEnum<T> prop, Class<T> clazz) {
    super(material, prop, clazz);

    setHardness(3F);
    setResistance(20F);
    setStepSound(soundTypeMetal);
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
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
    if(neighborBlock == Blocks.redstone_wire
       || neighborBlock == Blocks.powered_repeater
       || neighborBlock == Blocks.unpowered_repeater
       || neighborBlock == Blocks.powered_comparator
       || neighborBlock == Blocks.unpowered_comparator
       || neighborBlock == Blocks.redstone_torch) {
      return;
    }
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
