package slimeknights.tconstruct.library.modifiers.modules.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.data.GenericIntSerializer;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.LootingModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module for increasing the looting level, used for tools, on pants, and from bows
 * Currently, does not support incremental.
 */
public record LootingModule(int level) implements LootingModifierHook, ModifierModule {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.TOOL_LOOTING, TinkerHooks.LEGGINGS_LOOTING, TinkerHooks.PROJECTILE_LOOTING);
  public static IGenericLoader<LootingModule> LOADER = new GenericIntSerializer<>("level", LootingModule::new, LootingModule::level);

  @Override
  public int getLootingValue(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    return level * modifier.getLevel();
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }
}
