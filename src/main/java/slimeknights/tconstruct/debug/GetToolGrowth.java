package slimeknights.tconstruct.debug;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tools.modifiers.traits.TraitProgressiveStats;

public class GetToolGrowth extends CommandBase {

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }

  @Override
  public String getCommandName() {
    return "getToolGrowth";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "Hold tool while calling /getToolGrowth";
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
    if(sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      ItemStack item = player.inventory.getCurrentItem();
      TraitProgressiveStats.StatNBT bonus = ModifierNBT.readTag(TagUtil.getTagSafe(TagUtil.getExtraTag(item), "toolgrowthStatBonus"), TraitProgressiveStats.StatNBT.class);
      TraitProgressiveStats.StatNBT pool = ModifierNBT.readTag(TagUtil.getTagSafe(TagUtil.getExtraTag(item), "toolgrowthStatPool"), TraitProgressiveStats.StatNBT.class);

      if(bonus != null) {
        String b = String.format("Applied bonus:\n  Durability: %d\n  Speed: %f\n  Attack: %f", bonus.durability, bonus.speed, bonus.attack);
        sender.addChatMessage(new ChatComponentText(b));
      }
      else {
        sender.addChatMessage(new ChatComponentText("No bonus"));
      }
      if(pool != null) {
        String p = String.format("Applied bonus:\n  Durability: %d\n  Speed: %f\n  Attack: %f", pool.durability, pool.speed, pool.attack);
        sender.addChatMessage(new ChatComponentText(p));
      }
      else {
        sender.addChatMessage(new ChatComponentText("No bonus"));
      }


    }
  }
}
