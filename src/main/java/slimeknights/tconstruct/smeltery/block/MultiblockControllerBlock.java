package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.block.InventoryBlock;
import slimeknights.tconstruct.smeltery.tileentity.MultiblockTile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class MultiblockControllerBlock extends InventoryBlock {

  public static DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
  public static BooleanProperty ACTIVE = BooleanProperty.create("active");

  protected MultiblockControllerBlock(Properties props) {
    super(props);

    this.setDefaultState(this.getDefaultState().with(ACTIVE, false));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING, ACTIVE);
  }

  @Nullable
  protected MultiblockTile<?> getTile(World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if(te instanceof MultiblockTile) {
      return ((MultiblockTile<?>) te);
    }
    return null;
  }

  public boolean isActive(World world, BlockPos pos) {
    MultiblockTile<?> te = getTile(world, pos);
    return te != null && te.isActive();
  }

  @Override
  protected boolean openGui(PlayerEntity player, World world, BlockPos pos) {
    if(!isActive(world, pos)) {
      return false;
    }
    return super.openGui(player, world, pos);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    // set rotation
//    return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    return this.getDefaultState()
               .with(FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    // check structure
    MultiblockTile<?> te = getTile(worldIn, pos);
    if(te != null) {
      te.checkMultiblockStructure();
    }
  }


  @Nonnull
  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  /**
   * Spawns fire particles at the given location
   * @param world      World instance
   * @param direction  Block direction
   * @param x          Block X position
   * @param y          Block Y position
   * @param z          Block Z position
   * @param front      Block front
   * @param side       Block side offset
   */
  protected void spawnFireParticles(World world, Direction direction, double x, double y, double z, double front, double side) {
    switch(direction) {
      case WEST:
        world.addParticle(ParticleTypes.SMOKE, x - front, y, z + side, 0.0D, 0.0D, 0.0D);
        world.addParticle(ParticleTypes.FLAME, x - front, y, z + side, 0.0D, 0.0D, 0.0D);
        break;
      case EAST:
        world.addParticle(ParticleTypes.SMOKE, x + front, y, z + side, 0.0D, 0.0D, 0.0D);
        world.addParticle(ParticleTypes.FLAME, x + front, y, z + side, 0.0D, 0.0D, 0.0D);
        break;
      case NORTH:
        world.addParticle(ParticleTypes.SMOKE, x + side, y, z - front, 0.0D, 0.0D, 0.0D);
        world.addParticle(ParticleTypes.FLAME, x + side, y, z - front, 0.0D, 0.0D, 0.0D);
        break;
      case SOUTH:
        world.addParticle(ParticleTypes.SMOKE, x + side, y, z + front, 0.0D, 0.0D, 0.0D);
        world.addParticle(ParticleTypes.FLAME, x + side, y, z + front, 0.0D, 0.0D, 0.0D);
        break;
    }
  }

//  @Override
//  public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
//    // don't rotate, we like our tile entity data and we don't want to create an invalid structure by rotating the controller
//    return false;
//  }
}
