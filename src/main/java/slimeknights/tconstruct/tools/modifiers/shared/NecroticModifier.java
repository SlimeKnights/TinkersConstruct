package slimeknights.tconstruct.tools.modifiers.shared;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.helper.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class NecroticModifier extends Modifier {
  public NecroticModifier() {
    super(0x4D4D4D);
  }

  @Override
  public int afterEntityHit(IModifierToolStack tool, int level, ToolAttackContext context, float damageDealt) {
    if (context.isFullyCharged() && damageDealt > 0) {
      // heals between 0 and (level) * 5% of damage dealt
      LivingEntity attacker = context.getAttacker();
      float heal = attacker.getRNG().nextFloat() * damageDealt * level * 0.05f;
      attacker.heal(heal);
      if (heal > 2) {
        attacker.world.playSound(null, attacker.getPosX(), attacker.getPosY(), attacker.getPosZ(), SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.PLAYERS, 1.0f, 1.0f);
      }
    }
    return 0;
  }
}
