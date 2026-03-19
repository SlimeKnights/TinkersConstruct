package slimeknights.tconstruct.smeltery.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.util.CombatHelper;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.shared.block.KnightMetalBlock;

/** Combination of {@link FluidCannonBlock} with {@link KnightMetalBlock} */
public class KnightMetalFluidCannonBlock extends FluidCannonBlock {
  public KnightMetalFluidCannonBlock(Properties properties, int capacity, float power, float velocity, float inaccuracy) {
    super(properties, capacity, power, velocity, inaccuracy);
  }

  @Override
  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return KnightMetalBlock.SHAPE;
  }

  @Nullable
  @Override
  public BlockPathTypes getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
    return BlockPathTypes.DAMAGE_OTHER;
  }

  @Override
  public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    entity.hurt(CombatHelper.damageSource(level, TinkerDamageTypes.KNIGHTMETAL), KnightMetalBlock.BLOCK_DAMAGE);
  }
}
