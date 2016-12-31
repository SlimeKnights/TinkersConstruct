package slimeknights.tconstruct.debug;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.utils.ToolHelper;

public class DamageTool extends CommandBase {

  @Override
  public String getName() {
    return "damageTool";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "/damageTool <amount>";
  }

  @Override
  public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
      throws CommandException {
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
