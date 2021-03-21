package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class BaneOfArthropodsModifier extends ScaledTypeDamageModifier {
  public BaneOfArthropodsModifier() {
    super(0xD39A88, CreatureAttribute.ARTHROPOD);
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, boolean fullyCharged) {
    if (target.getCreatureAttribute() == CreatureAttribute.ARTHROPOD) {
      int duration = 20 + attacker.getRNG().nextInt(10 * level);
      target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, duration, 3));
    }
    return 0;
  }
}
