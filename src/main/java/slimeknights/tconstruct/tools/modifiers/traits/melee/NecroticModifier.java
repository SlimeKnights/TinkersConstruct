package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundCategory;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
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
      float chance = attacker.getRNG().nextFloat();
      attacker.heal(chance * damageDealt * level * 0.05f);
      if (chance > 0.5f) {
        attacker.world.playSound(null, attacker.getPosX(), attacker.getPosY(), attacker.getPosZ(), Sounds.NECROTIC_HEAL.getSound(), SoundCategory.PLAYERS, 1.0f, 1.0f);
      }
    }
    return 0;
  }
}
