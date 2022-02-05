package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class BaneOfSssssModifier extends ScaledTypeDamageModifier {
  public BaneOfSssssModifier() {
    super(MobType.ARTHROPOD);
  }

  @Override
  protected boolean isEffective(LivingEntity target) {
    return super.isEffective(target) || target.getType().is(TinkerTags.EntityTypes.CREEPERS);
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    LivingEntity target = context.getLivingTarget();
    if (target != null && isEffective(target)) {
      int duration = 20;
      int maxBonus = (int)(10 * getScaledLevel(tool, level));
      if (maxBonus > 0) {
        duration += context.getAttacker().getRandom().nextInt(maxBonus);
      }
      target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 3));
    }
    return 0;
  }
}
