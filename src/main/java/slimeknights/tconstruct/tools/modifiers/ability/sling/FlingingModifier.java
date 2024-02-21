package slimeknights.tconstruct.tools.modifiers.ability.sling;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.SlimeBounceHandler;

import java.util.Random;

/** Add velocity opposite of the targeted block */
public class FlingingModifier extends SlingModifier {
  @Override
  public boolean onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
    super.onStoppedUsing(tool, modifier, entity, timeLeft);
    if (entity.isOnGround() && entity instanceof Player player) {
      // check if player was targeting a block
      BlockHitResult mop = ModifiableItem.blockRayTrace(entity.level, player, ClipContext.Fluid.NONE);
      if (mop.getType() == HitResult.Type.BLOCK) {
        // we fling the inverted player look vector
        float f = getForce(tool, modifier, entity, timeLeft, true) * 4;
        if (f > 0) {
          Vec3 vec = player.getLookAngle().normalize();
          float inaccuracy = ModifierUtil.getInaccuracy(tool, player, 1) * 0.0075f;
          Random random = player.getRandom();
          player.push((vec.x + random.nextGaussian() * inaccuracy) * -f,
                      (vec.y + random.nextGaussian() * inaccuracy) * -f / 3f,
                      (vec.z + random.nextGaussian() * inaccuracy) * -f);
          SlimeBounceHandler.addBounceHandler(player);
          if (!entity.level.isClientSide) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.SLIME_SLING.getSound(), player.getSoundSource(), 1, 1);
            player.causeFoodExhaustion(0.2F);
            player.getCooldowns().addCooldown(tool.getItem(), 3);
            ToolDamageUtil.damageAnimated(tool, 1, entity);
          }
          return true;
        }
      }
    }
    entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), Sounds.SLIME_SLING.getSound(), entity.getSoundSource(), 1, 0.5f);
    return true;
  }
}
