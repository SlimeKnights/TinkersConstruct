package slimeknights.tconstruct.tools.modifiers.traits.general;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.DurabilityShieldModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public class StoneshieldModifier extends DurabilityShieldModifier {
  public StoneshieldModifier() {
    super(0xE0E9EC);
  }

  @Override
  protected int getShieldCapacity(IModifierToolStack tool, int level) {
    return (int)(level * 100 * tool.getModifier(ToolStats.DURABILITY));
  }

  @Override
  public int getPriority() {
    // higher than overslime, to ensure this is removed first
    return 175;
  }

  @Override
  public List<ItemStack> processLoot(IModifierToolStack tool, int level, List<ItemStack> generatedLoot, LootContext context) {
    Iterator<ItemStack> iterator = generatedLoot.iterator();
    int addedShield = 0;
    // 25% chance per level of consuming each stone
    float chance = level * 0.25f;
    while (iterator.hasNext()) {
      ItemStack stack = iterator.next();
      // if the item is a stone, num time
      if (TinkerTags.Items.STONESHIELDS.contains(stack.getItem())) {
        // 100% chance? just add the full count
        if (chance >= 1.0f) {
          addedShield += stack.getCount();
          iterator.remove();
        } else {
          // smaller chance, independant chance per stone
          int reduced = 0;
          for (int i = 0; i < stack.getCount(); i++) {
            if (RANDOM.nextFloat() < chance) {
              reduced++;
            }
          }
          // if we ate them all, remove, otherwise just shrink
          if (reduced == stack.getCount()) {
            iterator.remove();
          } else {
            stack.shrink(reduced);
          }
          addedShield += reduced;
        }
      }
    }

    // if we found any stone, add shield
    if (addedShield > 0) {
      // 5 stoneshield per stone eaten
      addShield(tool, level, addedShield * 5);
    }

    return generatedLoot;
  }

  /* Display */

  @Nullable
  @Override
  public Boolean showDurabilityBar(IModifierToolStack tool, int level) {
    // only show if we have any shield
    return getShield(tool) > 0 ? true : null;
  }

  @Override
  public int getDurabilityRGB(IModifierToolStack tool, int level) {
    if (getShield(tool) > 0) {
      // stoneshield shows in light grey
      return 0x7F7F7F;
    }
    return -1;
  }
}
