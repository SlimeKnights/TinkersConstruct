package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockPunji extends Block {

  public static final PropertyDirection FACING = PropertyDirection.create("facing");
  public static final PropertyBool DUMMY = PropertyBool.create("dummy");

  public BlockPunji() {
    super(Material.plants);

    this.setBlockBounds(0.125f, 0, 0.125f, 0.875f, 0.375f, 0.875f);
    this.setStepSound(Block.soundTypeGrass);
    this.setCreativeTab(TinkerRegistry.tabGadgets);
    this.setHardness(3.0f);

    this.setDefaultState(getBlockState().getBaseState().withProperty(FACING, EnumFacing.DOWN));
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
      meta = EnumFacing.DOWN.ordinal();
    }
    EnumFacing face = EnumFacing.values()[meta];

    return this.getDefaultState().withProperty(FACING, face);
  }

  /**
   * Convert the BlockState into the correct metadata value
   */
  public int getMetaFromState(IBlockState state) {
    return state.getValue(FACING).ordinal();
  }

  /**
   * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
   * IBlockstate
   */
  public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
    EnumFacing enumfacing = facing.getOpposite();

    return this.getDefaultState().withProperty(FACING, enumfacing);
  }

  @Override
  public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
    EnumFacing facing = worldIn.getBlockState(pos).getValue(FACING);
    setBlockBoundsBasedOnState(facing);
  }

  public void setBlockBoundsBasedOnState(EnumFacing facing) {
    float h = 0.375f;

    float xMin = 0.125F;
    float xMax = 0.875F;
    float zMin = 0.125F;
    float zMax = 0.875F;
    float yMin = 0.125F;
    float yMax = 0.875F;

    switch(facing) {
      case DOWN:
        yMin = 0;
        yMax = h;
        break;
      case UP:
        yMin = 1f-h;
        yMax = 1;
        break;
      case SOUTH:
        zMin = 1f-h;
        zMax = 1;
        break;
      case NORTH:
        zMax = h;
        zMin = 0;
        break;
      case EAST:
        xMin = 1f-h;
        xMax = 1;
        break;
      case WEST:
        xMax = h;
        xMin = 0;
        break;
    }

    this.setBlockBounds(xMin, yMin, zMin, xMax, yMax, zMax);
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
    setBlockBoundsBasedOnState(state.getValue(FACING));
    return super.getCollisionBoundingBox(worldIn, pos, state);
  }

  @Override
  public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
    if(entityIn instanceof EntityLiving) {
      float damage = 3f;
      if(entityIn.fallDistance > 0) {
        damage += entityIn.fallDistance * 1.5f + 2f;
      }
      entityIn.attackEntityFrom(DamageSource.cactus, damage);
      ((EntityLiving) entityIn).addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 20, 1));
    }
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean isFullCube() {
    return false;
  }
}
