package slimeknights.tconstruct.library.modifiers.hooks;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

/** Modifier hooks for wearing leggings boosting loot. One as a module in case you want to only apply the bonus to leggings/not leggings */
public interface ILeggingLootModifier {
  /**
   * Adds harvest loot table related enchantments from this modifier's effect to the tool, called before breaking a block.
   * Needed to add enchantments for silk touch and fortune. Can add conditionally if needed. Only affects tinker tools
   * For looting, see {@link #getLootingValue(IModifierToolStack, int, LivingEntity, Entity, DamageSource, int)}
   * @param tool      Tool used
   * @param level     Modifier level
   * @param context   Harvest context
   * @param consumer  Consumer accepting any enchantments
   */
  default void applyHarvestEnchantments(IModifierToolStack tool, int level, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {}

  /**
   * Gets the amount to boost the tool's luck by
   * @param tool          Tool instance
   * @param level         Modifier level
   * @param holder        Entity holding the tool
   * @param target        Entity being looted
   * @param damageSource  Damage source that killed the entity. May be null if this hook is called without attacking anything (e.g. shearing)
   * @param looting          Luck value set from previous modifiers
   * @return New luck value
   */
  default int getLootingValue(IModifierToolStack tool, int level, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    return looting;
  }
}
