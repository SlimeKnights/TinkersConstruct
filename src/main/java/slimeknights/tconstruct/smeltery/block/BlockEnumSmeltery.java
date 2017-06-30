package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import slimeknights.mantle.block.EnumBlock;
import slimeknights.mantle.multiblock.IMasterLogic;
import slimeknights.mantle.multiblock.IServantLogic;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.tileentity.TileSmelteryComponent;

public class BlockEnumSmeltery<T extends Enum<T> & EnumBlock.IEnumMeta & IStringSerializable> extends EnumBlock<T>
    implements ITileEntityProvider {

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

  @Nonnull
  @Override
  public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
    return new TileSmelteryComponent();
  }

  /* BlockContainer TE handling */
  @Override
  public void breakBlock(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    TileEntity te = worldIn.getTileEntity(pos);
    if(te instanceof TileSmelteryComponent) {
      ((TileSmelteryComponent) te).notifyMasterOfChange();
    }

    super.breakBlock(worldIn, pos, state);
    worldIn.removeTileEntity(pos);
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    // look for a smeltery (controller directly or through another smeltery block) and notify it that we exist
    for(EnumFacing dir : EnumFacing.values()) {
      TileEntity te = worldIn.getTileEntity(pos.offset(dir));
      if(te instanceof IMasterLogic) {
        TileEntity servant = worldIn.getTileEntity(pos);
        if(servant instanceof IServantLogic) {
          ((IMasterLogic) te).notifyChange((IServantLogic) servant, pos);
          break;
        }
      }
      else if(te instanceof TileSmelteryComponent) {
        TileSmelteryComponent component = (TileSmelteryComponent) te;
        if(component.hasValidMaster()) {
          component.notifyMasterOfChange();
          break;
        }
      }
    }
  }

  @Override
  @Deprecated
  public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
    super.eventReceived(state, worldIn, pos, id, param);
    TileEntity tileentity = worldIn.getTileEntity(pos);
    return tileentity != null && tileentity.receiveClientEvent(id, param);
  }
}
