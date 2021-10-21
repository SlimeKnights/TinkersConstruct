package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class DecayModifier extends Modifier {
  public DecayModifier() {
    super(0x7F9374);
  }
  
  /* gets the effect for the given level, including a random time */
  private static EffectInstance makeDecayEffect(int level) {
    // potions are 0 indexed instead of 1 indexed
    // wither skeletons apply 10 seconds of wither for comparison
    return new EffectInstance(Effects.WITHER, 20 * (5 + (RANDOM.nextInt(level * 3))), level - 1);
  }

  @Override
  public int afterEntityHit(IModifierToolStack tool, int level, ToolAttackContext context, float damageDealt) {
    if (context.isFullyCharged()) {
      // note the time of each effect is calculated independantly

      // 25% chance to poison yourself
      if (RANDOM.nextInt(3) == 0) {
        context.getAttacker().addPotionEffect(makeDecayEffect(level));
      }

      // always poison the target, means it works twice as often as lacerating
      LivingEntity target = context.getLivingTarget();
      if (target != null && target.isAlive()) {
        target.addPotionEffect(makeDecayEffect(level));
      }
    }
    return 0;
  }
}
