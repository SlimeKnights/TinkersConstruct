package slimeknights.tconstruct.library.json.variable.mining;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.variable.NestedFallbackLoader;
import slimeknights.tconstruct.library.json.variable.NestedFallbackLoader.NestedFallback;
import slimeknights.tconstruct.library.json.variable.entity.EntityVariable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/**
 * Gets a variable from the entity in the mining speed logic
 * @param entity    Entity variable getter
 * @param fallback  Fallback if entity is null (happens when the tooltip is called serverside mainly)
 */
public record EntityMiningSpeedVariable(EntityVariable entity, float fallback) implements MiningSpeedVariable, NestedFallback<EntityVariable> {
  public static final IGenericLoader<EntityMiningSpeedVariable> LOADER = new NestedFallbackLoader<>("entity_type", EntityVariable.LOADER, EntityMiningSpeedVariable::new);

  @Override
  public float getValue(IToolStackView tool, @Nullable BreakSpeed event, @Nullable Player player, @Nullable Direction sideHit) {
    if (player != null) {
      return entity.getValue(player);
    }
    return fallback;
  }

  @Override
  public EntityVariable nested() {
    return entity;
  }

  @Override
  public IGenericLoader<? extends EntityMiningSpeedVariable> getLoader() {
    return LOADER;
  }
}
