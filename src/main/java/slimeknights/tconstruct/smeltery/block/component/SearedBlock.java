package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryComponentBlockEntity;

import javax.annotation.Nullable;

public class SearedBlock extends Block implements EntityBlock {
  public static final BooleanProperty IN_STRUCTURE = BooleanProperty.create("in_structure");

  public SearedBlock(Properties properties) {
    super(properties);
    this.registerDefaultState(this.defaultBlockState().setValue(IN_STRUCTURE, false));
  }

  @Override
  protected void createBlockStateDefinition(Builder<Block,BlockState> builder) {
    builder.add(IN_STRUCTURE);
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new SmelteryComponentBlockEntity(pos, state);
  }

  @Override
  @Deprecated
  public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
    if (!newState.is(this)) {
      BlockEntityHelper.get(SmelteryComponentBlockEntity.class, worldIn, pos).ifPresent(te -> te.notifyMasterOfChange(pos, newState));
    }
    super.onRemove(state, worldIn, pos, newState, isMoving);
  }

  @Override
  public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    SmelteryComponentBlockEntity.updateNeighbors(world, pos, state);
  }

  @Override
  @Deprecated
  public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
    super.triggerEvent(state, worldIn, pos, id, param);
    BlockEntity be = worldIn.getBlockEntity(pos);
    return be != null && be.triggerEvent(id, param);
  }

  @Nullable
  @Override
  public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
    return state.getValue(IN_STRUCTURE) ? BlockPathTypes.DAMAGE_FIRE : BlockPathTypes.OPEN;
  }
}
