package slimeknights.tconstruct.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;

public class ReloadResources extends CommandBase {

  @Nonnull
  @Override
  public String getCommandName() {
    return "reloadResources";
  }

  @Nonnull
  @Override
  public String getCommandUsage(@Nonnull ICommandSender sender) {
    return "/reloadResources";
  }

  @Override
  public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
      throws CommandException {
    if(sender.getEntityWorld().isRemote) {
      Minecraft.getMinecraft().refreshResources();
    }
  }

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }
}
