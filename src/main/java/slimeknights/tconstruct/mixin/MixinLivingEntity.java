package slimeknights.tconstruct.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.shared.BlockEvents;

import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
  @Inject(method = "jump",at = @At("TAIL"))
  public void jump(CallbackInfo ci) {
    BlockEvents.onLivingJump((LivingEntity)(Object)this);
  }

}
