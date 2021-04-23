package slimeknights.tconstruct.mixin.client;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ModelLoader.class)
public class ModelLoaderMixin {

  @Inject(method = "getOrLoadModel", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
  private void logErrorsBecauseIntellijSucks(Identifier id, CallbackInfoReturnable<UnbakedModel> cir, UnbakedModel unbakedModel, Identifier identifier, Exception exception) {
    exception.printStackTrace();
  }
}
