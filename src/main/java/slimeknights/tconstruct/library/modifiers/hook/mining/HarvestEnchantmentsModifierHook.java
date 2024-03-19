package slimeknights.tconstruct.library.modifiers.hook.mining;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.LootingModifierHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

/**
 * Modifier hook implementing bonus enchantments from a tool, applied directly before block break.
 * Does not run on the main hand as there is a more efficient approach, see {@link EnchantmentModifierHook.SingleHarvestEnchantment} for an example of how to do that.
 * @see LootingModifierHook
 */
public interface HarvestEnchantmentsModifierHook {
  /** Slots that can use this hook, for mainhand its more efficient to instead use {@link BlockHarvestModifierHook} and {@link slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook} */
  EquipmentSlot[] APPLICABLE_SLOTS = { EquipmentSlot.OFFHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

  /**
   * Adds harvest loot table related enchantments from this modifier's effect to the tool, called before breaking a block.
   * Needed to add enchantments for silk touch and fortune. Can add conditionally if needed. Only affects tinker tools
   * @param tool       Tool used
   * @param modifier   Modifier used
   * @param context    Harvest context
   * @param equipment  Context for other equipment on the player
   * @param slot       Slot being checked for harvest enchantments
   * @param map        A mutable map to add enchantments from this modifier. May contain negatives.
   * @see EnchantmentModifierHook#addEnchantment(Map, Enchantment, int)
   * @see EnchantmentModifierHook.SingleHarvestEnchantment
   */
  void updateHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, EquipmentContext equipment,EquipmentSlot slot, Map<Enchantment,Integer> map);


  /* Helpers */

  /** Vanilla enchantments tag */
  String TAG_ENCHANTMENTS = "Enchantments";

  /**
   * Adds all enchantments from tools. Separate method as tools don't have enchants all the time.
   * Typically called before actions which involve loot, such as breaking blocks or attacking mobs.
   * @param tool     Tool in the main hand
   * @param stack    Base stack instance
   * @param context  Tool harvest context
   * @return  Old tag if enchants were applied
   */
  @Nullable
  static ListTag updateHarvestEnchantments(IToolStackView tool, ItemStack stack, ToolHarvestContext context) {
    Player player = context.getPlayer();
    if (player == null || !player.isCreative()) {
      // assuming we have a modifiable tool, we iterate all tools other than the main hand (since the main hand is in charge of harvesting the blocks)
      EquipmentContext equipmentContext = EquipmentContext.withTool(context.getLiving(), tool, EquipmentSlot.MAINHAND);
      // lazily parse the enchantment map, wait until someone has a hook
      ListTag originalEnchants = null;
      Map<Enchantment,Integer> enchantments = null;
      for (EquipmentSlot slot : APPLICABLE_SLOTS) {
        // tool must be modifiable and must be in an appropriate slot, or we don't care
        // we also disallow harvest tools, this means no pickaxe in the offhand granting you pickaxe stuff in the main hand, but something like a shield fine
        IToolStackView armor = equipmentContext.getToolInSlot(slot);
        if (armor != null && ModifierUtil.validArmorSlot(armor, slot)) {
          for (ModifierEntry entry : armor.getModifierList()) {
            // skip processing if we lack the hook, saves us parsing if none of the modifiers use it
            HarvestEnchantmentsModifierHook hook = entry.getModifier().getHooks().getOrNull(TinkerHooks.HARVEST_ENCHANTMENTS);
            if (hook != null) {
              // if we have not yet parsed the enchantments, time to do so
              if (enchantments == null) {
                originalEnchants = stack.getEnchantmentTags();
                enchantments = EnchantmentHelper.deserializeEnchantments(originalEnchants);
              }
              hook.updateHarvestEnchantments(armor, entry, context, equipmentContext, slot, enchantments);
            }
          }
        }
      }
      // if the enchantments is null, no hooks ran so the enchantments are unchanged
      if (enchantments != null) {
        // we allow 0 values for enchantments in the hook
        enchantments.values().removeIf(EnchantmentModifierHook.VALUE_REMOVER);
        EnchantmentHelper.setEnchantments(enchantments, stack);
        return originalEnchants;
      }
    }
    return null;
  }

  /**
   * Restores the original enchants to the given stack
   * @param stack        Stack to clear enchants
   * @param originalTag  Original list of enchantments. If empty, will remove the tag
   */
  static void restoreEnchantments(ItemStack stack, ListTag originalTag) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null) {
      if (originalTag.isEmpty()) {
        nbt.remove(TAG_ENCHANTMENTS);
      } else {
        nbt.put(TAG_ENCHANTMENTS, originalTag);
      }
    }
  }


  /** Merger that runs all submodules */
  record AllMerger(Collection<HarvestEnchantmentsModifierHook> modules) implements HarvestEnchantmentsModifierHook {
    @Override
    public void updateHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, EquipmentContext equipment, EquipmentSlot slot, Map<Enchantment,Integer> map) {
      for (HarvestEnchantmentsModifierHook module : modules) {
        module.updateHarvestEnchantments(tool, modifier, context, equipment, slot, map);
      }
    }
  }
}
