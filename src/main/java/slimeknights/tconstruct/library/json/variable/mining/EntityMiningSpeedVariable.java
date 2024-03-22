package slimeknights.tconstruct.library.json.variable.mining;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.variable.entity.EntityVariable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/**
 * Gets a variable from the entity in the mining speed logic
 * @param entity    Entity variable getter
 * @param fallback  Fallback if entity is null (happens when the tooltip is called serverside mainly)
 */
public record EntityMiningSpeedVariable(EntityVariable entity, float fallback) implements MiningSpeedVariable {
  public static final RecordLoadable<EntityMiningSpeedVariable> LOADER = RecordLoadable.create(
    EntityVariable.LOADER.directField("entity_type", EntityMiningSpeedVariable::entity),
    FloatLoadable.ANY.field("fallback", EntityMiningSpeedVariable::fallback),
    EntityMiningSpeedVariable::new);

  @Override
  public float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
    if (player != null) {
      return entity.getValue(player);
    }
    return fallback;
  }

  @Override
  public IGenericLoader<? extends EntityMiningSpeedVariable> getLoader() {
    return LOADER;
  }
}
