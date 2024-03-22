package slimeknights.tconstruct.library.modifiers.modules.armor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModuleCondition.ConditionalModifierModule;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module to block damage of the passed sources
 * @param source  Predicate of sources to block
 */
public record BlockDamageSourceModule(IJsonPredicate<DamageSource> source, ModifierModuleCondition condition) implements DamageBlockModifierHook, ModifierModule, ConditionalModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.DAMAGE_BLOCK);
  public static final RecordLoadable<BlockDamageSourceModule> LOADER = RecordLoadable.create(
    DamageSourcePredicate.LOADER.field("damage_source", BlockDamageSourceModule::source),
    ModifierModuleCondition.FIELD,
    BlockDamageSourceModule::new);

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
    return condition.matches(tool, modifier) && this.source.matches(source);
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }
  

  /* Builder */

  public static Builder source(IJsonPredicate<DamageSource> source) {
    return new Builder(source);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModifierModuleCondition.Builder<Builder> {
    private final IJsonPredicate<DamageSource> source;

    public BlockDamageSourceModule build() {
      return new BlockDamageSourceModule(source, condition);
    }
  }
}
