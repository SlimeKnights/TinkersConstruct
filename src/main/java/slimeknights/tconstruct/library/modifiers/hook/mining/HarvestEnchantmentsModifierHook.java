package slimeknights.tconstruct.library.modifiers.hook.mining;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.LootingModifierHook;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/** Modifier hook implementing bonus enchantments from a tool, applied directly before block break. Can implement separately for leggings and tools if desired via the different hooks */
public interface HarvestEnchantmentsModifierHook {
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


  /* Helpers */

  /** Vanilla enchantments tag */
  String TAG_ENCHANTMENTS = "Enchantments";

  /**
   * Adds all enchantments from tools. Separate method as tools don't have enchants all the time.
   * Typically called before actions which involve loot, such as breaking blocks or attacking mobs.
   * @param tool     Tool instance
   * @param stack    Base stack instance
   * @param context  Tool harvest context
   * @return  Old tag if enchants were applied
   */
  @Nullable
  static ListTag applyHarvestEnchantments(ToolStack tool, ItemStack stack, ToolHarvestContext context) {
    ListTag originalEnchants = null;
    Player player = context.getPlayer();
    if (player == null || !player.isCreative()) {
      Map<Enchantment, Integer> enchantments = new HashMap<>();
      BiConsumer<Enchantment,Integer> enchantmentConsumer = (ench, add) -> {
        if (ench != null && add != null) {
          Integer level = enchantments.get(ench);
          if (level != null) {
            add += level;
          }
          enchantments.put(ench, add);
        }
      };
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getHook(TinkerHooks.TOOL_HARVEST_ENCHANTMENTS).applyHarvestEnchantments(tool, entry, context, enchantmentConsumer);
      }
      // lucky pants
      if (player != null) {
        ItemStack pants = player.getItemBySlot(EquipmentSlot.LEGS);
        if (pants.is(TinkerTags.Items.LEGGINGS)) {
          ToolStack pantsTool = ToolStack.from(pants);
          for (ModifierEntry entry : pantsTool.getModifierList()) {
            entry.getHook(TinkerHooks.LEGGINGS_HARVEST_ENCHANTMENTS).applyHarvestEnchantments(pantsTool, entry, context, enchantmentConsumer);
          }
        }
      }
      if (!enchantments.isEmpty()) {
        // note this returns a new list if there is no tag, this is intentional as we need non-null to tell the tool to remove the tag
        originalEnchants = stack.getEnchantmentTags();
        EnchantmentHelper.setEnchantments(enchantments, stack);
      }
    }
    return originalEnchants;
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
    public void applyHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {
      for (HarvestEnchantmentsModifierHook module : modules) {
        module.applyHarvestEnchantments(tool, modifier, context, consumer);
      }
    }
  }
}
