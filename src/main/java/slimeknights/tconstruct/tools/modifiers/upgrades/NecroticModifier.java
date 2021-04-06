package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class NecroticModifier extends IncrementalModifier {
  public NecroticModifier() {
    super(0x4D4D4D);
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, boolean fullyCharged) {
    if (fullyCharged && damageDealt > 0) {
      // every level gives a +10% chance of healing you 10%
      if (attacker.getRNG().nextFloat() < getScaledLevel(tool, level) * 0.1) {
        attacker.heal(0.1f * damageDealt);
        attacker.world.playSound(null, attacker.getPosX(), attacker.getPosY(), attacker.getPosZ(), SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.PLAYERS, 1.0f, 1.0f);
      }

    }
    return 0;
  }
}
