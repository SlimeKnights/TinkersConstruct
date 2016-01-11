package slimeknights.tconstruct.smeltery.block;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
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
    setStepSound(soundTypeMetal);
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
  public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
    if(playerIn.isSneaking()) {
      return false;
    }
    TileEntity te = worldIn.getTileEntity(pos);
    if(te instanceof TileFaucet) {
      ((TileFaucet) te).activate();
      return true;
    }
    return super.onBlockActivated(worldIn, pos, state, playerIn, side, hitX, hitY, hitZ);
  }

  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
    EnumFacing facing = worldIn.getBlockState(pos).getValue(FACING);

    float xMin = 0.25F;
    float xMax = 0.75F;
    float zMin = 0.25F;
    float zMax = 0.75F;
    float yMin = 0.25F;
    float yMax = 0.625F;

    switch(facing) {
      case UP:
        yMin = 0.625F;
        yMax = 1.0F;
        break;
      case SOUTH:
        zMin = 0.625F;
        zMax = 1.0F;
        break;
      case NORTH:
        zMax = 0.375F;
        zMin = 0F;
        break;
      case EAST:
        xMin = 0.625F;
        xMax = 1.0F;
        break;
      case WEST:
        xMax = 0.375F;
        xMin = 0F;
        break;
    }

    this.setBlockBounds(xMin, yMin, zMin, xMax, yMax, zMax);
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
    float xMin = 0.25F;
    float xMax = 0.75F;
    float zMin = 0.25F;
    float zMax = 0.75F;
    float yMin = 0.25F;
    float yMax = 0.625F;

    switch(state.getValue(FACING)) {
      case UP:
        yMin = 0.625F;
        yMax = 1.0F;
        break;
      case SOUTH:
        zMin = 0.625F;
        zMax = 1.0F;
        break;
      case NORTH:
        zMax = 0.375F;
        zMin = 0F;
        break;
      case EAST:
        xMin = 0.625F;
        xMax = 1.0F;
        break;
      case WEST:
        xMax = 0.375F;
        xMin = 0F;
        break;
    }

    return AxisAlignedBB.fromBounds(pos.getX() + xMin, pos.getY() + yMin, pos.getZ() + zMin,
                                    pos.getX() + xMax, pos.getY() + yMax, pos.getZ() + zMax);
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
