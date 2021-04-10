package slimeknights.tconstruct.mixin.client;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemColors.class)
public class MixinItemColors {
  @Inject(method = "create",at = @At("TAIL"),locals = LocalCapture.CAPTURE_FAILSOFT)
  private static void create(BlockColors blockColors, CallbackInfoReturnable<ItemColors> cir) {


  }
}
