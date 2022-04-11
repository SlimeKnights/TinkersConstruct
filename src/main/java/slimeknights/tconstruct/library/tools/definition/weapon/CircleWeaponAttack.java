package slimeknights.tconstruct.library.tools.definition.weapon;

import com.google.gson.JsonObject;
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

/** Deals damage in a circle around the primary target */
@RequiredArgsConstructor
public class CircleWeaponAttack implements IWeaponAttack {
  public static final Loader LOADER = new Loader();

  private final float radius;

  @Override
  public boolean dealDamage(IToolStackView tool, ToolAttackContext context, float damage) {
    boolean hit = ToolAttackUtil.dealDefaultDamage(context.getAttacker(), context.getTarget(), damage);
    // only need fully charged for scythe sweep, easier than sword sweep
    if (context.isFullyCharged()) {
      // basically sword sweep logic, just deals full damage to all entities
      double range = radius + tool.getModifierLevel(TinkerModifiers.expanded.getId());
      // allow having no range until modified with range
      if (radius > 0) {
        double rangeSq = range * range;
        LivingEntity attacker = context.getAttacker();
        Entity target = context.getTarget();
        for (LivingEntity aoeTarget : attacker.level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(range, 0.25D, range))) {
          if (aoeTarget != attacker && aoeTarget != target && !attacker.isAlliedTo(aoeTarget)
              && !(aoeTarget instanceof ArmorStand stand && stand.isMarker()) && target.distanceToSqr(aoeTarget) < rangeSq) {
            float angle = attacker.getYRot() * ((float)Math.PI / 180F);
            aoeTarget.knockback(0.4F, Mth.sin(angle), -Mth.cos(angle));
            hit |= ToolAttackUtil.extraEntityAttack(tool, attacker, context.getHand(), aoeTarget);
          }
        }

        attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F, 1.0F);
        if (attacker instanceof Player player) {
          player.sweepAttack();
        }
      }
    }

    return hit;
  }

  @Override
  public IGenericLoader<? extends IWeaponAttack> getLoader() {
    return LOADER;
  }

  private static class Loader implements IGenericLoader<CircleWeaponAttack> {
    @Override
    public CircleWeaponAttack deserialize(JsonObject json) {
      return new CircleWeaponAttack(GsonHelper.getAsFloat(json, "diameter"));
    }

    @Override
    public CircleWeaponAttack fromNetwork(FriendlyByteBuf buffer) {
      return new CircleWeaponAttack(buffer.readFloat());
    }

    @Override
    public void serialize(CircleWeaponAttack object, JsonObject json) {
      json.addProperty("diameter", object.radius);
    }

    @Override
    public void toNetwork(CircleWeaponAttack object, FriendlyByteBuf buffer) {
      buffer.writeFloat(object.radius);
    }
  }
}
