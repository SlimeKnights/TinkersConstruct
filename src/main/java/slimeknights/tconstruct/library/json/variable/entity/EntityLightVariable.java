package slimeknights.tconstruct.library.json.variable.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.TinkerLoadables;

import javax.annotation.Nullable;

/**
 * Gets the light level at the entity position
 * @param lightLayer  Layer to fetch. Sky will be adjusted for time of day. If null, takes the max of sky and block.
 */
public record EntityLightVariable(@Nullable LightLayer lightLayer) implements EntityVariable {
  public static final RecordLoadable<EntityLightVariable> LOADER = RecordLoadable.create(TinkerLoadables.LIGHT_LAYER.nullableField("light_layer", EntityLightVariable::lightLayer), EntityLightVariable::new);

  /** Gets the skylight level, adjust for time of day. Clientside logic is based on {@link Level#updateSkyBrightness()} */
  private static int getSkyLight(Level level, BlockPos pos) {
    int light = level.getBrightness(LightLayer.SKY, pos);
    if (level.isClientSide && light > 0) { // optimization: no need to do float math if light is already 0
      // TODO 26.1: should no longer be needed
      float rain = 1 - (level.getRainLevel(1) * 5) / 16;
      float thunder = 1 - (level.getThunderLevel(1) * 5) / 16;
      float time = 0.5f + 2 * Mth.clamp(Mth.cos(level.getTimeOfDay(1) * ((float)Math.PI * 2)), -0.25f, 0.25f);
      return light - (int)((1 - time * rain * thunder) * 11);
    } else {
      return light - level.getSkyDarken();
    }
  }

  /** Gets the light level, adjusting skylight as needed */
  public static int getLightLevel(Level level, @Nullable LightLayer lightLayer, BlockPos pos) {
    if (lightLayer == null) {
      // block light is at min 0, so we never go negative from no sky access
      return Math.max(getSkyLight(level, pos), level.getBrightness(LightLayer.BLOCK, pos));
    }
    if (lightLayer == LightLayer.SKY) {
      return Math.max(0, getSkyLight(level, pos));
    }
    return level.getBrightness(lightLayer, pos);
  }

  @Override
  public float getValue(LivingEntity entity) {
    return getLightLevel(entity.level(), lightLayer, entity.blockPosition());
  }

  @Override
  public RecordLoadable<EntityLightVariable> getLoader() {
    return LOADER;
  }
}
