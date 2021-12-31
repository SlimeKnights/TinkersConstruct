package slimeknights.tconstruct.tools.item.broad;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.item.ToolItem;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class ScytheTool extends ToolItem {
  public ScytheTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean dealDamage(IModifierToolStack tool, ToolAttackContext context, float damage) {
    boolean hit = super.dealDamage(tool, context, damage);
    // only need fully charged for scythe sweep, easier than sword sweep
    if (context.isFullyCharged()) {
      // basically sword sweep logic, just deals full damage to all entities
      double range = 3 + tool.getModifierLevel(TinkerModifiers.expanded.get());
      LivingEntity attacker = context.getAttacker();
      Entity target = context.getTarget();
      for (LivingEntity aoeTarget : attacker.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(range, 0.25D, range))) {
        if (aoeTarget != attacker && aoeTarget != target && !attacker.isAlliedTo(aoeTarget)
            && !(aoeTarget instanceof ArmorStand stand && stand.isMarker()) && attacker.distanceToSqr(aoeTarget) < 8.0D + range) {
          float angle = attacker.getYRot() * ((float) Math.PI / 180F);
          aoeTarget.knockback(0.4F, Mth.sin(angle), -Mth.cos(angle));
          hit |= ToolAttackUtil.extraEntityAttack(this, tool, attacker, context.getHand(), aoeTarget);
        }
      }

      attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F, 1.0F);
      if (attacker instanceof Player player) {
        player.sweepAttack();
      }
    }

    return hit;
  }
}
