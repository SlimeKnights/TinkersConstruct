package slimeknights.tconstruct.debug;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TestTool extends CommandBase {

  @Override
  public String getName() {
    return "testTool";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "/testTool";
  }

  @Override
  public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
      throws CommandException {
    if(sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      ItemStack item = player.inventory.getCurrentItem();
      if(item.isEmpty() || !(item.getItem() instanceof ToolCore)) {
        throw new CommandException("Hold the tinkers tool to test in your hand");
      }

      int i = 0;
      while(!ToolHelper.isBroken(item)) {
        ToolHelper.damageTool(item, 1, player);
        i++;
      }

      sender.sendMessage(new TextComponentString("Effective Durability: " + i));
    }
  }
}
