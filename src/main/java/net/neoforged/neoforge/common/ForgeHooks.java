package slimeknights.tconstruct.compat.neoforged.neoforge.common;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class ForgeHooks {
  private ForgeHooks() {}

  @Nullable
  public static float[] onLivingFall(LivingEntity entity, float distance, float damageMultiplier) {
    return CommonHooks.onLivingFall(entity, distance, damageMultiplier);
  }

  public static void setCraftingPlayer(Player player) {
    CommonHooks.setCraftingPlayer(player);
  }

  public static Player getCraftingPlayer() {
    return CommonHooks.getCraftingPlayer();
  }

  public static int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
    return EventHooks.getItemBurnTime(stack, stack.getBurnTime(recipeType), recipeType);
  }

  public static ItemStack getProjectile(LivingEntity entity, ItemStack bow, ItemStack projectile) {
    return CommonHooks.getProjectile(entity, bow, projectile);
  }

  public static CriticalHitEvent getCriticalHit(Player player, Entity target, boolean vanillaCritical, float damageModifier) {
    return CommonHooks.fireCriticalHit(player, target, vanillaCritical, damageModifier);
  }

  @Nullable
  public static InteractionResult onInteractEntityAt(Player player, Entity entity, HitResult ray, InteractionHand hand) {
    return CommonHooks.onInteractEntityAt(player, entity, ray, hand);
  }

  public static PlayerInteractEvent.RightClickBlock onRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec) {
    return CommonHooks.onRightClickBlock(player, hand, pos, hitVec);
  }

  public static PlayerInteractEvent.LeftClickBlock onLeftClickBlock(Player player, BlockPos pos, net.minecraft.core.Direction face, ServerboundPlayerActionPacket.Action action) {
    return CommonHooks.onLeftClickBlock(player, pos, face, action);
  }

  public static int onBlockBreakEvent(ServerLevel level, GameType gameType, ServerPlayer player, BlockPos pos) {
    var state = level.getBlockState(pos);
    var event = CommonHooks.fireBlockBreak(level, gameType, player, pos, state);
    if (event.isCanceled()) {
      return -1;
    }
    return state.getExpDrop(level, pos, level.getBlockEntity(pos), player, player.getMainHandItem());
  }
}
