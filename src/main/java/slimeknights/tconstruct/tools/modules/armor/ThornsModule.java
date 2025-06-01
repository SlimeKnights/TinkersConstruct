package slimeknights.tconstruct.tools.modules.armor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Module implementing thorns */
public record ThornsModule(LevelingValue chance, LevelingValue constant, LevelingValue random, int durabilityUsage, ModifierCondition<IToolStackView> condition) implements CounterModule {
  public static final RecordLoadable<ThornsModule> LOADER = CounterModule.makeLoader("damage", ThornsModule::new);

  /** @apiNote use {@link #builder()} */
  @Internal
  public ThornsModule {}

  /** Creates a new builder instance */
  public static CounterModule.Builder<ThornsModule> builder() {
    return new CounterModule.Builder<>(ThornsModule::new);
  }

  @Override
  public RecordLoadable<ThornsModule> getLoader() {
    return LOADER;
  }

  @Override
  public void applyEffect(IToolStackView tool, ModifierEntry modifier, float value, EquipmentContext context, Entity attacker, DamageSource source, float damageDealt) {
    // this works like vanilla, damage is capped due to the hurt immunity mechanics, so if multiple pieces apply thorns between us and vanilla, damage is capped at max amount
    LivingEntity defender = context.getEntity();
    attacker.hurt(defender.damageSources().thorns(defender), value);
  }
}
