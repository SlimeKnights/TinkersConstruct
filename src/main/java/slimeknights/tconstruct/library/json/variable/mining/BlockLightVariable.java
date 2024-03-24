package slimeknights.tconstruct.library.json.variable.mining;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

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

  @Override
  public float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
    if (player != null) {
      // use block position if possible player position otherwise
      return player.level.getBrightness(lightLayer, event != null && sideHit != null ? event.getPos().relative(sideHit) : player.blockPosition());
    }
    return fallback;
  }

  @Override
  public IGenericLoader<? extends MiningSpeedVariable> getLoader() {
    return LOADER;
  }
}
