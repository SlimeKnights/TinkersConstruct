package slimeknights.tconstruct.library.json.variable.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.loader.EnumLoader;

/** Gets the light level at the entity position */
public record EntityLightVariable(LightLayer lightLayer) implements EntityVariable {
  public static final IGenericLoader<EntityLightVariable> LOADER = new EnumLoader<>("light_layer", LightLayer.class, EntityLightVariable::new, EntityLightVariable::lightLayer);

  @Override
  public float getValue(LivingEntity entity) {
    return entity.level.getBrightness(lightLayer, entity.blockPosition());
  }

  @Override
  public IGenericLoader<? extends EntityVariable> getLoader() {
    return LOADER;
  }
}
