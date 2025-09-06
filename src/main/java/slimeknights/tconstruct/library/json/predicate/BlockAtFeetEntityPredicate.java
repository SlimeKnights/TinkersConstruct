package slimeknights.tconstruct.library.json.predicate;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;

/** @deprecated use {@link slimeknights.mantle.data.predicate.entity.BlockAtEntityPredicate} */
@Deprecated
public record BlockAtFeetEntityPredicate(IJsonPredicate<BlockState> block) implements LivingEntityPredicate {
  public static final RecordLoadable<BlockAtFeetEntityPredicate> LOADER = RecordLoadable.create(BlockPredicate.LOADER.directField("block_type", BlockAtFeetEntityPredicate::block), BlockAtFeetEntityPredicate::new);

  @Override
  public RecordLoadable<BlockAtFeetEntityPredicate> getLoader() {
    return LOADER;
  }

  @Override
  public boolean matches(LivingEntity entity) {
    return block.matches(entity.level().getBlockState(entity.blockPosition()));
  }
}
