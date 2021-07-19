package slimeknights.tconstruct.mixin;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import slimeknights.tconstruct.world.WorldExtension;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(World.class)
public abstract class MixinWorld implements WorldExtension {
  @Shadow
  @Final
  public boolean isClient;

  @Shadow
  @Final
  public Thread thread;

  @Shadow
  @Final
  protected static Logger LOGGER;

  @Unique
  private List<Runnable> runnableList = new ArrayList<>();

  @Override
  public void queuePacket(Runnable runnable) {
    runnableList.add(runnable);
  }

  @Inject(method = "tickBlockEntities",at = @At("HEAD"))
  public void networkTick(CallbackInfo ci) {
    for(Runnable runnable : runnableList) {
      CompletableFuture.supplyAsync(() -> {
        runnable.run();
        return null;
      });
    }
  }
}
