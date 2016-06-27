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

  @Nonnull
  @Override
  public String getCommandName() {
    return "testTool";
  }

  @Nonnull
  @Override
  public String getCommandUsage(@Nonnull ICommandSender sender) {
    return "/testTool";
  }

  @Override
  public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
      throws CommandException {
    if(sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      ItemStack item = player.inventory.getCurrentItem();
      if(item == null || !(item.getItem() instanceof ToolCore)) {
        throw new CommandException("Hold the tinkers tool to test in your hand");
      }

      int i = 0;
      while(!ToolHelper.isBroken(item)) {
        ToolHelper.damageTool(item, 1, player);
        i++;
      }

      sender.addChatMessage(new TextComponentString("Effective Durability: " + i));
    }
  }
}
