package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.modifiers.SingleLevelModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

public class LootingModifier extends SingleLevelModifier {
  public LootingModifier() {
    super(0x345EC3);
  }

  @Override
  public int getLootingValue(IModifierToolStack tool, int level, LivingEntity holder, @Nullable LivingEntity target, @Nullable DamageSource damageSource, int looting) {
    return looting + level;
  }
}
