package slimeknights.tconstruct.tools.modifiers.shared;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class NecroticModifier extends Modifier {
  public NecroticModifier() {
    super(0x4D4D4D);
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, float cooldown) {
    if (cooldown > 0.9 && damageDealt > 0) {
      // every level gives a +10% chance of healing you 10%
      if (attacker.getRNG().nextFloat() < level * 0.1) {
        attacker.heal(0.1f * damageDealt);
        attacker.world.playSound(null, attacker.getPosX(), attacker.getPosY(), attacker.getPosZ(), SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.PLAYERS, 1.0f, 1.0f);
      }

    }
    return 0;
  }
}
