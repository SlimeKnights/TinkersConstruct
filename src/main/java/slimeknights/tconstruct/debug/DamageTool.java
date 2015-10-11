package slimeknights.tconstruct.debug;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import slimeknights.tconstruct.library.utils.ToolHelper;

public class DamageTool extends CommandBase {

  @Override
  public String getCommandName() {
    return "damageTool";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/damageTool <amount>";
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
    if(args.length != 1) {
      throw new CommandException("Invalid params");
    }
    if(sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      ItemStack item = player.inventory.getCurrentItem();
      ToolHelper.damageTool(item, Integer.valueOf(args[0]), player);
    }
  }
}
