package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockSideHitListener {
  private static final Map<UUID,Direction> HIT_FACE = new HashMap<>();
  private static boolean init = false;

  public static void init() {
    if (init) {
      return;
    }
    init = true;
//    MinecraftForge.EVENT_BUS.addListener(BlockSideHitListener::onLeftClickBlock);
//    MinecraftForge.EVENT_BUS.addListener(BlockSideHitListener::onLeftClickBlock);
  }

  /** Called when the player left clicks a block to store the face *//*
  private static void onLeftClickBlock(LeftClickBlock event) {
    HIT_FACE.put(event.getPlayer().getUniqueID(), event.getFace());
  }*/

  /** Called when a player leaves the server to clear the face */
//  private static void onLeaveServer(PlayerLoggedOutEvent event) {
//    HIT_FACE.remove(event.getPlayer().getUniqueID());
//  }

  /**
   * Gets the side this player last hit
   * @param player  Player
   * @return  Side last hit
   */
  public static Direction getSideHit(PlayerEntity player) {
    return HIT_FACE.getOrDefault(player.getUuid(), Direction.UP);
  }
}
