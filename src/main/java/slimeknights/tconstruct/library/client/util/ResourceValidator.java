package slimeknights.tconstruct.library.client.util;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.resource.IResourceType;
import slimeknights.tconstruct.library.utils.IEarlySelectiveReloadListener;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility that handles checking if a resource exists in any resource pack
 */
public class ResourceValidator implements IEarlySelectiveReloadListener, Predicate<ResourceLocation> {
  private final IResourceType type;
  private final String folder;
  private final int trim;
  private final String extension;
  private Set<ResourceLocation> resources;

  /**
   * Gets a resource validator instance
   * @param type
   * @param folder
   * @param extension
   */
  public ResourceValidator(IResourceType type, String folder, String trim, String extension) {
    this.type = type;
    this.folder = folder;
    this.trim = trim.length() + 1;
    this.extension = extension;
    this.resources = ImmutableSet.of();
  }

  @Override
  public void onResourceManagerReload(IResourceManager manager, Predicate<IResourceType> predicate) {
    if (predicate.test(type)) {
      int extensionLength = extension.length();
      // FIXME: this does not validate folder names
      this.resources = manager.getAllResourceLocations(folder, (loc) -> {
        // must have proper extension and contain valid characters
        return loc.endsWith(extension) && isPathValid(loc);
      }).stream().map((location) -> {
        String path = location.getPath();
        return new ResourceLocation(location.getNamespace(), path.substring(trim, path.length() - extensionLength));
      }).collect(Collectors.toSet());
    }
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
