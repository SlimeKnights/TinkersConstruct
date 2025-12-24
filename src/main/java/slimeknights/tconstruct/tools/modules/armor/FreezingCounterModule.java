package slimeknights.tconstruct.tools.modules.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Module implementing the counterattack side of freezing */
public record FreezingCounterModule(LevelingValue chance, LevelingValue constant, LevelingValue random, int durabilityUsage, IJsonPredicate<LivingEntity> defender, IJsonPredicate<LivingEntity> attacker, ModifierCondition<IToolStackView> condition) implements CounterModule {
  public static final RecordLoadable<FreezingCounterModule> LOADER = CounterModule.makeLoader("seconds", FreezingCounterModule::new);

  /** @apiNote use {@link #builder()} */
  @Internal
  public FreezingCounterModule {}

  /** Creates a new builder instance */
  public static CounterModule.Builder<FreezingCounterModule> builder() {
    return new CounterModule.Builder<>(FreezingCounterModule::new);
  }

  @Override
  public RecordLoadable<FreezingCounterModule> getLoader() {
    return LOADER;
  }

  @Override
  public boolean canApply(Entity target) {
    return target.canFreeze();
  }

  @Override
  public void applyEffect(IToolStackView tool, ModifierEntry modifier, float value, EquipmentContext context, Entity attacker, DamageSource source, float damageDealt) {
    attacker.setTicksFrozen(Math.max(attacker.getTicksRequiredToFreeze(), attacker.getTicksFrozen()) + (int)(value * 40));
    attacker.clearFire();
  }
}
