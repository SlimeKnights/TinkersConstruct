package slimeknights.tconstruct.library.data;

import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.ModLoader;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;

/**
 * Same as {@link ISafeManagerReloadListener}, but reloads earlier. Needed to work with some parts of models
 */
public interface IEarlySafeManagerReloadListener extends PreparableReloadListener {
  @Override
  default CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
    return CompletableFuture.runAsync(() -> {
      if (ModLoader.isLoadingStateValid()) {
        onReloadSafe(resourceManager);
      }
    }, backgroundExecutor).thenCompose(stage::wait);
  }

  /**
   * Safely handle a resource manager reload. Only runs if the mod loading state is valid
   * @param resourceManager  Resource manager
   */
  void onReloadSafe(ResourceManager resourceManager);
}
