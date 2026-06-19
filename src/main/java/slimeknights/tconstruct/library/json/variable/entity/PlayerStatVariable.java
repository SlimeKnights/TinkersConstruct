package slimeknights.tconstruct.library.json.variable.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.variable.StatLoadable;

/** Variable type for fetching a stat from a player. Note this only provides stat values on the server as the client lacks accurate access. */
public record PlayerStatVariable(Stat<?> stat, float fallback) implements EntityVariable {
  public static final RecordLoadable<PlayerStatVariable> LOADER = RecordLoadable.create(
    StatLoadable.INSTANCE.requiredField("stat", PlayerStatVariable::stat),
    FloatLoadable.ANY.defaultField("fallback", 0f, PlayerStatVariable::fallback),
    PlayerStatVariable::new);

  public PlayerStatVariable(ResourceLocation stat, float fallback) {
    this(Stats.CUSTOM.get(stat), fallback);
  }

  @Override
  public float getValue(LivingEntity entity) {
    // no attempt is made to fetch the stat client-side as they do not sync
    // no warning however as we need it for the tooltip, use the fallback for a reasonable tooltip value (or 0 to hide it)
    if (!entity.level().isClientSide && entity instanceof ServerPlayer player) {
      return player.getStats().getValue(stat);
    }
    return fallback;
  }

  @Override
  public RecordLoadable<? extends EntityVariable> getLoader() {
    return LOADER;
  }
}
