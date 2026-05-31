package slimeknights.tconstruct.compat.neoforged.neoforge.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LootingLevelEvent extends LivingEvent {
  private final DamageSource damageSource;
  private int lootingLevel;

  public LootingLevelEvent(LivingEntity entity, DamageSource damageSource, int lootingLevel) {
    super(entity);
    this.damageSource = damageSource;
    this.lootingLevel = lootingLevel;
  }

  public DamageSource getDamageSource() {
    return damageSource;
  }

  public int getLootingLevel() {
    return lootingLevel;
  }

  public void setLootingLevel(int lootingLevel) {
    this.lootingLevel = lootingLevel;
  }
}
