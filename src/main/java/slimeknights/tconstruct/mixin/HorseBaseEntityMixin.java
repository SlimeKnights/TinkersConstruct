package slimeknights.tconstruct.mixin;

import net.minecraft.entity.passive.HorseBaseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.shared.BlockEvents;

@Mixin(HorseBaseEntity.class)
public class HorseBaseEntityMixin {
  @Inject(method = "setInAir", at = @At(value = "HEAD"))
  public void setJumping(boolean inAir, CallbackInfo ci) {
    BlockEvents.onLivingJump((HorseBaseEntity)(Object)this);
  }

}
