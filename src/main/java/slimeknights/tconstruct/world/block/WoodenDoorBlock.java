package slimeknights.tconstruct.world.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class WoodenDoorBlock extends DoorBlock {
  public WoodenDoorBlock(Properties builder) {
    super(builder);
  }

  @Nullable
  @Override
  public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
    return state.get(OPEN) ? PathNodeType.DOOR_OPEN : PathNodeType.DOOR_WOOD_CLOSED;
  }
}
