package slimeknights.tconstruct.library.client.model;

import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.mantle.data.IEarlySafeManagerReloadListener;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Logic to handle dynamic texture scans. Instead of calling {@link ResourceManager#hasResource(ResourceLocation)} per texture, we take advantage of the fact
 * our models are always looking for many textures in a single folder and do a per folder call to {@link ResourceManager#listResources(String, Predicate)}
 */
@Log4j2
public class DynamicTextureLoader {
  /** Start of the path to trim when caching existing textures */
  private static final int TRIM_START = "textures/".length();
  /** End of the path to trim when caching existing textures */
  private static final int TRIM_END = ".png".length();

  /** If textures are placed here, only need a single scan instead of one per tool */
  private static final String PREFERRED_FOLDER = "item/tool";

  /** Set of all folders that have been scanned, so we can avoid scanning them twice */
  private static final Set<String> SCANNED_FOLDERS = new HashSet<>();
  /** Map of discovered textures */
  private static final Set<ResourceLocation> EXISTING_TEXTURES = new HashSet<>();
  /** Set of all textures that are missing from the resource pack, to avoid logging twice */
  private static final Set<ResourceLocation> SKIPPED_TEXTURES = new HashSet<>();
  /** Reload listener to clear caches */
  private static final IEarlySafeManagerReloadListener RELOAD_LISTENER = manager -> {
    clearCache();
  };

  /** Clears all cached texture names */
  public static void clearCache() {
    SCANNED_FOLDERS.clear();
    EXISTING_TEXTURES.clear();
    SKIPPED_TEXTURES.clear();
  }

  /** Registers this manager */
  public static void init(RegisterClientReloadListenersEvent event) {
    event.registerReloadListener(RELOAD_LISTENER);
    // clear cache on texture stitch, no longer need it then as its too late to lookup textures
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TextureStitchEvent.Pre.class, e -> clearCache());
  }

  /** Scans the given folder to add all textures */
  private static void scanFolder(ResourceManager manager, String folder) {
    manager.listResources("textures/" + folder, name -> name.endsWith(".png")).stream()
           .map(loc -> {
             String path = loc.getPath();
             return new ResourceLocation(loc.getNamespace(), path.substring(TRIM_START, path.length() - TRIM_END));
           })
           .forEach(EXISTING_TEXTURES::add);
  }

  /** Checks if the given folder is not yet scanned */
  private static boolean checkFolderNotScanned(ResourceManager manager, String originalFolder) {
    // if we already checked the folder, no work to do
    if (SCANNED_FOLDERS.contains(originalFolder)) {
      return false;
    }

    // if the folder we are looking for starts with the preferred folder, we can immediately resolve it by resolving the whole preferred folder
    if (originalFolder.startsWith(PREFERRED_FOLDER)) {
      SCANNED_FOLDERS.add(originalFolder);
      if (!SCANNED_FOLDERS.contains(PREFERRED_FOLDER)) {
        SCANNED_FOLDERS.add(PREFERRED_FOLDER);
        scanFolder(manager, PREFERRED_FOLDER);
      }
      return false;
    }

    // if a folder has not been scanned yet, check if any of its parent's have been scanned
    // list resources will fetch all sub folders, so this saves us calling it multiple times per tool
    String folder = originalFolder;
    int lastPos = folder.lastIndexOf('/');
    while (lastPos != -1) {
      folder = folder.substring(0, lastPos);
      if (SCANNED_FOLDERS.contains(folder)) {
        // if we scanned a parent, no work to do, but mark ourself as scanned, may find more textures here later
        SCANNED_FOLDERS.add(originalFolder);
        return false;
      }
      lastPos = folder.lastIndexOf('/');
    }
    // mark this folder as searched for next time, return true to fetch texture names
    SCANNED_FOLDERS.add(originalFolder);
    return true;
  }

  /** Checks if a texture exists */
  public static boolean textureExists(ResourceManager manager, String folder, ResourceLocation location) {
    if (checkFolderNotScanned(manager, folder)) {
      scanFolder(manager, folder);
    }
    return EXISTING_TEXTURES.contains(location);
  }

  /** Logs that a dynamic texture is missing, config option to disable */
  public static void logMissingTexture(ResourceLocation location) {
    if (!SKIPPED_TEXTURES.contains(location)) {
      SKIPPED_TEXTURES.add(location);
      log.debug("Skipping loading texture '{}' as it does not exist in the resource pack", location);
    }
  }

  /**
   * Gets a consumer to add textures to the given collection
   * @param allTextures         Collection of textures
   * @param folder              Folder the texture is expected to reside in. Will give inconsistent behavior if the location is not a member of the folder, this is not validated
   * @param logMissingTextures  If true, log textures that were not found
   * @return  Texture consumer
   */
  public static Predicate<Material> getTextureAdder(String folder, Collection<Material> allTextures, boolean logMissingTextures) {
    ResourceManager manager = Minecraft.getInstance().getResourceManager();
    return mat -> {
      // either must be non-blocks, or must exist. We have fallbacks if it does not exist
      ResourceLocation loc = mat.texture();
      if (!InventoryMenu.BLOCK_ATLAS.equals(mat.atlasLocation()) || textureExists(manager, folder, loc)) {
        allTextures.add(mat);
        return true;
      }
      if (logMissingTextures) {
        logMissingTexture(loc);
      }
      return false;
    };
  }

  /** Gets the folder containing the given texture */
  public static String getTextureFolder(ResourceLocation location) {
    String path = location.getPath();
    int index = path.lastIndexOf('/');
    if (index == -1) {
      return path;
    }
    return path.substring(0, index);
  }
}
