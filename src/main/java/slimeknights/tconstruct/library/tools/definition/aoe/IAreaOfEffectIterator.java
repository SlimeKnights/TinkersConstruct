package slimeknights.tconstruct.library.tools.definition.aoe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestLogic;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collections;
import java.util.function.Predicate;

/** Logic for iterating over a set of blocks */
public interface IAreaOfEffectIterator extends IHaveLoader<IAreaOfEffectIterator> {
  /** Default iterator, no blocks */
  IAreaOfEffectIterator DEFAULT = new IAreaOfEffectIterator() {
    @Override
    public Iterable<BlockPos> getBlocks(IToolStackView tool, ItemStack stack, Player player, BlockState state, Level world, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
      return Collections.emptyList();
    }

    @Override
    public IGenericLoader<? extends IAreaOfEffectIterator> getLoader() {
      throw new UnsupportedOperationException("Attempt to serialize empty AOE iterator");
    }
  };

  /** Registry of all AOE loaders */
  GenericLoaderRegistry<IAreaOfEffectIterator> LOADER = new GenericLoaderRegistry<>(DEFAULT);

  /**
   * Gets a list of blocks that the tool can affect.
   *
   * @param tool        tool stack
   * @param stack       item stack for vanilla methods
   * @param world       the current world
   * @param player      the player using the tool
   * @param origin      the origin block spot to start from
   * @param sideHit     side of the block that was hit
   * @param matchType   Type of match
   * @return A list of BlockPos's that the AOE tool can affect. Note these positions will likely be mutable
   */
  Iterable<BlockPos> getBlocks(IToolStackView tool, ItemStack stack, Player player, BlockState state, Level world, BlockPos origin, Direction sideHit, AOEMatchType matchType);

  /**
   * Gets the predicate for whether a given position can be broken in AOE
   * @param tool       Tool used
   * @param stack      Item stack, for vanilla hooks
   * @param world      Level instance
   * @param origin     Center position
   * @param matchType  Match logic
   * @return  Predicate for AOE block matching
   */
  static Predicate<BlockPos> defaultBlockPredicate(IToolStackView tool, ItemStack stack, Level world, BlockPos origin, AOEMatchType matchType) {
    // requires effectiveness
    if (matchType == AOEMatchType.BREAKING) {
      // don't let hardness vary too much
      float refHardness = world.getBlockState(origin).getDestroySpeed(world, origin);
      return pos -> {
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
          return ToolHarvestLogic.isEffective(tool, state);
        }
        return false;
      };
    } else {
      return pos -> !world.isEmptyBlock(pos);
    }
  }

  /** Match types for the AOE getter */
  enum AOEMatchType {
    /** Used when the block is being broken, typically matches only harvestable blocks
     * When using this type, the iteratable should be fetched before breaking the block */
    BREAKING,
    /** Used for right click interactions such as hoeing, typically matches any block (will filter later) */
    TRANSFORM
  }
}
