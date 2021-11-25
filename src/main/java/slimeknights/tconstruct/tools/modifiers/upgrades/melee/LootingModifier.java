package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.modifiers.SingleLevelModifier;
import slimeknights.tconstruct.library.modifiers.hooks.ILeggingLootModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

public class LootingModifier extends SingleLevelModifier implements ILeggingLootModifier {
  public LootingModifier() {
    super(0x345EC3);
  }

  @Override
  public int getLootingValue(IModifierToolStack tool, int level, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    return looting + level;
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, ILeggingLootModifier.class, this);
  }
}
