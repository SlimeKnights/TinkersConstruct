package slimeknights.tconstruct.library.json.variable.mining;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.variable.NestedFallbackLoader;
import slimeknights.tconstruct.library.json.variable.NestedFallbackLoader.NestedFallback;
import slimeknights.tconstruct.library.json.variable.block.BlockVariable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/**
 * Uses a {@link BlockVariable} to fetch a value from the break speed event
 * @param block     Block variable logic
 * @param fallback  Fallback value if the event is null
 */
public record BlockMiningSpeedVariable(BlockVariable block, float fallback) implements MiningSpeedVariable, NestedFallback<BlockVariable> {
  public static final IGenericLoader<BlockMiningSpeedVariable> LOADER = new NestedFallbackLoader<>("block_type", BlockVariable.LOADER, BlockMiningSpeedVariable::new);

  @Override
  public float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
    if (event != null) {
      return block.getValue(event.getState());
    }
    return fallback;
  }

  @Override
  public BlockVariable nested() {
    return block;
  }

  @Override
  public IGenericLoader<? extends MiningSpeedVariable> getLoader() {
    return LOADER;
  }
}
