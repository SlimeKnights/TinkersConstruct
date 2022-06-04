package slimeknights.tconstruct.library.client;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.network.chat.TextColor;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;

import javax.annotation.Nullable;

/**
 * @deprecated use {@link slimeknights.mantle.client.ResourceColorManager}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Deprecated
public class ResourceColorManager {
  /** Default color so the getter can be nonnull */
  @Deprecated
  public static final TextColor WHITE = slimeknights.mantle.client.ResourceColorManager.WHITE;
  /** Instance of this manager */
  @Deprecated
  public static final slimeknights.mantle.client.ResourceColorManager INSTANCE = slimeknights.mantle.client.ResourceColorManager.INSTANCE;

  /**
   * Initializes this manager, registering it with the resource manager
   * @param manager  Manager
   */
  @Deprecated
  public static void init(RegisterClientReloadListenersEvent manager) {
    slimeknights.mantle.client.ResourceColorManager.init(manager);
  }

  /** Gets the text color at the given path, or null if undefined */
  @Nullable
  public static TextColor getOrNull(String path) {
    return slimeknights.mantle.client.ResourceColorManager.getOrNull(path);
  }

  /** Gets the text color at the given path */
  @Deprecated
  public static TextColor getTextColor(String path) {
    return slimeknights.mantle.client.ResourceColorManager.getTextColor(path);
  }

  /** Gets an integer color for the given path */
  @Deprecated
  public static int getColor(String path) {
    return slimeknights.mantle.client.ResourceColorManager.getColor(path);
  }
}
