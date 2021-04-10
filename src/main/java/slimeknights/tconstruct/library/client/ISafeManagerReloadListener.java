package slimeknights.tconstruct.library.client;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;

/**
 * Same as {@link SynchronousResourceReloadListener}, but only runs if the mod loader state is valid
 */
public interface ISafeManagerReloadListener extends SynchronousResourceReloadListener {
  @Override
  default void apply(ResourceManager resourceManager) {
    onReloadSafe(resourceManager);
  }

  /**
   * Safely handle a resource manager reload. Only runs if the mod loading state is valid
   * @param resourceManager  Resource manager
   */
  void onReloadSafe(ResourceManager resourceManager);
}
