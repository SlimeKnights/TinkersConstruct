package slimeknights.tconstruct.library.json.variable.entity;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.loader.RegistryEntryLoader;

/** Gets the level of the mob effect on an entity */
public record EntityEffectLevelVariable(MobEffect effect) implements EntityVariable {
  public static final IGenericLoader<EntityEffectLevelVariable> LOADER = new RegistryEntryLoader<>("effect", ForgeRegistries.MOB_EFFECTS, EntityEffectLevelVariable::new, EntityEffectLevelVariable::effect);

  @Override
  public float getValue(LivingEntity entity) {
    MobEffectInstance instance = entity.getEffect(effect);
    if (instance != null) {
      return instance.getAmplifier() + 1;
    }
    return 0;
  }

  @Override
  public IGenericLoader<? extends EntityVariable> getLoader() {
    return LOADER;
  }
}
