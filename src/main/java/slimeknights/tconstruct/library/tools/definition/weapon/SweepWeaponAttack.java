package slimeknights.tconstruct.library.tools.definition.weapon;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

/** Attack logic for a sweep attack, similar to a sword */
@RequiredArgsConstructor
public class SweepWeaponAttack implements IWeaponAttack {
  public static final Loader LOADER = new Loader();

  @Getter @VisibleForTesting
  private final float range;

  @Override
  public boolean dealDamage(IToolStackView tool, ToolAttackContext context, float damage) {
    // deal damage first
    boolean hit = ToolAttackUtil.dealDefaultDamage(context.getAttacker(), context.getTarget(), damage);

    // sweep code from Player#attack(Entity)
    // basically: no crit, no sprinting and has to stand on the ground for sweep. Also has to move regularly slowly
    LivingEntity attacker = context.getAttacker();
    if (hit && context.isFullyCharged() && !attacker.isSprinting() && !context.isCritical() && attacker.isOnGround() && (attacker.walkDist - attacker.walkDistO) < attacker.getSpeed()) {
      // loop through all nearby entities
      double range = this.range + tool.getModifierLevel(TinkerModifiers.expanded.getId());
      double rangeSq = (2 + range);
      rangeSq *= rangeSq;
      // if the modifier is missing, sweeping damage will be 0, so easiest to let it fully control this
      float sweepDamage = TinkerModifiers.sweeping.get().getSweepingDamage(tool, damage);
      Entity target = context.getTarget();
      for (LivingEntity aoeTarget : attacker.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(range, 0.25D, range))) {
        if (aoeTarget != attacker && aoeTarget != target && !attacker.isAlliedTo(aoeTarget)
            && !(aoeTarget instanceof ArmorStand armorStand && armorStand.isMarker()) && attacker.distanceToSqr(aoeTarget) < rangeSq) {
          float angle = attacker.getYRot() * ((float) Math.PI / 180F);
          aoeTarget.knockback(0.4F, Mth.sin(angle), -Mth.cos(angle));
          ToolAttackUtil.dealDefaultDamage(attacker, aoeTarget, sweepDamage);
        }
      }

      attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F, 1.0F);
      if (attacker instanceof Player player) {
        player.sweepAttack();
      }
    }

    return hit;
  }

  @Override
  public IGenericLoader<? extends IWeaponAttack> getLoader() {
    return LOADER;
  }

  private static class Loader implements IGenericLoader<SweepWeaponAttack> {
    @Override
    public SweepWeaponAttack deserialize(JsonObject json) {
      return new SweepWeaponAttack(GsonHelper.getAsFloat(json, "range"));
    }

    @Override
    public SweepWeaponAttack fromNetwork(FriendlyByteBuf buffer) {
      return new SweepWeaponAttack(buffer.readFloat());
    }

    @Override
    public void serialize(SweepWeaponAttack object, JsonObject json) {
      json.addProperty("range", object.range);
    }

    @Override
    public void toNetwork(SweepWeaponAttack object, FriendlyByteBuf buffer) {
      buffer.writeFloat(object.range);
    }
  }
}
