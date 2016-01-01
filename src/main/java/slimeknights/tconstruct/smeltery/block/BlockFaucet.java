package slimeknights.tconstruct.smeltery.block;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.smeltery.tileentity.TileFaucet;

public class BlockFaucet extends BlockContainer {

  // Facing == input, can be any side except bottom, because down always is output direction
  public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
    @Override
    public boolean apply(@Nullable EnumFacing input) {
      return input != EnumFacing.DOWN;
    }
  });

  public BlockFaucet() {
    super(Material.rock);

    setCreativeTab(TinkerRegistry.tabSmeltery);
    setHardness(3F);
    setResistance(20F);
  }

  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, FACING);
  }

  /**
   * Convert the given metadata into a BlockState for this Block
   */
  public IBlockState getStateFromMeta(int meta) {
    if(meta >= EnumFacing.values().length) {
      meta = 1;
    }
    EnumFacing face = EnumFacing.values()[meta];
    if(face == EnumFacing.DOWN) {
      face = EnumFacing.UP;
    }

    return this.getDefaultState().withProperty(FACING, face);
  }

  /**
   * Convert the BlockState into the correct metadata value
   */
  public int getMetaFromState(IBlockState state) {
    return state.getValue(FACING).ordinal();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getRenderType() {
    return 3;
  }


  @SideOnly(Side.CLIENT)
  public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
    return true;
  }

  public boolean isFullCube() {
    return false;
  }

  /**
   * Used to determine ambient occlusion and culling when rebuilding chunks for render
   */
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileFaucet();
  }

  /**
   * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
   * IBlockstate
   */
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    EnumFacing enumfacing = facing.getOpposite();

    if(enumfacing == EnumFacing.DOWN) {
      enumfacing = placer.getHorizontalFacing().getOpposite();
    }

    return this.getDefaultState().withProperty(FACING, enumfacing);
  }
}
