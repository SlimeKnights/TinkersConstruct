package slimeknights.tconstruct.library.tools.item.ranged;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface LauncherUserInfo {

  static LauncherUserInfo playerLike(LivingEntity user, float speed) {
    return new PlayerLike(user, speed);
  }

  LivingEntity user();

  default Level level() {
    return user().level();
  }

  /**
   * regular arrow speed at full charge. 3 for player with bow. 1.6 for skeletons. 3.15 for crossbows.
   */
  float speedFactor();

  /**
   * shoot inaccuracy factor. 1 for player, and hostile mobs use a complex formula involving difficulty.
   */
  float inaccuracyFactor();

  /**
   * Whether to make the arrow marked as no-pickup. True for infinite arrows or hostile mob arrows.
   */
  boolean infinite();

  /**
   * Whether to damage weapon. True for hostile mobs.
   */
  boolean damageWeapon();

  record PlayerLike(LivingEntity user, float speedFactor) implements LauncherUserInfo {

    @Override
    public float inaccuracyFactor() {
      return 1;
    }

    @Override
    public boolean infinite() {
      return user instanceof Player player && player.getAbilities().instabuild;
    }

    @Override
    public boolean damageWeapon() {
      return !infinite();
    }

  }

}
