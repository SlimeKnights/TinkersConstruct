package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

import static slimeknights.tconstruct.library.tools.helper.ToolInteractionUtil.damageTool;

/**
 * Because, really, there's way too much stuff that handles breaking/unbreaking and broken tools.
 */
public class ToolBreakUtil {

  public static void healTool(ItemStack stack, int amount, LivingEntity entity) {
    damageTool(stack, -amount, entity);
  }

  public static void breakTool(ItemStack stack) {
    ToolData toolData = ToolData.from(stack);
    if(!toolData.getStats().broken) {
      ToolData newData = toolData.createNewDataWithBroken(true);
      stack.setTag(newData.serializeToNBT());
    }
  }

  public static void unbreakTool(ItemStack stack) {
    ToolData toolData = ToolData.from(stack);
    if (toolData.getStats().broken) {
      // ensure correct damage value
      stack.setDamage(stack.getMaxDamage());

      // setItemDamage might break the tool again, so we do this afterwards
      ToolData newData = toolData.createNewDataWithBroken(false);
      stack.setTag(newData.serializeToNBT());
    }
  }

  public static void repairTool(ItemStack stack, int amount) {
    // entity is optional, only needed for rendering break effect, never needed when repairing
    repairTool(stack, amount, null);
  }

  public static void repairTool(ItemStack stack, int amount, LivingEntity entity) {
    unbreakTool(stack);

    // todo: fire tool event
    //TinkerToolEvent.OnRepair.fireEvent(stack, amount);

    healTool(stack, amount, entity);
  }
}
