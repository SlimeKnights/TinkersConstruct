package slimeknights.tconstruct.library.tools.definition.module.aoe;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.data.registry.DefaultingLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.function.Predicate;

/** Logic for iterating over a set of blocks */
public interface AreaOfEffectIterator {
  /** Interface for loadable area of effect iterators, used for the fallback AOE iterator */
  interface Loadable extends AreaOfEffectIterator, ToolModule {
    List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<Loadable>defaultHooks(ToolHooks.AOE_ITERATOR);

    @Override
    RecordLoadable<? extends Loadable> getLoader();

    @Override
    default List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }
  }

  /** Empty instance for {@link ConditionalAOEIterator} nesting con */
  Loadable EMPTY = SingletonLoader.singleton(loader -> new Loadable() {
    @Override
    public RecordLoadable<? extends Loadable> getLoader() {
      return loader;
    }

    @Override
    public Iterable<BlockPos> getBlocks(IToolStackView tool, UseOnContext context, BlockState state, AOEMatchType matchType) {
      return List.of();
    }
  });

  /** Registry of all AOE loaders. TODO 1.21: change field type to {@link DefaultingLoaderRegistry} */
  GenericLoaderRegistry<Loadable> LOADER = new DefaultingLoaderRegistry<>("AOE Iterator", EMPTY, false);

  /** Registers a loader with both tool modules and area of effect (latter used for fallback loader) */
  static void register(ResourceLocation name, RecordLoadable<? extends Loadable> loader) {
    ToolModule.LOADER.register(name, loader);
    LOADER.register(name, loader);
  }

  /**
   * Gets a list of blocks that the tool can affect.
   *
   * @param tool        tool stack
   * @param context     Context for the original target. Note the hit location is unavailable during block breaking.
   * @param matchType   Type of match
   * @return A list of BlockPos's that the AOE tool can affect. Note these positions will likely be mutable
   */
  Iterable<BlockPos> getBlocks(IToolStackView tool, UseOnContext context, BlockState state, AOEMatchType matchType);

  /** Checks if the tool is effective on the given block */
  private static boolean isEffective(IToolStackView tool, Level world, BlockPos pos, float refHardness) {
    BlockState state = world.getBlockState(pos);
    if (state.isAir()) {
      return false;
    }
    // if the hardness varies by too much, don't allow breaking
    float hardness = state.getDestroySpeed(world, pos);
    if (hardness == -1) {
      return false;
    }
    if (refHardness == 0 ? hardness == 0 : hardness / refHardness <= 3) {
      // must not be broken, and the tool definition must be effective
      return IsEffectiveToolHook.isEffective(tool, state);
    }
    return false;
  }

  /** Gets the predicate for whether a given position can be broken in AOE */
  static Predicate<BlockPos> defaultBlockPredicate(IToolStackView tool, UseOnContext context, AOEMatchType matchType) {
    // requires effectiveness
    Level world = context.getLevel();
    if (matchType == AOEMatchType.TRANSFORM) {
      return pos -> !world.isEmptyBlock(pos);
    } else {
      // don't let hardness vary too much
      BlockPos origin = context.getClickedPos();
      float refHardness = world.getBlockState(origin).getDestroySpeed(world, origin);
      if (matchType == AOEMatchType.DISPLAY) {
        return pos -> {
          Level level = context.getLevel();
          if (isEffective(tool, level, pos, refHardness)) {
            return true;
          }
          BlockState offsetState = level.getBlockState(pos);
          for (ModifierEntry entry : tool.getModifiers()) {
            if (entry.getHook(ModifierHooks.AOE_HIGHLIGHT).shouldHighlight(tool, entry, context, pos, offsetState)) {
              return true;
            }
          }
          return false;
        };
      }
      return pos -> isEffective(tool, world, pos, refHardness);
    }
  }

  /** Match types for the AOE getter */
  enum AOEMatchType {
    /** Used when the block is being broken, typically matches only harvestable blocks
     * When using this type, the iteratable should be fetched before breaking the block */
    BREAKING,
    /** Used for right click interactions such as hoeing, typically matches any block (will filter later) */
    TRANSFORM,
    /** Used for wireframe display in world */
    DISPLAY
  }
}
