package slimeknights.tconstruct.library.modifiers.hook.behavior;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

/**
 * This interface exposes two methods, {@link #updateEnchantmentLevel(IToolStackView, ModifierEntry, Holder, int)} and {@link #updateEnchantments(IToolStackView, ModifierEntry, Map)}
 * to allow tools to claim to have enchantments to vanilla APIs without modifying NBT. For performance reasons we don't simply have one hook call the other, but their behavior must be consistent.
 * That is, whatever change you make to the level in {@link #updateEnchantmentLevel(IToolStackView, ModifierEntry, Holder, int)} must also be reflected in the map in {@link #updateEnchantments(IToolStackView, ModifierEntry, Map)}.
 */
public interface EnchantmentModifierHook {
  /** Predicate to remove unneeded values from the map */
  Predicate<Integer> VALUE_REMOVER = value -> value == null || value <= 0;

  /**
   * Gets the enchantment level for the given tool
   * @param tool         Tool instance
   * @param modifier     Modifier instance
   * @param enchantment  Enchantment being queried
   * @param level        Level before this enchantment makes any changes. May be negative, will be capped to 0+ after the hook runs.
   * @return Enchantment level, typically added to {@code level} instead of replacing it. May be negative, will be capped to 0+ after the hook runs.
   */
  int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Holder<Enchantment> enchantment, int level);

  /**
   * Adds all enchantment modifications made by this tool to the map.
   * You may reduce the value in the map to a negative, all values 0 or less will be removed after the hook runs.
   * @param tool      Tool instance
   * @param modifier  Modifier instance
   * @param map       A mutable map to add enchantments from this modifier. May contain negatives.
   * @see #addEnchantment(Map, Enchantment, int)
   */
  void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map);

  /** Adds the given enchantment to the map */
  static void addEnchantment(Map<Enchantment,Integer> map, Enchantment enchantment, int amount) {
    if (amount != 0) {
      map.put(enchantment, map.getOrDefault(enchantment, 0) + amount);
    }
  }

  /**
   * Gets the enchantment level for the given tool
   * @param stack        Item stack instance
   * @param enchantment  Enchantment to query
   * @return  Enchantment level
   */
  static int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment) {
    int level = 0;
    for (Entry<Holder<Enchantment>> entry : EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet()) {
      if (entry.getKey().is(enchantment)) {
        level = entry.getIntValue();
        break;
      }
    }
    IToolStackView tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      level = entry.getHook(ModifierHooks.ENCHANTMENTS).updateEnchantmentLevel(tool, entry, enchantment, level);
    }
    // we allow hooks to return negative, such as to cancel out an enchantment
    return Math.max(level, 0);
  }

  /**
   * Gets all enchantments on the given stack
   * @param stack  Stack instance
   * @return  All contained enchantments
   */
  static Map<Enchantment,Integer> getAllEnchantments(ItemStack stack) {
    Map<Enchantment,Integer> enchantments = new java.util.HashMap<>();
    ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
    for (Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
      enchantments.put(entry.getKey().value(), entry.getIntValue());
    }
    IToolStackView tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      entry.getHook(ModifierHooks.ENCHANTMENTS).updateEnchantments(tool, entry, enchantments);
    }
    // we allow hooks to return negative, such as to cancel out an enchantment
    enchantments.values().removeIf(VALUE_REMOVER);
    return enchantments;
  }

  /** Gets all enchantments on the given stack as the 1.21 component type. */
  static ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
    Map<Enchantment,Integer> enchantments = getAllEnchantments(stack);
    ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
    for (Map.Entry<Enchantment,Integer> entry : enchantments.entrySet()) {
      lookup.listElements()
            .filter(holder -> holder.value() == entry.getKey())
            .findFirst()
            .ifPresent(holder -> mutable.set(holder, entry.getValue()));
    }
    return mutable.toImmutable();
  }

  /** Merger that combines all modules together */
  record AllMerger(Collection<EnchantmentModifierHook> modules) implements EnchantmentModifierHook {
    @Override
    public int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Holder<Enchantment> enchantment, int level) {
      for (EnchantmentModifierHook module : modules) {
        level = module.updateEnchantmentLevel(tool, modifier, enchantment, level);
      }
      return level;
    }

    @Override
    public void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map) {
      for (EnchantmentModifierHook module : modules) {
        module.updateEnchantments(tool, modifier, map);
      }
    }
  }

  /**
   * Simple implementation of this hook with just a single enchantment
   */
  @SuppressWarnings("unused") // API
  interface SingleEnchantment extends EnchantmentModifierHook {
    /**
     * Gets the enchantment this hook adds to the tool
     * @param tool      Tool instance
     * @param modifier  Modifier instance
     * @return  Enchantment for this hook to add
     */
    Enchantment getEnchantment(IToolStackView tool, ModifierEntry modifier);

    /**
     * Gets the level of the enchantment to add
     * @param tool      Tool instance
     * @param modifier  Modifier instance
     * @return  Level of the enchantment to add
     */
    default int getEnchantmentLevel(IToolStackView tool, ModifierEntry modifier) {
      return modifier.getLevel();
    }

    @Override
    default int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Holder<Enchantment> enchantment, int level) {
      if (enchantment.value() == getEnchantment(tool, modifier)) {
        level += getEnchantmentLevel(tool, modifier);
      }
      return level;
    }

    @Override
    default void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map) {
      addEnchantment(map, getEnchantment(tool, modifier), getEnchantmentLevel(tool, modifier));
    }
  }

  /** Combination of {@link SingleEnchantment} with {@link BlockHarvestModifierHook.MarkHarvesting} */
  interface SingleHarvestEnchantment extends SingleEnchantment, BlockHarvestModifierHook.MarkHarvesting {
    @Override
    default int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Holder<Enchantment> enchantment, int level) {
      if (BlockHarvestModifierHook.MarkHarvesting.isHarvesting(tool)) {
        return SingleEnchantment.super.updateEnchantmentLevel(tool, modifier, enchantment, level);
      }
      return level;
    }

    @Override
    default void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map) {
      if (BlockHarvestModifierHook.MarkHarvesting.isHarvesting(tool)) {
        SingleEnchantment.super.updateEnchantments(tool, modifier, map);
      }
    }
  }
}
