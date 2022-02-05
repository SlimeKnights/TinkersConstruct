package slimeknights.tconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorLootModifier;
import slimeknights.tconstruct.library.modifiers.impl.SingleLevelModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public class LootingModifier extends SingleLevelModifier implements IArmorLootModifier {
  @Override
  public int getLootingValue(IToolStackView tool, int level, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    return looting + level;
  }

  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    return tryModuleMatch(type, IArmorLootModifier.class, this);
  }
}
