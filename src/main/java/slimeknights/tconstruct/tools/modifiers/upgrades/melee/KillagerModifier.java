package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import slimeknights.tconstruct.common.TinkerTags;

public class KillagerModifier extends ScaledTypeDamageModifier {
  public KillagerModifier() {
    super(MobType.ILLAGER);
  }

  @Override
  protected boolean isEffective(LivingEntity target) {
    return super.isEffective(target) || target.getType().is(TinkerTags.EntityTypes.KILLAGERS);
  }
}
