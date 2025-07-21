package slimeknights.tconstruct.library.utils;

import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock.Action;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Logic to keep track of the side of the block that was last hit
 */
public class BlockSideHitListener {
  private static final Map<UUID,Direction> HIT_FACE = new HashMap<>();
  @Getter
  private static Direction clientSideHit = Direction.UP;
  private static boolean init = false;

  /** Initializes this listener */
  public static void init() {
    if (init) {
      return;
    }
    init = true;
    MinecraftForge.EVENT_BUS.addListener(BlockSideHitListener::onLeftClickBlock);
    MinecraftForge.EVENT_BUS.addListener(BlockSideHitListener::onLeaveServer);
  }

  /** Called when the player left-clicks a block to store the face */
  private static void onLeftClickBlock(LeftClickBlock event) {
    if (event.getAction() == Action.START) {
      Player player = event.getEntity();
      if (player.level().isClientSide()) {
        clientSideHit = event.getFace();
      } else {
        HIT_FACE.put(player.getUUID(), event.getFace());
      }
    }
  }

  /** Called when a player leaves the server to clear the face */
  private static void onLeaveServer(PlayerLoggedOutEvent event) {
    HIT_FACE.remove(event.getEntity().getUUID());
  }

  /**
   * Gets the side this player last hit, should return correct values in most modifier hooks related to block breaking
   * @param player  Player
   * @return  Side last hit
   */
  public static Direction getSideHit(Player player) {
    if (player.level().isClientSide()) {
      return clientSideHit;
    }
    return HIT_FACE.getOrDefault(player.getUUID(), Direction.UP);
  }
}
