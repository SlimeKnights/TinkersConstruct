package slimeknights.tconstruct.mixin.client;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.client.particles.AxeAttackParticle;
import slimeknights.tconstruct.tools.client.particles.HammerAttackParticle;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin {

  @Shadow
  public abstract <T extends ParticleEffect> void registerFactory(ParticleType<T> particleType, ParticleManager.SpriteAwareFactory<T> spriteAwareFactory);

  @Inject(method = "registerDefaultFactories", at = @At("TAIL"))
  private void registerTinkersFactories(CallbackInfo ci) {
    registerFactory(TinkerTools.hammerAttackParticle, HammerAttackParticle.Factory::new);
    registerFactory(TinkerTools.axeAttackParticle, AxeAttackParticle.Factory::new);
  }
}
