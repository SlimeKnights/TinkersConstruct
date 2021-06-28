package slimeknights.tconstruct.library.tools.helper;

import lombok.Data;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Object for common context for weapon attack hooks */
@Data
public class ToolAttackContext {
  /** Entity doing the attacking */
  @Nonnull
  private final LivingEntity attacker;
  /** Player doing the attacking, null if not a player */
  @Nullable
  private final PlayerEntity playerAttacker;
  /** Hand containing the tool */
  @Nonnull
  private final Hand hand;
  /** Originally targeted entity, may be different from {@link #getTarget()} for multipart entities */
  @Nonnull
  private final Entity target;
  /** Target entity */
  @Nullable
  private final LivingEntity livingTarget;
  /** If true, attack is a critical hit */
  private final boolean isCritical;
  /** Current attack cooldown */
  private final float cooldown;
  /** If true, this is a secondary attack, such as for scythes */
  private final boolean isExtraAttack;

  /** Returns true if this attack is fully charged */
  public boolean isFullyCharged() {
    return getCooldown() > 0.9f;
  }
}
