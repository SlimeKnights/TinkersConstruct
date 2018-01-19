package slimeknights.tconstruct.debug;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class BreakTool extends CommandBase {

  @Override
  public String getName() {
    return "breakTool";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "/breakTool";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
    ItemStack tool = player.inventory.getCurrentItem();
    if(tool.isEmpty() || !(tool.getItem() instanceof ToolCore)) {
      throw new CommandException("Hold the tinkers tool to test in your hand");
    }

    ToolHelper.breakTool(tool, player);
  }

}
