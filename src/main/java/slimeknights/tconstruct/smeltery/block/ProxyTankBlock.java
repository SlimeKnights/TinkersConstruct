package slimeknights.tconstruct.smeltery.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.mantle.block.InventoryBlock;
import slimeknights.tconstruct.smeltery.block.entity.ProxyTankBlockEntity;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/** Block logic for the proxy tank */
public class ProxyTankBlock extends Block implements EntityBlock {
  private static final float LOWER = 0.3125f;
  private static final float UPPER = 0.6875f;
  public ProxyTankBlock(Properties properties) {
    super(properties);
  }


  /* Facing */

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(HORIZONTAL_FACING);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public BlockState rotate(BlockState pState, Rotation pRotation) {
    return pState.setValue(HORIZONTAL_FACING, pRotation.rotate(pState.getValue(HORIZONTAL_FACING)));
  }

  @Override
  public BlockState mirror(BlockState pState, Mirror pMirror) {
    return pState.rotate(pMirror.getRotation(pState.getValue(HORIZONTAL_FACING)));
  }


  /* Block entity */

  @Override
  @Nullable
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new ProxyTankBlockEntity(pos, state);
  }

  @Override
  @Deprecated
  public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
    BlockEntity be = worldIn.getBlockEntity(pos);
    return be != null && be.triggerEvent(id, param);
  }


  /* Inventory */

  @Deprecated
  @Override
  public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
    if (world.getBlockEntity(pos) instanceof ProxyTankBlockEntity tank) {
      boolean clickedTank;
      Direction direction = hit.getDirection();
      if (direction == Direction.DOWN) {
        // down is a solid flat spot, treat it all as items
        clickedTank = false;
      } else {
        Vec3 location = hit.getLocation();
        double x = location.x - pos.getX();
        double z = location.z - pos.getZ();
        // up is a window, corners are tanks and center item
        clickedTank = (x < LOWER || x > UPPER) && (z < LOWER || z > UPPER);
        // if you clicked a side tank, cancel that if we clicked too low
        if (clickedTank && direction != Direction.UP) {
          double y = location.y - pos.getY();
          clickedTank = y > 0.25f;
        }
      }
      tank.interact(player, hand, clickedTank);
    }
    return InteractionResult.SUCCESS;
  }

  @Deprecated
  @Override
  public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock() && world.getBlockEntity(pos) instanceof ProxyTankBlockEntity tank) {
      InventoryBlock.dropInventoryItems(world, pos, tank.getItemTank());
    }
    super.onRemove(state, world, pos, newState, isMoving);
  }


  /* Tank */

  @Deprecated
  @Override
  public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
    return 1.0F;
  }

  @Deprecated
  @Override
  public boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Deprecated
  @Override
  public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
    if (world.getBlockEntity(pos) instanceof ProxyTankBlockEntity tank) {
      return tank.getComparatorStrength();
    }
    return 0;
  }
}
