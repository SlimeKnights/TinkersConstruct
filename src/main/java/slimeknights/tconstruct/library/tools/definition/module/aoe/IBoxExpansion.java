package slimeknights.tconstruct.library.tools.definition.module.aoe;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.data.registry.NamedComponentRegistry;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;

/** Logic that determines how box AOE expands */
public interface IBoxExpansion {
  /** Registered box expansion types */
  NamedComponentRegistry<IBoxExpansion> REGISTRY = new NamedComponentRegistry<>("Unknown Box Expansion Type");

  /**
   * Gets the directions to expand for the given player and side hit
   * @param player   Player instance
   * @param sideHit  Side of the block hit
   * @return  Directions of expansion
   */
  ExpansionDirections getDirections(@Nullable Player player, Direction sideHit);

  /** Computed direction of expansion */
  record ExpansionDirections(Direction width, Direction height, Direction depth, boolean traverseDown) {}

  /** Expands a box around the targeted face */
  IBoxExpansion SIDE_HIT = REGISTRY.register(TConstruct.getResource("side_hit"), (player, sideHit) -> {
    // depth is always direction into the block
    Direction depth = sideHit.getOpposite();
    Direction width, height;
    // for Y, direction is based on facing
    if (sideHit.getAxis() == Axis.Y) {
      height = player != null ? player.getDirection() : Direction.NORTH;
      width = height.getClockWise();
    } else {
      // for X and Z, just rotate from side hit
      width = sideHit.getCounterClockWise();
      height = Direction.UP;
    }
    return new ExpansionDirections(width, height, depth, true);
  });

  /** Box expansion based on the direction the player looks */
  IBoxExpansion PITCH = REGISTRY.register(TConstruct.getResource("pitch"), (player, sideHit) -> {
    // depth is always direction into the block
    Direction width, height, depth;
    if (player != null) {
      Direction playerLook = player.getDirection();
      float pitch = player.getXRot();
      width = playerLook.getClockWise();
      if (pitch < -60) {
        depth = Direction.UP;
        height = playerLook;
      } else if (pitch > 60) {
        depth = Direction.DOWN;
        height = playerLook;
      } else {
        height = Direction.UP;
        depth = playerLook;
      }
    } else {
      // no player? use logic like SIDE_HIT
      // for Y, direction is based on facing
      depth = sideHit.getOpposite();
      if (sideHit.getAxis() == Axis.Y) {
        height = Direction.NORTH;
        width = height.getClockWise();
      } else {
        // for X and Z, just rotate from side hit
        width = sideHit.getCounterClockWise();
        height = Direction.UP;
      }
    }
    return new ExpansionDirections(width, height, depth, true);
  });

  /** Box expansion going up and additionally to a facing side */
  IBoxExpansion HEIGHT = REGISTRY.register(TConstruct.getResource("height"), (player, sideHit) -> {
    // if hit the top or bottom, use facing direction
    Direction depth;
    if (sideHit.getAxis().isVertical()) {
      depth = player != null ? player.getDirection() : Direction.NORTH;
    } else {
      depth = sideHit.getOpposite();
    }
    return new ExpansionDirections(depth.getClockWise(), Direction.UP, depth, false);
  });
}
