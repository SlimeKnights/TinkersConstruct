package slimeknights.tconstruct.tools;

import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolDefinition;

import java.util.List;

public class TestTool extends ToolCore {

  public TestTool(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public void getTooltip(ItemStack stack, List<String> tooltips) {

  }
}
