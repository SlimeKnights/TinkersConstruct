package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Rotation;

import net.minecraft.block.AbstractBlock.Properties;

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
  protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(AXIS);
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
  }
}
