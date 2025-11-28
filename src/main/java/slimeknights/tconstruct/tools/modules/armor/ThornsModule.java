package slimeknights.tconstruct.tools.modules.armor;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.util.CombatHelper;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Module implementing thorns */
public record ThornsModule(ResourceKey<DamageType> damageType, LevelingValue chance, LevelingValue constant, LevelingValue random, int durabilityUsage, IJsonPredicate<LivingEntity> defender, IJsonPredicate<LivingEntity> attacker, ModifierCondition<IToolStackView> condition) implements CounterModule {
  public static final RecordLoadable<ThornsModule> LOADER = RecordLoadable.create(
    Loadables.DAMAGE_TYPE_KEY.defaultField("damage_type", DamageTypes.THORNS, true, ThornsModule::damageType),
    CHANCE_FIELD,
    LevelingValue.LOADABLE.defaultField("constant_damage", LevelingValue.ZERO, CounterModule::constant),
    LevelingValue.LOADABLE.defaultField("random_damage", LevelingValue.ZERO, CounterModule::random),
    DURABILITY_FIELD, DEFENDER_FIELD, ATTACKER_FIELD, ModifierCondition.TOOL_FIELD,
    ThornsModule::new);

  /** @apiNote use {@link #type(ResourceKey)} */
  @Internal
  public ThornsModule {}

  /** @deprecated use {@link #type(ResourceKey)} */
  @Deprecated(forRemoval = true)
  public static CounterModule.Builder<ThornsModule> builder() {
    return type(DamageTypes.THORNS);
  }

  /** Creates a new builder instance */
  public static CounterModule.Builder<ThornsModule> type(ResourceKey<DamageType> type) {
    return new CounterModule.Builder<>((chance, constant, random, durability, defender, attacker, condition) -> new ThornsModule(type, chance, constant, random, durability, defender, attacker, condition));
  }

  @Override
  public RecordLoadable<ThornsModule> getLoader() {
    return LOADER;
  }

  @Override
  public void applyEffect(IToolStackView tool, ModifierEntry modifier, float value, EquipmentContext context, Entity attacker, DamageSource source, float damageDealt) {
    // this works like vanilla, damage is capped due to the hurt immunity mechanics, so if multiple pieces apply thorns between us and vanilla, damage is capped at max amount
    attacker.hurt(CombatHelper.damageSource(damageType, context.getEntity()), value);
  }
}
