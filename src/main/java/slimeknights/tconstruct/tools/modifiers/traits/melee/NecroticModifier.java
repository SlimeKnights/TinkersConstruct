package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class NecroticModifier extends Modifier {
  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    if (context.isFullyCharged() && damageDealt > 0) {
      // heals between 0 and (level) * 5% of damage dealt
      LivingEntity attacker = context.getAttacker();
      float chance = attacker.getRandom().nextFloat();
      attacker.heal(chance * damageDealt * level * 0.05f);
      if (chance > 0.5f) {
        attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), Sounds.NECROTIC_HEAL.getSound(), SoundSource.PLAYERS, 1.0f, 1.0f);
      }
    }
    return 0;
  }
}
