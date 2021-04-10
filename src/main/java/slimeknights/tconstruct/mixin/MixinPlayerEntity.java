package slimeknights.tconstruct.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.library.SlimeBounceHandler;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {
  @Inject(method = "tick", at = @At("TAIL"))
  public void tick(CallbackInfo ci) {
    for(SlimeBounceHandler handler : SlimeBounceHandler.getBouncingEntities().values()) {
      handler.playerTickPost((PlayerEntity) (Object) this);
    }
  }

}
