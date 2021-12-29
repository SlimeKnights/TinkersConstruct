package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Rotation;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SearedPillarBlock extends SearedBlock {
  public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;
  public SearedPillarBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y));
  }

  @Override
  public BlockState rotate(BlockState state, Rotation rot) {
    switch(rot) {
      case COUNTERCLOCKWISE_90: case CLOCKWISE_90:
        switch(state.getValue(AXIS)) {
          case X:
            return state.setValue(AXIS, Direction.Axis.Z);
          case Z:
            return state.setValue(AXIS, Direction.Axis.X);
        }
    }
    return state;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(AXIS);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
  }
}
