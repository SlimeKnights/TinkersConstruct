package slimeknights.tconstruct.library.modifiers.modules.armor;

import com.google.common.collect.ImmutableList;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import slimeknights.mantle.data.loadable.common.BlockStateLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.json.predicate.tool.ToolContextPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;

import static slimeknights.tconstruct.library.modifiers.ModifierEntry.VALID_LEVEL;

/**
 * Module to replace blocks with another block while walking.
 * @param replacements  List of replacements to perform
 * @param radius        Range to affect
 * @param tool          Tool condition
 */
public record ReplaceBlockWalkerModule(List<BlockReplacement> replacements, LevelingValue radius, IJsonPredicate<IToolContext> tool) implements ArmorWalkRadiusModule<Void>, ModifierModule {
  public static final RecordLoadable<ReplaceBlockWalkerModule> LOADER = RecordLoadable.create(
    BlockReplacement.LOADABLE.list().requiredField("replace", ReplaceBlockWalkerModule::replacements),
    LevelingValue.LOADABLE.requiredField("radius", ReplaceBlockWalkerModule::radius),
    ToolContextPredicate.LOADER.defaultField("tool", ReplaceBlockWalkerModule::tool),
    ReplaceBlockWalkerModule::new);

  @Override
  public float getRadius(IToolStackView tool, ModifierEntry modifier) {
    return radius.compute(modifier.getLevel() + tool.getModifierLevel(TinkerModifiers.expanded.getId()));
  }

  @Override
  public void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    if (this.tool.matches(tool)) {
      ArmorWalkRadiusModule.super.onWalk(tool, modifier, living, prevPos, newPos);
    }
  }

  @Override
  public void walkOn(IToolStackView tool, ModifierEntry entry, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable, Void context) {
    if (world.isEmptyBlock(target)) {
      mutable.set(target.getX(), target.getY() - 1, target.getZ());
      int level = entry.getLevel();
      for (BlockReplacement replacement : replacements) {
        if (replacement.level.test(level)) {
          // target handles matching any desired states like fluid level
          BlockState state = replacement.state;
          if (replacement.target.matches(world.getBlockState(mutable))
              && state.canSurvive(world, mutable) && world.isUnobstructed(state, mutable, CollisionContext.empty())
              && !ForgeEventFactory.onBlockPlace(living, BlockSnapshot.create(world.dimension(), world, mutable), Direction.UP)) {
            world.setBlockAndUpdate(mutable, state);
            world.scheduleTick(mutable, state.getBlock(), Mth.nextInt(living.getRandom(), 60, 120));

            // stop after the first successful replacement, there is no reason to do multiple consecutive
            break;
          }
        }
      }
    }
  }

  /** Represents a single replacement handled by this module */
  private record BlockReplacement(IJsonPredicate<BlockState> target, BlockState state, IntRange level) {
    public static final RecordLoadable<BlockReplacement> LOADABLE = RecordLoadable.create(
      BlockPredicate.LOADER.defaultField("target", BlockReplacement::target),
      BlockStateLoadable.DIFFERENCE.directField(BlockReplacement::state), // pulling from this object directly means the keys used are block and properties
      VALID_LEVEL.defaultField("modifier_level", BlockReplacement::level),
      BlockReplacement::new);
  }

  @Override
  public RecordLoadable<ReplaceBlockWalkerModule> getLoader() {
    return LOADER;
  }


  /* Builder */

  public static Builder builder() {
    return new Builder();
  }

  @SuppressWarnings("unused")  // API
  public static class Builder implements LevelingValue.Builder<ReplaceBlockWalkerModule> {
    private final ImmutableList.Builder<BlockReplacement> replacements = ImmutableList.builder();
    @Setter
    @Accessors(fluent = true)
    private IJsonPredicate<IToolContext> tool = ToolContextPredicate.ANY;

    private Builder() {}

    /** Replaces at the given level range */
    private Builder replaceLevelRange(IJsonPredicate<BlockState> target, BlockState replacement, IntRange modifierLevel) {
      this.replacements.add(new BlockReplacement(target, replacement, modifierLevel));
      return this;
    }

    /** Adds the given replacement only at the given level range */
    public Builder replaceLevelRange(IJsonPredicate<BlockState> target, BlockState replacement, int min, int max) {
      return replaceLevelRange(target, replacement, VALID_LEVEL.range(min, max));
    }

    /** Adds the given replacement only at the given level range */
    public Builder replaceMinLevel(IJsonPredicate<BlockState> target, BlockState replacement, int max) {
      return replaceLevelRange(target, replacement, VALID_LEVEL.max(max));
    }

    /** Adds the given replacement only at the given level range */
    public Builder replaceMaxLevel(IJsonPredicate<BlockState> target, BlockState replacement, int min) {
      return replaceLevelRange(target, replacement, VALID_LEVEL.min(min));
    }

    /** Adds the given replacement only at the given level range */
    public Builder replaceAlways(IJsonPredicate<BlockState> target, BlockState replacement) {
      return replaceLevelRange(target, replacement, VALID_LEVEL);
    }

    @Override
    public ReplaceBlockWalkerModule amount(float flat, float eachLevel) {
      List<BlockReplacement> replacements = this.replacements.build();
      if (replacements.isEmpty()) {
        throw new IllegalStateException("Must have at least 1 replacement");
      }
      return new ReplaceBlockWalkerModule(replacements, new LevelingValue(flat, eachLevel), tool);
    }
  }
}
