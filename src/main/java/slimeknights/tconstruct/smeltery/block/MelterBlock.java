package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.shared.block.GuiInventoryBlock;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;

import javax.annotation.Nonnull;
import java.util.Random;

/* TODO: extract base methods to shared class */
public class MelterBlock extends GuiInventoryBlock {
  public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
  public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
  public MelterBlock(Properties props) {
    super(props);
    this.setDefaultState(this.getDefaultState().with(ACTIVE, false));
  }

  /*
   * Block state
   */

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING, ACTIVE);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState()
               .with(ACTIVE, isValidFuelSource(context.getWorld().getBlockState(context.getPos().down())))
               .with(FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Deprecated
  @Override
  public BlockState rotate(BlockState state, Rotation rotation) {
    return state.with(FACING, rotation.rotate(state.get(FACING)));
  }

  @Deprecated
  @Override
  public BlockState mirror(BlockState state, Mirror mirror) {
    return state.with(FACING, mirror.mirror(state.get(FACING)));
  }

  @Deprecated
  @Override
  public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState neighbor, IWorld world, BlockPos pos, BlockPos neighborPos) {
    if (direction == Direction.DOWN) {
      return state.with(ACTIVE, isValidFuelSource(neighbor));
    }
    return state;
  }

  /**
   * Checks if the given state is a valid melter fuel source
   * @param state  State instance
   * @return  True if its a valid fuel source
   */
  protected boolean isValidFuelSource(BlockState state) {
    return TinkerSmeltery.searedTank.contains(state.getBlock());
  }


  /*
   * Block behavior
   */

  @Deprecated
  @Override
  public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return false;
  }

  @Deprecated
  @Override
  @OnlyIn(Dist.CLIENT)
  public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return 1.0F;
  }

  @Override
  public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
    return true;
  }

  @Deprecated
  @Override
  public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return false;
  }

  @Override
  @Deprecated
  public int getLightValue(BlockState state) {
    return state.get(ACTIVE) ? super.getLightValue(state) : 0;
  }

  @Override
  public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
    if(state.get(ACTIVE)) {
      Direction direction = state.get(FACING);
      double x = pos.getX() + 0.5D;
      double y = (double) pos.getY() + (rand.nextFloat() * 6F) / 16F;
      double z = pos.getZ() + 0.5D;
      double frontOffset = 0.52D;
      double sideOffset = rand.nextDouble() * 0.6D - 0.3D;

      spawnFireParticles(world, direction, x, y, z, frontOffset, sideOffset);
    }
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
  protected void spawnFireParticles(IWorld world, Direction direction, double x, double y, double z, double front, double side) {
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


  /*
   * Tile Entity interaction
   */

  @Nonnull
  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return new MelterTileEntity();
  }

  @Override
  protected boolean openGui(PlayerEntity player, World world, BlockPos pos) {
    BlockState state = world.getBlockState(pos);
    if(state.getBlock() == this && state.get(ACTIVE)) {
      return super.openGui(player, world, pos);
    }
    return false;
  }

  @Deprecated
  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    if (ITankTileEntity.interactWithTank(world, pos, player, hand, hit)) {
      return ActionResultType.SUCCESS;
    }
    return super.onBlockActivated(state, world, pos, player, hand, hit);
  }


  /*
   * Comparator
   */

  @Deprecated
  @Override
  public boolean hasComparatorInputOverride(BlockState state) {
    return true;
  }

  @Deprecated
  @Override
  public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
    return ITankTileEntity.getComparatorInputOverride(worldIn, pos);
  }
  // TODO: comparator

  // TODO: tank interaction
}
