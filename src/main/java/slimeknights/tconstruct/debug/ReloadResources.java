package slimeknights.tconstruct.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class ReloadResources extends CommandBase {

  @Override
  public String getCommandName() {
    return "reloadResources";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/reloadResources";
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
    if(sender.getEntityWorld().isRemote) {
      Minecraft.getMinecraft().refreshResources();
    }
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }
}
