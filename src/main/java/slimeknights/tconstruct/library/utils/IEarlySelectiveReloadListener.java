package slimeknights.tconstruct.library.utils;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.SelectiveReloadStateHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

/**
 * This interface is similar to {@link net.minecraftforge.resource.ISelectiveResourceReloadListener}, except it runs during {@link net.minecraft.client.resources.ReloadListener}'s prepare phase.
 * This is used mainly as models load during the prepare phase, so it ensures they are loaded soon enough.
 * {@link net.minecraftforge.resource.ISelectiveResourceReloadListener}
 */
public interface IEarlySelectiveReloadListener extends IFutureReloadListener {
  @Override
  default CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
    return CompletableFuture.runAsync(() -> {
      this.onResourceManagerReload(resourceManager, SelectiveReloadStateHandler.INSTANCE.get());
    }, backgroundExecutor).thenCompose(stage::markCompleteAwaitingOthers);
  }

  /**
   * @param resourceManager the resource manager being reloaded
   * @param resourcePredicate predicate to test whether any given resource type should be reloaded
   */
  void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate);
}
