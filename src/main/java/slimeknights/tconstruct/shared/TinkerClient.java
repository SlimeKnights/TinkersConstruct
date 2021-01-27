package slimeknights.tconstruct.shared;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.util.ResourceValidator;
import slimeknights.tconstruct.smeltery.SmelteryClientEvents;
import slimeknights.tconstruct.tables.TableClientEvents;
import slimeknights.tconstruct.world.WorldClientEvents;

/**
 * This class should only be referenced on the client side
 */
public class TinkerClient {
  /** Validates that a texture exists for models. During model type as that is when the validator is needed */
  public static final ResourceValidator textureValidator = new ResourceValidator("textures/item/tool", "textures", ".png");

  /**
   * Called by TConstruct to handle any client side logic that needs to run during the constructor
   */
  public static void onConstruct() {
    TinkerBook.initBook();

    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft != null) {
      IResourceManager manager = Minecraft.getInstance().getResourceManager();
      if (manager instanceof IReloadableResourceManager) {
        addResourceListeners((IReloadableResourceManager)manager);
      }
    }
  }

  /**
   * Adds resource listeners to the client class
   */
  private static void addResourceListeners(IReloadableResourceManager manager) {
    WorldClientEvents.addResourceListener(manager);
    TableClientEvents.addResourceListener(manager);
    SmelteryClientEvents.addResourceListener(manager);
    MaterialRenderInfoLoader.addResourceListener(manager);
    manager.addReloadListener(textureValidator);
  }
}
