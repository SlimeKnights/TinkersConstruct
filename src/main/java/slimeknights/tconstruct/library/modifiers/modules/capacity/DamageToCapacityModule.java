package slimeknights.tconstruct.library.modifiers.modules.capacity;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.CapacityBarHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/** Module that restores capacity when you take damage */
public record DamageToCapacityModule(IJsonPredicate<DamageSource> source, LevelingValue multiplier, boolean reduceDamage, @Nullable ModifierId owner, ModifierCondition<IToolStackView> condition) implements ModifierModule, ModifyDamageModifierHook, CapacitySourceModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<DamageToCapacityModule>defaultHooks(ModifierHooks.MODIFY_DAMAGE);
  public static final RecordLoadable<DamageToCapacityModule> LOADER = RecordLoadable.create(
    DamageSourcePredicate.LOADER.requiredField("source", DamageToCapacityModule::source),
    LevelingValue.LOADABLE.requiredField("multiplier", DamageToCapacityModule::multiplier),
    BooleanLoadable.INSTANCE.requiredField("reduce_damage", DamageToCapacityModule::reduceDamage),
    OWNER_FIELD, ModifierCondition.TOOL_FIELD,
    DamageToCapacityModule::new);

  /** @apiNote use {@link #source(IJsonPredicate)} */
  @Internal
  public DamageToCapacityModule {}

  @Override
  public RecordLoadable<DamageToCapacityModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    //  if it matches, absorb it
    if (condition.matches(tool, modifier) && this.source.matches(source)) {
      ModifierEntry barModifier = barModifier(tool, modifier);
      CapacityBarHook bar = barModifier.getHook(ModifierHooks.CAPACITY_BAR);
      // absorbed damage becomes capacity
      int capacity = bar.getCapacity(tool, barModifier);
      int current = bar.getAmount(tool);
      // once it fills though, you take the damage directly
      if (current < capacity) {
        int added = Math.min(capacity - current, Mth.ceil(amount * multiplier.compute(modifier.getEffectiveLevel())));
        bar.setAmount(tool, barModifier, current + added);
        if (reduceDamage) {
          amount -= added;
        }
      }
    }
    return amount;
  }


  /* Builder */

  /** Creates a new builder for the given predicate */
  public static Builder source(IJsonPredicate<DamageSource> source) {
    return new Builder(source);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends CapacitySourceModule.Builder<Builder> implements LevelingValue.Builder<DamageToCapacityModule>  {
    private final IJsonPredicate<DamageSource> source;
    private boolean reduceDamage = false;

    /** Sets the module to reduce damage based on the capacity gained */
    public Builder reduceDamage() {
      this.reduceDamage = true;
      return this;
    }

    @Override
    public DamageToCapacityModule amount(float flat, float eachLevel) {
      return new DamageToCapacityModule(source, new LevelingValue(flat, eachLevel), reduceDamage, owner, condition);
    }
  }
}
