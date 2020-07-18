package slimeknights.tconstruct.tables.client.inventory.library;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.ToolCore;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

public class ToolBuildScreenInfo {

  @Nonnull
  public final ItemStack tool;

  public ItemStack toolForRendering;

  // the positions where the slots are located
  public final List<Point> positions = Lists.newArrayList();

  public ToolBuildScreenInfo() {
    // for repairing
    this.tool = ItemStack.EMPTY;
  }

  public ToolBuildScreenInfo(ItemStack tool) {
    this.tool = tool;
  }

  /**
   * Add another slot at the specified position for the tool.
   * The positions are usually located between:
   * X: 7 - 69
   * Y: 18 - 64
   */
  public void addSlotPosition(int x, int y) {
    this.positions.add(new Point(x, y));
  }

  public ItemStack getToolForRendering() {
    if (this.toolForRendering == null || this.toolForRendering.isEmpty()) {
      if (tool.getItem() instanceof ToolCore) {
        this.toolForRendering = ((ToolCore) tool.getItem()).buildToolForRendering();
      }
      else {
        this.toolForRendering = tool;
      }
    }

    return this.toolForRendering;
  }
}
