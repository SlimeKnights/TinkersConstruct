package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.LootingModifierHook;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

/**
 * Modifier hooks for wearing leggings boosting loot. One as a module in case you want to only apply the bonus to leggings/not leggings
 * @deprecated use {@link HarvestEnchantmentsModifierHook} or {@link slimeknights.tconstruct.library.modifiers.hook.LootingModifierHook}
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public interface IArmorLootModifier extends HarvestEnchantmentsModifierHook, LootingModifierHook {
  /**
   * Adds harvest loot table related enchantments from this modifier's effect to the tool, called before breaking a block.
   * Needed to add enchantments for silk touch and fortune. Can add conditionally if needed. Only affects tinker tools
   * For looting, see {@link #getLootingValue(IToolStackView, int, LivingEntity, Entity, DamageSource, int)}
   * @param tool      Tool used
   * @param level     Modifier level
   * @param context   Harvest context
   * @param consumer  Consumer accepting any enchantments
   * @deprecated use {@link #applyHarvestEnchantments(IToolStackView, ModifierEntry, ToolHarvestContext, BiConsumer)}
   */
  @Deprecated
  default void applyHarvestEnchantments(IToolStackView tool, int level, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {}

  /**
   * Gets the amount to boost the tool's luck by
   * @param tool          Tool instance
   * @param level         Modifier level
   * @param holder        Entity holding the tool
   * @param target        Entity being looted
   * @param damageSource  Damage source that killed the entity. May be null if this hook is called without attacking anything (e.g. shearing)
   * @param looting          Luck value set from previous modifiers
   * @return New luck value
   * @deprecated use {@link #getLootingValue(IToolStackView, ModifierEntry, LivingEntity, Entity, DamageSource, int)}
   */
  @Deprecated
  default int getLootingValue(IToolStackView tool, int level, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    return looting;
  }


  /** New interface fallback to make transition easier */

  @Override
  default void applyHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {
    applyHarvestEnchantments(tool, modifier.getLevel(), context, consumer);
  }

  @Override
  default int getLootingValue(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    return getLootingValue(tool, modifier.getLevel(), holder, target, damageSource, looting);
  }
}
