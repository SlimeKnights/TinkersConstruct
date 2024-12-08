package slimeknights.tconstruct.common.recipe;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import slimeknights.mantle.data.listener.IEarlySafeManagerReloadListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles notifying recipe caches that they need to invalidate
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecipeCacheInvalidator implements IEarlySafeManagerReloadListener {
  private static final RecipeCacheInvalidator INSTANCE = new RecipeCacheInvalidator();
  private static final List<BooleanConsumer> listeners = new ArrayList<>();

  /**
   * Adds a new listener that runs every time the recipes are reloaded
   * @param runnable  Runnable accepting a boolean representing if this is client side
   */
  public static void addReloadListener(BooleanConsumer runnable) {
    listeners.add(runnable);
  }

  /**
   * Registers a listener that properly responds to the client side
   * @param runnable  Runnable to clear cache
   * @return  Object that can clear cache as needed
   */
  public static DuelSidedListener addDuelSidedListener(Runnable runnable) {
    DuelSidedListener listener = new DuelSidedListener(runnable);
    addReloadListener(listener);
    return listener;
  }

  /**
   * Reloads all listeners, used client side
   */
  public static void reload(boolean client) {
    for (BooleanConsumer runnable : listeners) {
      runnable.accept(client);
    }
  }

  @Override
  public void onReloadSafe(ResourceManager resourceManager) {
    reload(false);
  }

  /**
   * Called when resource managers reload
   * @param event  Reload event
   */
  public static void onReloadListenerReload(AddReloadListenerEvent event) {
    event.addListener(INSTANCE);
  }

  /** Logic to respond properly to late running of the client */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class DuelSidedListener implements BooleanConsumer {
    private final Runnable clearCache;
    private boolean clearQueued = false;

    @Override
    public void accept(boolean client) {
      // client side event runs at the end of recipe loading
      // server side runs at the start
      // so queue client side to run at the beginning of the next recipe list
      if (client) {
        clearQueued = true;
      } else {
        clearCache();
      }
    }

    /**
     * Clears the cache based on the runnable
     */
    public void clearCache() {
      clearQueued = false;
      clearCache.run();
    }

    /**
     * Clears the cache if a clear is queued. Intended to be called during add
     */
    public void checkClear() {
      if (clearQueued) {
        clearCache();
      }
    }
  }
}
