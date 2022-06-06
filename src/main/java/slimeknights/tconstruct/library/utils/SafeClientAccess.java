package slimeknights.tconstruct.library.utils;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;

/** @deprecated use {@link slimeknights.mantle.client.SafeClientAccess} */
@Deprecated
public class SafeClientAccess {
  /** @deprecated use {@link slimeknights.mantle.client.SafeClientAccess#getTooltipKey()} */
  @Deprecated
  public static TooltipKey getTooltipKey() {
    if (FMLEnvironment.dist == Dist.CLIENT) {
      return ClientOnly.getPressedKey();
    }
    return TooltipKey.UNKNOWN;
  }

  /** @deprecated use {@link slimeknights.mantle.client.SafeClientAccess#getPlayer()} */
  @Nullable @Deprecated
  public static Player getPlayer() {
    return slimeknights.mantle.client.SafeClientAccess.getPlayer();
  }

  /** This class is only loaded on the client, so is safe to reference client only methods */
  private static class ClientOnly {
    /** Gets the currently pressed key modifier for tooltips */
    public static TooltipKey getPressedKey() {
      if (Screen.hasShiftDown()) {
        return TooltipKey.SHIFT;
      }
      if (Screen.hasControlDown()) {
        return TooltipKey.CONTROL;
      }
      if (Screen.hasAltDown()) {
        return TooltipKey.ALT;
      }
      return TooltipKey.NORMAL;
    }
  }
}
