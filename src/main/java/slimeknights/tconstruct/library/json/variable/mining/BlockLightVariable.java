package slimeknights.tconstruct.library.json.variable.mining;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Gets the targeted block light level. Will use the targeted position if possible, otherwise the players position
 * @param lightLayer   Block light layer to use
 * @param fallback     Fallback value if missing event and player
 */
public record BlockLightVariable(LightLayer lightLayer, float fallback) implements MiningSpeedVariable {
  public static final RecordLoadable<BlockLightVariable> LOADER = RecordLoadable.create(
    TinkerLoadables.LIGHT_LAYER.requiredField("light_layer", BlockLightVariable::lightLayer),
    FloatLoadable.ANY.requiredField("fallback", BlockLightVariable::fallback),
    BlockLightVariable::new);

  /** Gets the block position relative to the arguments */
  private BlockPos getPos(@Nullable BreakSpeed event, Player player, @Nullable Direction sideHit) {
    // use block position if possible player position otherwise
    if (event != null && sideHit != null) {
      Optional<BlockPos> eventPos = event.getPosition();
      if (eventPos.isPresent()) {
        return eventPos.get().relative(sideHit);
      }
    }
    return player.blockPosition();
  }

  @Override
  public float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
    if (player != null) {
      return player.level.getBrightness(lightLayer, getPos(event, player, sideHit));
    }
    return fallback;
  }

  @Override
  public RecordLoadable<BlockLightVariable> getLoader() {
    return LOADER;
  }
}
