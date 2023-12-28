package slimeknights.tconstruct.library.modifiers.hook.display;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.Collection;

public interface DurabilityDisplayModifierHook {
  /**
   * Override the default tool logic for showing the durability bar.
   * TODO 1.19: Switch to boolean return type, disallow a modifier from hiding the durability bar when the someone else wishes to show it.
   * @param tool      Tool instance
   * @param modifier  Modifier running the hook
   * @return  True forces the bar to show, false forces it to hide. Return null defer to later modifiers.
   */
  @Nullable
  Boolean showDurabilityBar(IToolStackView tool, ModifierEntry modifier);

  /**
   * Gets the damage percentage for display.  First tool returning something other than NaN will determine display durability
   * @param tool      Tool instance
   * @param modifier  Modifier running the hook
   * @return  Width of the durability bar between 1 and 13 with 13 being full. Return 0 to indicate you have nothing to display (falling back to the next hook).
   */
  int getDurabilityWidth(IToolStackView tool, ModifierEntry modifier);

  /**
   * Gets the RGB for the durability bar
   * @param tool      Tool instance
   * @param modifier  Modifier running the hook
   * @return  Color integer in the format of 0xRRGGBB, or -1 to not handle it
   */
  int getDurabilityRGB(IToolStackView tool, ModifierEntry modifier);


  /* Helpers */

  /** Checks if the durability bar should show for the given tool stack */
  static boolean showDurabilityBar(ItemStack stack) {
    // don't show durability bar if the tool does not support durability
    // we don't use that feature in the base mod, but Tinkers' Things notably has a tool that uses it
    if (!stack.getItem().canBeDepleted() || !stack.is(TinkerTags.Items.DURABILITY)) {
      return false;
    }

    // poll modifiers to see if any think the bar should show or should not show
    ToolStack tool = ToolStack.from(stack);
    for (ModifierEntry entry : tool.getModifierList()) {
      Boolean show = entry.getHook(TinkerHooks.DURABILITY_DISPLAY).showDurabilityBar(tool, entry);
      if (show != null) {
        return show;
      }
    }
    return tool.getItem().canBeDepleted() && tool.getDamage() > 0;
  }

  /**
   * Gets the width to display for the given amount.
   * @param amount  Amount between 0 and max. Larger values mean more durability/width
   * @param max     Maximum amount
   * @return  Width to display.
   */
  static int getWidthFor(int amount, int max) {
    // empty? fallback to next option
    if (amount <= 0) {
      return 0;
    }
    // full? display the max amount
    if (amount >= max) {
      return 13;
    }
    // we never display an empty bar unless its broken, this value will be betwen 1 and 13
    // (amount-1)/max is strictly less than 1, meaning the range of last part of the equation is 0-12
    // it is chosen to put an even 1/12 of the width in each possible pixel value
    // (compare to vanilla, which has 1/13 each at pixels 1-12, 1/26 at 0, and 1/26 at 13
    return 1 + (13 * (amount - 1) / max);
  }

  /**
   * Gets the durability to display on the stack durability bar
   * @param stack  Stack instance
   * @return  Durability width between 0 and 13
   */
  static int getDurabilityWidth(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return 0;
    }
    // poll each modifier for an override
    for (ModifierEntry entry : tool.getModifierList()) {
      int width = entry.getHook(TinkerHooks.DURABILITY_DISPLAY).getDurabilityWidth(tool, entry);
      if (width > 0) {
        return width;
      }
    }
    // if no override, default to vanilla logic
    int max = tool.getStats().getInt(ToolStats.DURABILITY);
    return getWidthFor(max - tool.getDamage(), max);
  }

  /**
   * Gets the RGB to display durability at
   * @param stack  Stack instance
   * @return  RGB value
   */
  static int getDurabilityRGB(ItemStack stack) {
    ToolStack tool = ToolStack.from(stack);
    // if the tool is broken, it has a 0 width bar, no other way to achieve 0 width bar
    // 0 width is not even visible, so save some effort and call it black
    if (tool.isBroken()) {
      return 0;
    }

    // first modifier who wishs to handle it wins
    for (ModifierEntry entry : tool.getModifierList()) {
      int rgb = entry.getHook(TinkerHooks.DURABILITY_DISPLAY).getDurabilityRGB(tool, entry);
      // not a problem to check against -1, the top 16 bits are unused so no one should be using it
      if (rgb != -1) {
        return rgb;
      }
    }
    // so no modifier changed the color, default to the vanilla green to red scale.
    // Using raw durability here instead of the hook, if you want to change the displayed durability its your job to change the colors as well
    float max = tool.getStats().getInt(ToolStats.DURABILITY);
    return Mth.hsvToRgb(Math.max(0.0f, (max - tool.getDamage()) / max / 3.0f), 1.0f, 1.0f);
  }


  /** Merger that runs the first hook that returns a non-default value */
  record FirstMerger(Collection<DurabilityDisplayModifierHook> modules) implements DurabilityDisplayModifierHook {
    @Nullable
    @Override
    public Boolean showDurabilityBar(IToolStackView tool, ModifierEntry modifier) {
      for (DurabilityDisplayModifierHook module : modules) {
        Boolean result = module.showDurabilityBar(tool, modifier);
        if (result != null) {
          return result;
        }
      }
      return null;
    }

    @Override
    public int getDurabilityWidth(IToolStackView tool, ModifierEntry modifier) {
      for (DurabilityDisplayModifierHook module : modules) {
        int damage = module.getDurabilityWidth(tool, modifier);
        if (damage > 0) {
          return damage;
        }
      }
      return -1;
    }

    @Override
    public int getDurabilityRGB(IToolStackView tool, ModifierEntry modifier) {
      for (DurabilityDisplayModifierHook module : modules) {
        int color = module.getDurabilityRGB(tool, modifier);
        if (color != -1) {
          return color;
        }
      }
      return -1;
    }
  }
}
