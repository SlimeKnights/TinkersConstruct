package slimeknights.tconstruct.shared;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.resource.VanillaResourceType;
import slimeknights.tconstruct.library.book.TinkerBook;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.renderer.font.CustomFontRenderer;
import slimeknights.tconstruct.library.client.util.ResourceValidator;
import slimeknights.tconstruct.world.WorldClientEvents;

/**
 * This class should only be referenced on the client side
 */
public class TinkerClient {
  /** Validates that a texture exists for models. During model type as that is when the validator is needed */
  public static final ResourceValidator textureValidator = new ResourceValidator(VanillaResourceType.MODELS, "textures/item/tool", "textures", ".png");

  public static CustomFontRenderer fontRenderer;

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
    MaterialRenderInfoLoader.addResourceListener(manager);
    manager.addReloadListener(textureValidator);
  }

  public static void onCommonSetup() {
    TinkerClient.fontRenderer = new CustomFontRenderer(Minecraft.getInstance().fontRenderer);
    TinkerClient.fontRenderer.setBidiFlag(Minecraft.getInstance().getLanguageManager().isCurrentLanguageBidirectional());

    Minecraft minecraft = Minecraft.getInstance();
    if (minecraft != null) {
      IResourceManager manager = Minecraft.getInstance().getResourceManager();
      if (manager instanceof IReloadableResourceManager) {
        ((IReloadableResourceManager) manager).addReloadListener(fontRenderer);
      }
    }
  }
}
