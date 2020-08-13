package slimeknights.tconstruct.tools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
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
  public static BlockRayTraceResult retrace(PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
    return retrace(player, RayTraceContext.BlockMode.COLLIDER, fluidMode);
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
  public static BlockRayTraceResult retrace(PlayerEntity player, RayTraceContext.BlockMode blockMode, RayTraceContext.FluidMode fluidMode) {
    return player.world.rayTraceBlocks(new RayTraceContext(getStartVector(player), getEndVector(player), blockMode, fluidMode, player));
  }

  /**
   * Gets the starting vector
   *
   * @param player the player
   * @return the start vector
   */
  public static Vector3d getStartVector(PlayerEntity player) {
    return getCorrectedHeadVector(player);
  }

  /**
   * Gets the ending vector
   *
   * @param player the player
   * @return the end vector
   */
  public static Vector3d getEndVector(PlayerEntity player) {
    Vector3d headVec = getCorrectedHeadVector(player);
    Vector3d lookVec = player.getLook(1.0F);
    double reach = getBlockReachDistance(player);
    return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
  }

  /**
   * Gets the corrected head vector
   *
   * @param player the player
   * @return the corrected head vector
   */
  public static Vector3d getCorrectedHeadVector(PlayerEntity player) {
    return new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());
  }

  /**
   * Gets the block reach distance to use for the raytrace
   *
   * @param player the player
   * @return the block reach distance
   */
  public static double getBlockReachDistance(PlayerEntity player) {
    return player.world.isRemote ? getBlockReachDistanceClient() : player instanceof ServerPlayerEntity ? getBlockReachDistanceServer((ServerPlayerEntity) player) : 5D;
  }

  /**
   * Gets the block reach distance from the server
   *
   * @return the block reach distance from the server
   */
  private static double getBlockReachDistanceServer(ServerPlayerEntity player) {
    return player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get()).getValue();
  }

  /**
   * Gets the block reach distance from the client
   *
   * @return the block reach distance from the client
   */
  @OnlyIn(Dist.CLIENT)
  private static double getBlockReachDistanceClient() {
    assert Minecraft.getInstance().playerController != null;

    return Minecraft.getInstance().playerController.getBlockReachDistance();
  }

}
