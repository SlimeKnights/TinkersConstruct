package slimeknights.tconstruct.library.tools.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** Object for common context for weapon attack hooks */
@Getter
@RequiredArgsConstructor
public class ToolAttackContext {
  /** Entity doing the attacking */
  @Nonnull
  private final LivingEntity attacker;
  /** Player doing the attacking, null if not a player */
  @Nullable
  private final Player playerAttacker;
  /** Hand containing the tool */
  @Nonnull
  private final InteractionHand hand;
  @Nonnull
  private final EquipmentSlot slotType;
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

  public ToolAttackContext(LivingEntity attacker, @Nullable Player playerAttacker, InteractionHand hand, Entity target, @Nullable LivingEntity livingTarget, boolean isCritical, float cooldown, boolean isExtraAttack) {
    this(attacker, playerAttacker, hand, Util.getSlotType(hand), target, livingTarget, isCritical, cooldown, isExtraAttack);
  }

  /** Returns true if this attack is fully charged */
  public boolean isFullyCharged() {
    return getCooldown() > 0.9f;
  }

  /** Gets the level for this context */
  public Level getLevel() {
    return attacker.level();
  }
}
