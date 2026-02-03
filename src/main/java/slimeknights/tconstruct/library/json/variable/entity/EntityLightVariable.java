package slimeknights.tconstruct.library.json.variable.entity;

import net.minecraft.core.BlockPos;
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

  /** Gets the skylight level, adjust for time of day */
  public static int getSkyLight(Level level, BlockPos pos) {
    return level.getBrightness(LightLayer.SKY, pos) - level.getSkyDarken();
  }

  /** Gets the light level, adjusting skylight as needed */
  public static int getLightLevel(Level level, @Nullable LightLayer lightLayer, BlockPos pos) {
    if (lightLayer == null) {
      return Math.max(getSkyLight(level, pos), level.getBrightness(LightLayer.BLOCK, pos));
    }
    if (lightLayer == LightLayer.SKY) {
      return getSkyLight(level, pos);
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
