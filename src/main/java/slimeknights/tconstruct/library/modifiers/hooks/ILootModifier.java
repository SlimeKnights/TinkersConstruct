package slimeknights.tconstruct.library.modifiers.hooks;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

/** Modifier hooks for tools with loot. One as a module in case you want to only apply the bonus to leggings/not leggings */
public interface ILootModifier {
  /** Default implementation of the hook */
  ILootModifier DEFAULT = new ILootModifier() {};
  /** Merger to combine multiple hooks into one */
  Function<Collection<ILootModifier>,ILootModifier> MERGER = SumMerger::new;

  /**
   * Adds harvest loot table related enchantments from this modifier's effect to the tool, called before breaking a block.
   * Needed to add enchantments for silk touch and fortune. Can add conditionally if needed. Only affects tinker tools
   * For looting, see {@link #getLootingValue(IToolStackView, ModifierEntry, LivingEntity, Entity, DamageSource, int)}
   * @param tool      Tool used
   * @param modifier  Modifier and level used to call this hook
   * @param context   Harvest context
   * @param consumer  Consumer accepting any enchantments
   */
  default void applyHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {}

  /**
   * Gets the amount to boost the tool's luck by
   * @param tool          Tool instance
   * @param modifier      Modifier and level used to call this hook
   * @param holder        Entity holding the tool
   * @param target        Entity being looted
   * @param damageSource  Damage source that killed the entity. May be null if this hook is called without attacking anything (e.g. shearing)
   * @param looting       Luck value set from previous modifiers
   * @return New luck value
   */
  default int getLootingValue(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    return looting;
  }

  /** Merge strategy that adds all children */
  @SuppressWarnings("ClassCanBeRecord")
  @RequiredArgsConstructor
  class SumMerger implements ILootModifier {
    private final Collection<ILootModifier> children;

    @Override
    public void applyHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {
      for (ILootModifier child : children) {
        child.applyHarvestEnchantments(tool, modifier, context, consumer);
      }
    }

    @Override
    public int getLootingValue(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
      for (ILootModifier child : children) {
        looting = child.getLootingValue(tool, modifier, holder, target, damageSource, looting);
      }
      return looting;
    }
  }
}
