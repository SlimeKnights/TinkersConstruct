package slimeknights.tconstruct.debug;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.ItemHandlerHelper;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.tools.modifiers.ModFortify;

public class GenValidModifiers extends CommandBase {

  @Override
  public String getCommandName() {
    return "genValidModifiers";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "Hold tool while calling /genValidModifiers to generate all modified variants";
  }

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    if(sender.getCommandSenderEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
      ItemStack item = player.inventory.getCurrentItem();

      sender.addChatMessage(new TextComponentString(item.getDisplayName() + " accepts the following modifiers:"));
      for(IModifier mod : TinkerRegistry.getAllModifiers()) {
        if(!mod.hasItemsToApplyWith()) {
          continue;
        }
        try {
          if((mod instanceof ModifierTrait || !(mod instanceof AbstractTrait)) && mod.canApply(item.copy(), item) && (mod.getIdentifier().equals("fortified") || !(mod instanceof ModFortify))) {
            ItemStack copy = item.copy();
            mod.apply(copy);
            ItemHandlerHelper.giveItemToPlayer(player, copy);
          }
        }
        catch (TinkerGuiException e) {
          // do
        }
      }
    }
  }

}
