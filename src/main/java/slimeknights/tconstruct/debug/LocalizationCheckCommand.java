package slimeknights.tconstruct.debug;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;

public class LocalizationCheckCommand extends CommandBase {

  @Override
  public int getRequiredPermissionLevel() {
    return 0;
  }

  @Nonnull
  @Override
  public String getCommandName() {
    return "checkLocalizationStrings";
  }

  @Nonnull
  @Override
  public String getCommandUsage(@Nonnull ICommandSender sender) {
    return "/" + getCommandName() + " [Material-Identifier]";
  }

  @Nonnull
  @Override
  public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
    if(args.length != 1) {
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
  public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
    if(args.length > 1) {
      throw new WrongUsageException(getCommandUsage(sender));
    }

    if(args.length > 0) {
      Material mat = TinkerRegistry.getMaterial(args[0]);
      if(mat == Material.UNKNOWN) {
        throw new CommandException("Unknown material: " + args[0]);
      }

      scanMaterial(mat, sender);
    }
    else {
      for(Material mat : TinkerRegistry.getAllMaterials()) {
        scanMaterial(mat, sender);
      }
    }
  }

  private void scanMaterial(Material material, ICommandSender sender) {
    checkStr(String.format(Material.LOC_Name, material.identifier), sender);
  }

  private void checkStr(String str, ICommandSender sender) {
    if(!I18n.canTranslate(str)) {
      sender.addChatMessage(new TextComponentString("Missing localization for name: " + str));
    }
  }
}
