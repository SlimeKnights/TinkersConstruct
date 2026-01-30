package slimeknights.tconstruct.tools.modules.durability;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.predicate.TinkerPredicate;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module to set a minimum or a maximum amount of damage for a tool to take each operation.
 * @param min  Minimum amount of damage the tool can take. Use 0 for no min.
 * @param max  Maximum amount of damage the tool can take. Use {@link Integer#MAX_VALUE} for no max.
 * @param condition  Condition of when this module applies
 */
public record ToolDamageRangeModule(int min, int max, ApplyRangeWhen when, IJsonPredicate<LivingEntity> holder, ModifierCondition<IToolStackView> condition) implements ModifierModule, ToolDamageModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ToolDamageRangeModule>defaultHooks(ModifierHooks.TOOL_DAMAGE);
  public static final RecordLoadable<ToolDamageRangeModule> LOADER = RecordLoadable.create(
    IntLoadable.FROM_ZERO.defaultField("min", 0, ToolDamageRangeModule::min),
    IntLoadable.FROM_ZERO.defaultField("max", Integer.MAX_VALUE, ToolDamageRangeModule::max),
    new EnumLoadable<>(ApplyRangeWhen.class).defaultField("apply_when", ApplyRangeWhen.ALWAYS, ToolDamageRangeModule::when),
    LivingEntityPredicate.LOADER.defaultField("holder", ToolDamageRangeModule::holder),
    ModifierCondition.TOOL_FIELD,
    ToolDamageRangeModule::new);

  public ToolDamageRangeModule(int min, int max, ApplyRangeWhen when) {
    this(min, max, when, LivingEntityPredicate.ANY, ModifierCondition.ANY_TOOL);
  }

  @Override
  public RecordLoadable<ToolDamageRangeModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
    return onDamageTool(tool, modifier, amount, holder, null, false);
  }

  @Override
  public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder, @Nullable ItemStack stack, boolean secondary) {
    if (condition.matches(tool, modifier) && (when == ApplyRangeWhen.ALWAYS || secondary && when == ApplyRangeWhen.SECONDARY) && TinkerPredicate.matches(this.holder, holder)) {
      // if the range is closed, all actions deal that
      if (min == max) {
        return min;
      }
      // ensure amount is in the range
      return Mth.clamp(amount, min, max);
    }
    return amount;
  }

  public enum ApplyRangeWhen {
    /** Range is always applied */
    ALWAYS,
    /** Range is only applied to primary damage sources */
    PRIMARY,
    /** Range is only applied to secondary damage sources */
    SECONDARY;
  }
}
