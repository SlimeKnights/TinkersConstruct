package slimeknights.tconstruct.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import slimeknights.tconstruct.library.client.IEarlySafeManagerReloadListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles notifying recipe caches that they need to invalidate
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeCacheInvalidator implements IEarlySafeManagerReloadListener {
  private static final RecipeCacheInvalidator INSTANCE = new RecipeCacheInvalidator();
  private static final List<Runnable> listeners = new ArrayList<>();

  /**
   * Adds a new listener that runs every time the recipes are reloaded
   * @param runnable  Runnable
   */
  public static void addReloadListener(Runnable runnable) {
    listeners.add(runnable);
  }

  @Override
  public void onReloadSafe(IResourceManager resourceManager) {
    for (Runnable runnable : listeners) {
      runnable.run();
    }
  }

  /**
   * Called when resource managers reload
   * @param event  Reload event
   */
  public static void onReloadListenerReload(AddReloadListenerEvent event) {
    event.addListener(INSTANCE);
  }
}
