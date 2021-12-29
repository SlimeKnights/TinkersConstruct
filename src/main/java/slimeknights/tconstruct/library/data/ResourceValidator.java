package slimeknights.tconstruct.library.data;

import com.google.common.collect.ImmutableSet;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility that handles checking if a resource exists in any resource pack
 */
public class ResourceValidator implements IEarlySafeManagerReloadListener, Predicate<ResourceLocation> {
  private final String folder;
  private final int trim;
  private final String extension;
  protected Set<ResourceLocation> resources;

  /**
   * Gets a resource validator instance
   * @param folder     Folder to search
   * @param trim       Text to trim off resource locations
   * @param extension  File extension
   */
  public ResourceValidator(String folder, String trim, String extension) {
    this.folder = folder;
    this.trim = trim.length() + 1;
    this.extension = extension;
    this.resources = ImmutableSet.of();
  }

  @Override
  public void onReloadSafe(ResourceManager manager) {
    int extensionLength = extension.length();
    // FIXME: this does not validate folder names
    this.resources = manager.listResources(folder, (loc) -> {
      // must have proper extension and contain valid characters
      return loc.endsWith(extension) && isPathValid(loc);
    }).stream().map((location) -> {
      String path = location.getPath();
      return new ResourceLocation(location.getNamespace(), path.substring(trim, path.length() - extensionLength));
    }).collect(Collectors.toSet());
  }

  private static boolean isPathValid(String path) {
      return path.chars().allMatch((c) -> {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == '/' || c == '.' || c == '-';
      });
  }

  @Override
  public boolean test(ResourceLocation location) {
    return resources.contains(location);
  }

  /**
   * Clears the resource cache, saves RAM as there could be a lot of locations
   */
  public void clear() {
    resources = ImmutableSet.of();
  }
}
