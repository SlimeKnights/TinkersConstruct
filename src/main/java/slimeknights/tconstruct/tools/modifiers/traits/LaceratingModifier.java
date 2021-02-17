package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class LaceratingModifier extends Modifier {
  public LaceratingModifier() {
    super(0x601cc4);
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, boolean fullyCharged) {
    // 25% chance of applying per level
    if (fullyCharged && target.isAlive() && RANDOM.nextFloat() < 0.50f) {
      // set entity so the potion is attributed as a player kill
      target.setLastAttackedEntity(attacker);
      // potions are 0 indexed instead of 1 indexed
      // 81 ticks will do about 5 damage at level 1
      TinkerModifiers.bleeding.get().apply(target, 1 + 20 * (2 + (RANDOM.nextInt(level + 3))), level - 1);
    }
    return 0;
  }
}
