package slimeknights.tconstruct.tools.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RayTracer {

  /**
   * Creates a BlockRayTraceEvent with a block mode of COLLIDER and whatever fluid mode you pass to it.
   * Uses the corrected player's starting vector and the corrected ending vector. (Takes into account the players eye level)
   *
   * @param player the player
   * @param fluidMode the raytracing fluid mode
   * @return a BlockRayTraceResult
   */
  public static BlockHitResult retrace(PlayerEntity player, RaycastContext.FluidHandling fluidMode) {
    return retrace(player, RaycastContext.ShapeType.COLLIDER, fluidMode);
  }

  /**
   * Creates a BlockRayTraceEvent with whatever block and fluid mode you pass to it.
   * Uses the corrected player's starting vector and the corrected ending vector. (Takes into account the players eye level)
   *
   * @param player the player
   * @param blockMode the raytracing block mode to use
   * @param fluidMode the raytracing fluid mode to use
   * @return a BlockRayTraceResult
   */
  public static BlockHitResult retrace(PlayerEntity player, RaycastContext.ShapeType blockMode, RaycastContext.FluidHandling fluidMode) {
    return player.world.raycast(new RaycastContext(getStartVector(player), getEndVector(player), blockMode, fluidMode, player));
  }

  /**
   * Gets the starting vector
   *
   * @param player the player
   * @return the start vector
   */
  public static Vec3d getStartVector(PlayerEntity player) {
    return getCorrectedHeadVector(player);
  }

  /**
   * Gets the ending vector
   *
   * @param player the player
   * @return the end vector
   */
  public static Vec3d getEndVector(PlayerEntity player) {
    Vec3d headVec = getCorrectedHeadVector(player);
    Vec3d lookVec = player.getRotationVec(1.0F);
    double reach = getBlockReachDistance(player);
    return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
  }

  /**
   * Gets the corrected head vector
   *
   * @param player the player
   * @return the corrected head vector
   */
  public static Vec3d getCorrectedHeadVector(PlayerEntity player) {
    return new Vec3d(player.getX(), player.getY() + player.getStandingEyeHeight(), player.getZ());
  }

  /**
   * Gets the block reach distance to use for the raytrace
   *
   * @param player the player
   * @return the block reach distance
   */
  public static double getBlockReachDistance(PlayerEntity player) {
    return player.world.isClient ? getBlockReachDistanceClient() : player instanceof ServerPlayerEntity ? getBlockReachDistanceServer((ServerPlayerEntity) player) : 5D;
  }

  /**
   * Gets the block reach distance from the server
   *
   * @return the block reach distance from the server
   */
  private static double getBlockReachDistanceServer(ServerPlayerEntity player) {
    return player.getAttributeInstance(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue();
  }

  /**
   * Gets the block reach distance from the client
   *
   * @return the block reach distance from the client
   */
  @Environment(EnvType.CLIENT)
  private static double getBlockReachDistanceClient() {
    assert MinecraftClient.getInstance().interactionManager != null;

    return MinecraftClient.getInstance().interactionManager.getReachDistance();
  }

}
