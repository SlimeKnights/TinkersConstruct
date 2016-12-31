package slimeknights.tconstruct.debug;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tools.traits.TraitProgressiveStats;

public class GetToolGrowth extends CommandBase {

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }

  @Override
  public String getName() {
    return "getToolGrowth";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "Hold tool while calling /getToolGrowth";
  }

  @Override
  public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
      throws CommandException {
    if(sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      ItemStack item = player.inventory.getCurrentItem();
      TraitProgressiveStats.StatNBT bonus = ModifierNBT.readTag(TagUtil.getTagSafe(TagUtil.getExtraTag(item), "toolgrowthStatBonus"), TraitProgressiveStats.StatNBT.class);
      TraitProgressiveStats.StatNBT pool = ModifierNBT.readTag(TagUtil.getTagSafe(TagUtil.getExtraTag(item), "toolgrowthStatPool"), TraitProgressiveStats.StatNBT.class);

      if(bonus != null) {
        String b = String.format("Applied bonus:\n  Durability: %d\n  Speed: %f\n  Attack: %f", bonus.durability, bonus.speed, bonus.attack);
        sender.sendMessage(new TextComponentString(b));
      }
      else {
        sender.sendMessage(new TextComponentString("No bonus"));
      }
      if(pool != null) {
        String p = String.format("Applied bonus:\n  Durability: %d\n  Speed: %f\n  Attack: %f", pool.durability, pool.speed, pool.attack);
        sender.sendMessage(new TextComponentString(p));
      }
      else {
        sender.sendMessage(new TextComponentString("No bonus"));
      }


    }
  }
}
