package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class LaceratingModifier extends Modifier {
  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    // 50% chance of applying
    LivingEntity target = context.getLivingTarget();
    if (target != null && context.isFullyCharged() && target.isAlive() && RANDOM.nextFloat() < 0.50f) {
      // set entity so the potion is attributed as a player kill
      target.setLastHurtMob(context.getAttacker());
      // potions are 0 indexed instead of 1 indexed
      // 81 ticks will do about 5 damage at level 1
      TinkerModifiers.bleeding.get().apply(target, 1 + 20 * (2 + (RANDOM.nextInt(level + 3))), level - 1, true);
    }
    return 0;
  }
}
