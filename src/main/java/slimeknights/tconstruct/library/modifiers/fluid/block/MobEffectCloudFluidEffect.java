package slimeknights.tconstruct.library.modifiers.fluid.block;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidMobEffect;
import slimeknights.tconstruct.library.modifiers.fluid.TimeAction;

import java.util.List;

/**
 * Effect to create a lingering cloud at the hit block
 * @see FluidMobEffect.Builder
 */
public record MobEffectCloudFluidEffect(List<FluidMobEffect> effects) implements FluidEffect<FluidEffectContext.Block> {
  private static final String FORMAT = TConstruct.makeTranslationKey("fluid_effect", "mob_effect.set");
  public static final RecordLoadable<MobEffectCloudFluidEffect> LOADER = RecordLoadable.create(
    FluidMobEffect.LOADABLE.list(1).requiredField("effects", e -> e.effects),
    MobEffectCloudFluidEffect::new);

  public MobEffectCloudFluidEffect(FluidMobEffect... effects) {
    this(List.of(effects));
  }

  @Override
  public RecordLoadable<MobEffectCloudFluidEffect> getLoader() {
    return LOADER;
  }

  /** Makes a cloud for the given context and size */
  public static AreaEffectCloud makeCloud(FluidEffectContext.Block context) {
    Vec3 location = context.getHitResult().getLocation();
    AreaEffectCloud cloud = new AreaEffectCloud(context.getLevel(), location.x(), location.y(), location.z());
    cloud.setOwner(context.getEntity());
    cloud.setRadius(1);
    cloud.setRadiusOnUse(-0.5f);
    cloud.setWaitTime(10);
    cloud.setRadiusPerTick(-cloud.getRadius() / cloud.getDuration());
    return cloud;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext.Block context, FluidAction action) {
    if (context.isOffsetReplaceable()) {
      float scale = level.value();
      if (action.execute()) {
        AreaEffectCloud cloud = makeCloud(context);
        boolean hasEffects = false;
        for (FluidMobEffect effect : effects) {
          int time = (int)(effect.time() * scale);
          if (time > 10) {
            cloud.addEffect(effect.effectWithTime(time));
            hasEffects = true;
          }
        }
        if (hasEffects) {
          context.getLevel().addFreshEntity(cloud);
        } else {
          cloud.discard();
          return 0;
        }
      }
      return scale;
    }
    return 0;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    return FluidEffect.makeTranslation(
      getLoader(),
      effects.stream()
             .map(effect -> effect.getDisplayName(TimeAction.SET))
             .reduce(MERGE_COMPONENT_LIST).orElse(Component.empty()));
  }
}
