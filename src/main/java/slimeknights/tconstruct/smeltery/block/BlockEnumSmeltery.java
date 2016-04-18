package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;

public class BlockEnumSmeltery<T extends Enum<T> & EnumBlock.IEnumMeta & IStringSerializable> extends EnumBlock<T> implements ITileEntityProvider {

  public BlockEnumSmeltery(PropertyEnum<T> prop, Class<T> clazz) {
    this(Material.ROCK, prop, clazz);
  }

  public BlockEnumSmeltery(Material material, PropertyEnum<T> prop, Class<T> clazz) {
    super(material, prop, clazz);

    this.setHardness(3F);
    this.setResistance(20F);
    this.setSoundType(SoundType.METAL);
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
    // todo: fix this once vanilla redstone doesn't fire neighbor update events to all neighbors of neighbors anymore
    if(neighborBlock == Blocks.REDSTONE_WIRE
       || neighborBlock == Blocks.POWERED_REPEATER
       || neighborBlock == Blocks.UNPOWERED_REPEATER
       || neighborBlock == Blocks.POWERED_COMPARATOR
       || neighborBlock == Blocks.UNPOWERED_COMPARATOR
       || neighborBlock == Blocks.REDSTONE_TORCH) {
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
