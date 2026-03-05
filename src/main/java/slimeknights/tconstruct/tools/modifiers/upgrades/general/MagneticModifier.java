package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.json.RandomLevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.combat.MobEffectModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.tools.modifiers.effect.MagneticEffect;

/** @deprecated use {@link MagneticEffect} or {@link MobEffectModule.ToolUsage} */
@Deprecated(forRemoval = true)
public class MagneticModifier extends Modifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(MobEffectModule.builder(TinkerEffects.magnetic).time(RandomLevelingValue.flat(40)).buildToolUsage());
    hookBuilder.addModule(new ArmorLevelModule(TinkerDataKeys.MAGNET, false, null));
  }

  /** @deprecated use {@link MagneticEffect#applyVelocity(LivingEntity, int, Class, int, float, int)} */
  @Deprecated(forRemoval = true)
  public static <T extends Entity> void applyVelocity(LivingEntity entity, int amplifier, Class<T> targetClass, int minRange, float speed, int maxPush) {
    MagneticEffect.applyVelocity(entity, amplifier, targetClass, minRange, speed, maxPush);
  }

  /** @deprecated use {@link MagneticEffect#applyVelocity(Level, Vec3, int, Class, int, float, int)} */
  @Deprecated(forRemoval = true)
  public static <T extends Entity> void applyVelocity(Level level, Vec3 origin, int amplifier, Class<T> targetClass, int minRange, float speed, int maxPush) {
    MagneticEffect.applyVelocity(level, origin, amplifier, targetClass, minRange, speed, maxPush);
  }

  /** @deprecated use {@link MagneticEffect#applyMagnet(LivingEntity, int)} */
  @Deprecated(forRemoval = true)
  public static void applyMagnet(LivingEntity entity, int amplifier) {
    MagneticEffect.applyMagnet(entity, amplifier);
  }
}
