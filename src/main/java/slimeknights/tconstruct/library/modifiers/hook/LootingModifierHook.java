package slimeknights.tconstruct.library.modifiers.hook;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Function;

/**
 * Modifier hooks for wearing leggings boosting loot. One as a module in case you want to only apply the bonus to leggings/not leggings
 * TODO 1.19: move to {@link slimeknights.tconstruct.library.modifiers.hook.combat}
 */
public interface LootingModifierHook {
  /** Default behavior for the looting modifier */
  LootingModifierHook DEFAULT = (tool, entry, holder, target, damageSource, looting) -> looting;

  /** Constructor for a merger that sums all children */
  Function<Collection<LootingModifierHook>, LootingModifierHook> SUM_MERGER = SumMerger::new;


  /**
   * Gets the amount to boost the tool's luck by
   * @param tool          Tool instance
   * @param modifier      Modifier entry calling the hook
   * @param holder        Entity holding the tool
   * @param target        Entity being looted
   * @param damageSource  Damage source that killed the entity. May be null if this hook is called without attacking anything (e.g. shearing)
   * @param looting       Looting value set from previous modifiers
   * @return New looting value
   */
  int getLootingValue(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting);

  /**
   * Gets the looting value from the given tool
   * @param hook           Hook to call
   * @param tool           Tool instance
   * @param holder         Entity holding the tool
   * @param target         Target of the attack
   * @param damageSource   Damage used to attack
   * @param looting        Previous looting value
   * @return  New looting value
   */
  static int getLootingValue(ModifierHook<LootingModifierHook> hook, IToolStackView tool, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    for (ModifierEntry entry : tool.getModifierList()) {
      looting = entry.getHook(hook).getLootingValue(tool, entry, holder, target, damageSource, looting);
    }
    return looting;
  }

  /** Constructor for a merger that sums all children */
  record SumMerger(Collection<LootingModifierHook> modules) implements LootingModifierHook {
    @Override
    public int getLootingValue(IToolStackView tool, ModifierEntry entry, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
      for (LootingModifierHook module : modules) {
        looting = module.getLootingValue(tool, entry, holder, target, damageSource, looting);
      }
      return looting;
    }
  }
}
