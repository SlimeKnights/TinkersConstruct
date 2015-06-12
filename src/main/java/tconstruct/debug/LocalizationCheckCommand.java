package tconstruct.debug;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

import java.util.List;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tinkering.Material;

public class LocalizationCheckCommand extends CommandBase {

  @Override
  public String getCommandName() {
    return "checkLocalizationStrings";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "/" + getCommandName() + " [Material-Identifier]";
  }

  @Override
  public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
    if (args.length != 1) {
      return null;
    }

    List<String> completions = Lists.newLinkedList();
    String matName = args[0].toLowerCase();
    for(Material mat : TinkerRegistry.getAllMaterials()) {
      if(mat.identifier.toLowerCase().startsWith(matName)) {
        completions.add(mat.identifier);
      }
    }

    return completions;
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) throws CommandException {
    if (args.length > 1) {
      throw new WrongUsageException(getCommandUsage(sender));
    }

    if (args.length > 0) {
      Material mat = TinkerRegistry.getMaterial(args[0]);
      if (mat == Material.UNKNOWN) {
        throw new CommandException("Unknown material: " + args[0]);
      }

      scanMaterial(mat, sender);
    } else {
      for (Material mat : TinkerRegistry.getAllMaterials()) {
        scanMaterial(mat, sender);
      }
    }
  }

  private void scanMaterial(Material material, ICommandSender sender) {
    checkStr(String.format(Material.LOCALIZATION_STRING, material.identifier), sender);
  }

  private void checkStr(String str, ICommandSender sender) {
    if (!StatCollector.canTranslate(str)) {
      sender.addChatMessage(new ChatComponentText("Missing localization for name: " + str));
    }
  }
}
