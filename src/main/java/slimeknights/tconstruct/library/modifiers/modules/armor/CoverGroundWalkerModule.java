package slimeknights.tconstruct.library.modifiers.modules.armor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.common.BlockStateLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModuleBuilder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

/**
 * Module to cover the ground in the given block
 * @param state      State used to cover the ground
 * @param radius     Radius to cover
 * @param condition  Standard module condition
 */
public record CoverGroundWalkerModule(BlockState state, LevelingValue radius, ModifierCondition<IToolStackView> condition) implements ModifierModule, ArmorWalkRadiusModule<Void>, ConditionalModule<IToolStackView> {
  public static final RecordLoadable<CoverGroundWalkerModule> LOADER = RecordLoadable.create(
    BlockStateLoadable.DIFFERENCE.directField(CoverGroundWalkerModule::state),
    LevelingValue.LOADABLE.requiredField("radius", CoverGroundWalkerModule::radius),
    ModifierCondition.TOOL_FIELD,
    CoverGroundWalkerModule::new);

  /** @apiNote Internal constructor, use builder via {@link #block(Block)}. */
  @Internal
  public CoverGroundWalkerModule {}

  @Override
  public float getRadius(IToolStackView tool, ModifierEntry modifier) {
    return radius.compute(modifier.getLevel() + tool.getModifierLevel(TinkerModifiers.expanded.getId()));
  }

  @Override
  public void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    if (condition.matches(tool, modifier)) {
      ArmorWalkRadiusModule.super.onWalk(tool, modifier, living, prevPos, newPos);
    }
  }

  @Override
  public boolean walkOn(IToolStackView tool, ModifierEntry entry, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable, Void context) {
    if (world.isEmptyBlock(target) && state.canSurvive(world, target)) {
      world.setBlockAndUpdate(target, state);
    }
    return false;
  }

  @Override
  public RecordLoadable<CoverGroundWalkerModule> getLoader() {
    return LOADER;
  }


  /* Builder */

  /** Creates a builder instance for the given state */
  public static Builder state(BlockState state) {
    return new Builder(state);
  }

  /** Creates a builder instance for the given block */
  public static Builder block(Block block) {
    return state(block.defaultBlockState());
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModuleBuilder.Stack<Builder> implements LevelingValue.Builder<CoverGroundWalkerModule> {
    private final BlockState state;

    @Override
    public CoverGroundWalkerModule amount(float flat, float eachLevel) {
      return new CoverGroundWalkerModule(state, new LevelingValue(flat, eachLevel), condition);
    }
  }
}
