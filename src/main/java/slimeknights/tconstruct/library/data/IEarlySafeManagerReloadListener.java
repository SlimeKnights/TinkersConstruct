package slimeknights.tconstruct.library.data;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.fml.ModLoader;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Same as {@link ISafeManagerReloadListener}, but reloads earlier. Needed to work with some parts of models
 */
public interface IEarlySafeManagerReloadListener extends IFutureReloadListener {
  @Override
  default CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
    return CompletableFuture.runAsync(() -> {
      if (ModLoader.isLoadingStateValid()) {
        onReloadSafe(resourceManager);
      }
    }, backgroundExecutor).thenCompose(stage::markCompleteAwaitingOthers);
  }

  /**
   * Safely handle a resource manager reload. Only runs if the mod loading state is valid
   * @param resourceManager  Resource manager
   */
  void onReloadSafe(IResourceManager resourceManager);
}
