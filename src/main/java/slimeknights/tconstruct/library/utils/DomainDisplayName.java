package slimeknights.tconstruct.library.utils;

import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.text.WordUtils;
import slimeknights.mantle.data.listener.ISafeManagerReloadListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Logic to get the display name for a resource domain
 */
public class DomainDisplayName {
  private DomainDisplayName() {}

  /** Map of domain name to display name */
  private static final Map<String,String> DISPLAY_NAME_LOOKUP = new HashMap<>();
  /** Cached pattern for matching a dash or underscore */
  private static final Pattern DASH_UNDERSCORE = Pattern.compile("[_-]");
  /** Reload listener to clear names on resource pack reload */
  private static final ISafeManagerReloadListener RELOAD_LISTENER = manager -> DISPLAY_NAME_LOOKUP.clear();

  /**
   * Formats a domain name into title case. For example, "my_pack" becomes "My Pack"
   * @param domain  Domain name to format
   * @return  Formatted domain name
   */
  private static String formatDomainName(String domain) {
    return WordUtils.capitalize(DASH_UNDERSCORE.matcher(domain).replaceAll(" "));
  }

  /** Gets the name for a mod ID, uncached */
  private static String nameForUncached(String domain) {
    // first, check if the resource pack translated the thing
    String langKey = "domain." + domain + ".display_name";
    String translated = ForgeI18n.getPattern(langKey);
    if (!translated.equals(langKey)) {
      return translated;
    }

    // that failed? try a mod container lookup
    return ModList.get().getModContainerById(domain)
                  .map(container -> container.getModInfo().getDisplayName())
                  .orElseGet(() -> formatDomainName(domain));
  }

  /**
   * Gets the name for a resource domain
   * @param domain  Resource domain
   * @return Display name
   */
  public static String nameFor(String domain) {
    return DISPLAY_NAME_LOOKUP.computeIfAbsent(domain, DomainDisplayName::nameForUncached);
  }

  /** Registers the reload listener with the resource manager */
  public static void addResourceListener(RegisterClientReloadListenersEvent manager) {
    manager.registerReloadListener(RELOAD_LISTENER);
  }
}
