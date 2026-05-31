package slimeknights.tconstruct.compat.neoforged.neoforge.event;

import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;

public final class ForgeEventFactory {
  private ForgeEventFactory() {}

  public static void firePlayerCraftingEvent(Player player, ItemStack crafted, Container craftMatrix) {
    EventHooks.firePlayerCraftingEvent(player, crafted, craftMatrix);
  }

  public static void onPlayerDestroyItem(Player player, ItemStack stack, InteractionHand hand) {
    EventHooks.onPlayerDestroyItem(player, stack, hand);
  }

  public static boolean onBlockPlace(Entity entity, BlockSnapshot blockSnapshot, Direction direction) {
    return EventHooks.onBlockPlace(entity, blockSnapshot, direction);
  }

  public static boolean onProjectileImpact(Projectile projectile, HitResult ray) {
    return EventHooks.onProjectileImpact(projectile, ray);
  }

  public static boolean onExplosionStart(Level level, Explosion explosion) {
    return EventHooks.onExplosionStart(level, explosion);
  }

  public static void onExplosionDetonate(Level level, Explosion explosion, List<Entity> list, double diameter) {
    EventHooks.onExplosionDetonate(level, explosion, list, diameter);
  }

  public static InteractionResultHolder<ItemStack> onArrowNock(ItemStack item, Level level, Player player, InteractionHand hand, boolean hasAmmo) {
    return EventHooks.onArrowNock(item, level, player, hand, hasAmmo);
  }

  public static int onArrowLoose(ItemStack stack, Level level, Player player, int charge, boolean hasAmmo) {
    return EventHooks.onArrowLoose(stack, level, player, charge, hasAmmo);
  }
}
