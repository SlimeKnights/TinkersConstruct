package slimeknights.tconstruct.tools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RayTracer {

  public static BlockRayTraceResult retrace(PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
    return retrace(player, RayTraceContext.BlockMode.COLLIDER, fluidMode);
  }

  public static BlockRayTraceResult retrace(PlayerEntity player, RayTraceContext.BlockMode blockMode, RayTraceContext.FluidMode fluidMode) {
    return player.world.rayTraceBlocks(new RayTraceContext(getStartVec(player), getEndVec(player), blockMode, fluidMode, player));
  }

  public static Vec3d getStartVec(PlayerEntity player) {
    return getCorrectedHeadVec(player);
  }

  public static Vec3d getEndVec(PlayerEntity player) {
    Vec3d headVec = getCorrectedHeadVec(player);
    Vec3d lookVec = player.getLook(1.0F);
    double reach = getBlockReachDistance(player);
    return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
  }

  public static Vec3d getCorrectedHeadVec(PlayerEntity player) {
    return new Vec3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());
  }

  public static double getBlockReachDistance(PlayerEntity player) {
    return player.world.isRemote ? getBlockReachDistanceClient() : player instanceof ServerPlayerEntity ? getBlockReachDistanceServer((ServerPlayerEntity) player) : 5D;
  }

  private static double getBlockReachDistanceServer(ServerPlayerEntity player) {
    return player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
  }

  @OnlyIn(Dist.CLIENT)
  private static double getBlockReachDistanceClient() {
    return Minecraft.getInstance().playerController.getBlockReachDistance();
  }

}
