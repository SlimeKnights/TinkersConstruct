package slimeknights.tconstruct.debug;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import java.util.List;

public interface ICommandBase {
  /**
   * Register aliases for a command
   *
   * @param dispatcher     The command dispatcher
   * @param commandLiteral The command to register the aliases for
   * @param aliases        A List of Strings, one for each alias to register
   */
  static void registerAliasesStatic(
    CommandDispatcher<CommandSource> dispatcher,
    LiteralCommandNode<CommandSource> commandLiteral,
    List<String> aliases
  ) {
    aliases.forEach(str -> dispatcher.register(Commands.literal(str).redirect(commandLiteral)));
  }

  /**
   * Register aliases for a command.
   * This is a convenience method for calling its static counterpart within a non-static context,
   * or for overriding with your own alias registration code
   *
   * @param dispatcher     The command dispatcher
   * @param commandLiteral The command to register the aliases for
   * @param aliases        A List of Strings, one for each alias to register
   */
  default void registerAliases(
    CommandDispatcher<CommandSource> dispatcher,
    LiteralCommandNode<CommandSource> commandLiteral,
    List<String> aliases
  ) { registerAliasesStatic(dispatcher, commandLiteral, aliases); }


  /**
   * Register aliases for a command
   *
   * @param dispatcher           The command dispatcher
   * @param commandLiteral       The command to register the aliases for
   * @param requiredPermissionLv The level of permission required to use this command
   * @param aliases              A List of Strings, one for each alias to register
   */
  static void registerAliasesStatic(
    CommandDispatcher<CommandSource> dispatcher,
    LiteralCommandNode<CommandSource> commandLiteral,
    int requiredPermissionLv,
    List<String> aliases
  ) {
    aliases.forEach(str -> dispatcher.register(
      Commands.literal(str)
        .requires(cmdSrc -> cmdSrc.hasPermissionLevel(requiredPermissionLv))
        .redirect(commandLiteral)));
  }

  /**
   * Register aliases for a command.
   * This is a convenience method for calling its static counterpart within a non-static context,
   * or for overriding with your own alias registration code
   *
   * @param dispatcher           The command dispatcher
   * @param commandLiteral       The command to register the aliases for
   * @param requiredPermissionLv The level of permission required to use this command
   * @param aliases              A List of Strings, one for each alias to register
   */
  default void registerAliases(
    CommandDispatcher<CommandSource> dispatcher,
    LiteralCommandNode<CommandSource> commandLiteral,
    int requiredPermissionLv,
    List<String> aliases
  ) { registerAliasesStatic(dispatcher, commandLiteral, requiredPermissionLv, aliases); }
}

