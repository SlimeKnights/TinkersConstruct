package slimeknights.tconstruct.library.tools.helper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStack.TooltipDisplayFlags;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
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
   * @param tool     Tool instance
   * @param stack    Base stack instance
   * @param context  Tool harvest context
   * @return  True if enchants were applied
   */
  public static boolean applyHarvestEnchants(ToolStack tool, ItemStack stack, ToolHarvestContext context) {
    boolean addedEnchants = false;
    PlayerEntity player = context.getPlayer();
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
        entry.getModifier().applyHarvestEnchantments(tool, entry.getLevel(), context, enchantmentConsumer);
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

  /**
   * Gets the looting value for the given tool
   * @param tool           Tool used
   * @param holder         Entity holding the tool
   * @param target         Target being looted
   * @param damageSource   Damage source for looting, may ben null if no attack
   * @return  Looting value for the tool
   */
  public static int getLootingLevel(IModifierToolStack tool, LivingEntity holder, Entity target, @Nullable DamageSource damageSource) {
    if (tool.isBroken()) {
      return 0;
    }
    int looting = 0;
    for (ModifierEntry entry : tool.getModifierList()) {
      looting = entry.getModifier().getLootingValue(tool, entry.getLevel(), holder, target, damageSource, looting);
    }
    return looting;
  }

  /**
   * Validates that the tool is still valid after removing the given modifiers.
   * Alternative to calling {@link ToolStack#validate()} when a list of modifiers are being removed.
   * @param tool              Tool
   * @param modifiersToCheck  List of modifiers to check, only runs validate on any modifiers not currently on the tool
   * @return  Validated result, either pass or an error
   */
  public static ValidatedResult validateRemovedModifiers(ToolStack tool, List<ModifierEntry> modifiersToCheck) {
    // first try validating the tool
    ValidatedResult toolResult = tool.validate();
    if (toolResult.hasError()) {
      return toolResult;
    }

    // validate all removed traits
    for (ModifierEntry entry : modifiersToCheck) {
      Modifier modifier = entry.getModifier();
      if (tool.getModifierLevel(modifier) == 0) {
        ValidatedResult result = modifier.validate(tool, 0);
        if (result.hasError()) {
          return result;
        }
      }
    }
    return ValidatedResult.PASS;
  }
}
