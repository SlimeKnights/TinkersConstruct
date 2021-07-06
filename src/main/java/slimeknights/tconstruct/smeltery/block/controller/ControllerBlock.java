package slimeknights.tconstruct.smeltery.block.controller;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import slimeknights.mantle.block.InventoryBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedBlock;

/** Shared logic for all multiblock structure controllers */
public abstract class ControllerBlock extends InventoryBlock {
  public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
  public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
  public static final BooleanProperty IN_STRUCTURE = SearedBlock.IN_STRUCTURE;
  protected ControllerBlock(Properties builder) {
    super(builder);
    this.setDefaultState(this.getDefaultState().with(ACTIVE, false).with(IN_STRUCTURE, false));
  }


  /*
   * Block state
   */

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING, ACTIVE, IN_STRUCTURE);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
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


  /*
   * Tile Entity interaction
   */

  /** @return True if the GUI can be opened */
  protected boolean canOpenGui(BlockState state) {
    return state.get(IN_STRUCTURE);
  }

  /** Displays the multiblock's status, typically an error that it cannot form */
  protected boolean displayStatus(PlayerEntity player, World world, BlockPos pos, BlockState state) {
    return false;
  }

  @Override
  protected boolean openGui(PlayerEntity player, World world, BlockPos pos) {
    BlockState state = world.getBlockState(pos);
    if (state.getBlock() == this) {
      if (canOpenGui(state)) {
        return super.openGui(player, world, pos);
      } else {
        return displayStatus(player, world, pos, state);
      }
    }
    return false;
  }


  /*
   * Particles
   */

  /**
   * Spawns fire particles at the given location
   * @param world  World instance
   * @param state  Block state
   * @param x      Block X position
   * @param y      Block Y position
   * @param z      Block Z position
   * @param front  Block front
   * @param side   Block side offset
   */
  protected void spawnFireParticles(IWorld world, BlockState state, double x, double y, double z, double front, double side) {
    spawnFireParticles(world, state, x, y, z, front, side, ParticleTypes.FLAME);
  }

  /**
   * Spawns fire particles at the given location
   * @param world     World instance
   * @param state     Block state
   * @param x         Block X position
   * @param y         Block Y position
   * @param z         Block Z position
   * @param front     Block front
   * @param side      Block side offset
   * @param particle  Particle to draw
   */
  protected void spawnFireParticles(IWorld world, BlockState state, double x, double y, double z, double front, double side, IParticleData particle) {
    switch(state.get(FACING)) {
      case WEST:
        world.addParticle(ParticleTypes.SMOKE, x - front, y, z + side, 0.0D, 0.0D, 0.0D);
        world.addParticle(particle,            x - front, y, z + side, 0.0D, 0.0D, 0.0D);
        break;
      case EAST:
        world.addParticle(ParticleTypes.SMOKE, x + front, y, z + side, 0.0D, 0.0D, 0.0D);
        world.addParticle(particle,            x + front, y, z + side, 0.0D, 0.0D, 0.0D);
        break;
      case NORTH:
        world.addParticle(ParticleTypes.SMOKE, x + side, y, z - front, 0.0D, 0.0D, 0.0D);
        world.addParticle(particle,            x + side, y, z - front, 0.0D, 0.0D, 0.0D);
        break;
      case SOUTH:
        world.addParticle(ParticleTypes.SMOKE, x + side, y, z + front, 0.0D, 0.0D, 0.0D);
        world.addParticle(particle,            x + side, y, z + front, 0.0D, 0.0D, 0.0D);
        break;
    }
  }
}
