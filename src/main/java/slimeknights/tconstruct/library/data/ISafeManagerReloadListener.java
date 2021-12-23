package slimeknights.tconstruct.library.data;

import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.fml.ModLoader;

/**
 * Same as {@link IResourceManagerReloadListener}, but only runs if the mod loader state is valid
 */
@SuppressWarnings("deprecation")
public interface ISafeManagerReloadListener extends IResourceManagerReloadListener {
  @Override
  default void onResourceManagerReload(IResourceManager resourceManager) {
    if (ModLoader.isLoadingStateValid()) {
      onReloadSafe(resourceManager);
    }
  }

  /**
   * Safely handle a resource manager reload. Only runs if the mod loading state is valid
   * @param resourceManager  Resource manager
   */
  void onReloadSafe(IResourceManager resourceManager);
}
