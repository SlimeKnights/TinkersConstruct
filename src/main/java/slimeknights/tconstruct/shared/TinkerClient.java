package slimeknights.tconstruct.shared;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.util.ResourceValidator;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.smeltery.SmelteryClientEvents;
import slimeknights.tconstruct.tables.TableClientEvents;
import slimeknights.tconstruct.world.WorldClientEvents;

import java.util.function.Consumer;

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
    //noinspection ConstantConditions
    if (minecraft != null) {
      IResourceManager manager = Minecraft.getInstance().getResourceManager();
      if (manager instanceof IReloadableResourceManager) {
        addResourceListeners((IReloadableResourceManager)manager);
      }
    }

    // add the recipe cache invalidator to the client
    Consumer<RecipesUpdatedEvent> recipesUpdated = event -> RecipeCacheInvalidator.reload(true);
    MinecraftForge.EVENT_BUS.addListener(recipesUpdated);
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
    manager.addReloadListener(HarvestLevels.INSTANCE);
  }
}
