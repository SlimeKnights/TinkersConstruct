package slimeknights.tconstruct.library.tools.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStack.TooltipDisplayFlags;
import net.minecraft.nbt.CompoundNBT;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/** Generic modifier hooks that don't quite fit elsewhere */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModifierUtil {
  private static final String TAG_ENCHANTMENTS = "Enchantments";
  private static final String TAG_HIDE_FLAGS = "HideFlags";

  /**
   * Adds all enchantments from tools. Separate method as tools don't have enchants all the time.
   * Typically called before actions which involve loot, such as breaking blocks or attacking mobs.
   * @param tool    Tool instance
   * @param stack   Base stack instance
   * @param player  Player instance, just used for creative check
   * @return  True if enchants were applied
   */
  public static boolean applyEnchantments(ToolStack tool, ItemStack stack, @Nullable PlayerEntity player) {
    boolean addedEnchants = false;
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
        entry.getModifier().applyEnchantments(tool, entry.getLevel(), enchantmentConsumer);
      }
      if (!enchantments.isEmpty()) {
        addedEnchants = true;
        EnchantmentHelper.setEnchantments(enchantments, stack);
        stack.getOrCreateTag().putInt(TAG_HIDE_FLAGS, TooltipDisplayFlags.ENCHANTMENTS.func_242397_a());
      }
    }
    return addedEnchants;
  }

  /**
   * Clears enchants from the given stack
   * @param stack  Stack to clear enchants
   */
  public static void clearEnchantments(ItemStack stack) {
    CompoundNBT nbt = stack.getTag();
    if (nbt != null) {
      nbt.remove(TAG_ENCHANTMENTS);
      nbt.remove(TAG_HIDE_FLAGS);
    }
  }

}
