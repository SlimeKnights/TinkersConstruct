package slimeknights.tconstruct.library.client;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.profiler.Profiler;
import net.minecraftforge.fml.ModLoader;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Same as {@link ISafeManagerReloadListener}, but reloads earlier. Needed to work with some parts of models
 */
public interface IEarlySafeManagerReloadListener extends ResourceReloadListener {
  @Override
  default CompletableFuture<Void> reload(Synchronizer stage, ResourceManager resourceManager, Profiler preparationsProfiler, Profiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
    return CompletableFuture.runAsync(() -> {
      if (ModLoader.isLoadingStateValid()) {
        onReloadSafe(resourceManager);
      }
    }, backgroundExecutor).thenCompose(stage::whenPrepared);
  }

  /**
   * Safely handle a resource manager reload. Only runs if the mod loading state is valid
   * @param resourceManager  Resource manager
   */
  void onReloadSafe(ResourceManager resourceManager);
}
