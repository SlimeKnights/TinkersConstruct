package slimeknights.tconstruct.library.modifiers.hook;

import net.minecraft.world.item.enchantment.Enchantment;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;

/** Modifier hook implementing bonus enchantments from a tool, applied directly before block break. Can implement separately for leggings and tools if desired via the different hooks */
public interface HarvestEnchantmentsModifierHook {
  HarvestEnchantmentsModifierHook EMPTY = (tool, modifier, context, consumer) -> {};

  /** Merger that runs all submodules */
  Function<Collection<HarvestEnchantmentsModifierHook>,HarvestEnchantmentsModifierHook> ALL_MERGER = AllMerger::new;

  /**
   * Adds harvest loot table related enchantments from this modifier's effect to the tool, called before breaking a block.
   * Needed to add enchantments for silk touch and fortune. Can add conditionally if needed. Only affects tinker tools
   * For looting, see {@link LootingModifierHook}
   * @param tool      Tool used
   * @param modifier  Modifier used
   * @param context   Harvest context
   * @param consumer  Consumer accepting any enchantments
   */
  void applyHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer);


  /** Merger that runs all submodules */
  record AllMerger(Collection<HarvestEnchantmentsModifierHook> modules) implements HarvestEnchantmentsModifierHook {
    @Override
    public void applyHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {
      for (HarvestEnchantmentsModifierHook module : modules) {
        module.applyHarvestEnchantments(tool, modifier, context, consumer);
      }
    }
  }
}
