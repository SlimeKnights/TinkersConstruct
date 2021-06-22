package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class BaneOfSssssModifier extends ScaledTypeDamageModifier {
  public BaneOfSssssModifier() {
    super(0xD39A88, CreatureAttribute.ARTHROPOD);
  }

  @Override
  protected boolean isEffective(LivingEntity target) {
    return super.isEffective(target) || target.getType().isContained(TinkerTags.EntityTypes.CREEPERS);
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, Hand hand, LivingEntity target, float damageDealt, boolean isCritical, float cooldown, boolean isExtraAttack) {
    if (isEffective(target)) {
      int duration = 20;
      int maxBonus = (int)(10 * getScaledLevel(tool, level));
      if (maxBonus > 0) {
        duration += attacker.getRNG().nextInt(maxBonus);
      }
      target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, duration, 3));
    }
    return 0;
  }
}
