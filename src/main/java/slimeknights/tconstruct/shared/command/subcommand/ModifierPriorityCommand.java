package slimeknights.tconstruct.shared.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.TablePrinter;
import slimeknights.mantle.command.MantleCommand;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.shared.command.argument.ModifierHookArgument;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Command to list priorities for all modifiers */
public class ModifierPriorityCommand {
  private static final Component SUCCESS = Component.translatable("command.tconstruct.modifier_priority");

  /**
   * Registers this sub command with the root command
   * @param subCommand  Command builder
   */
  public static void register(LiteralArgumentBuilder<CommandSourceStack> subCommand) {
    subCommand.requires(sender -> sender.hasPermission(MantleCommand.PERMISSION_EDIT_SPAWN))
              // no argument: list all priorities
              .executes(context -> run(context, false))
              // argument: list only priorities of modifiers using that hook
              .then(Commands.argument("modifier_hook", ModifierHookArgument.modifierHook())
                            .executes(context -> run(context, true)));
  }

  /**
   * Runs the command
   * @param context   Command context
   * @param filtered  If true, filter the modifiers based on the hook argument
   * @return  Number of modifiers that are in the output table
   */
  private static int run(CommandContext<CommandSourceStack> context, boolean filtered) {
    // common table properties
    TablePrinter<Modifier> table = new TablePrinter<>();
    table.header("ID", m -> m.getId().toString());
    table.header("Priority", m -> Integer.toString(m.getPriority()));
    Stream<Modifier> modifiers = ModifierManager.INSTANCE.getAllValues();
    StringBuilder builder = new StringBuilder();
    builder.append("Modifier Priorities");

    // if filtered, show fewer modifiers and add to the log name
    if (filtered) {
      ModuleHook<?> filter = ModifierHookArgument.getModifier(context, "modifier_hook");
      modifiers = modifiers.filter(m -> m.getHooks().hasHook(filter));
      builder.append(" for ").append(filter.getId());
    } else {
      // if not filtered, include a row listing all used hooks
      table.header("Hooks", m -> m.getHooks().getAllModules().keySet().stream().map(ModuleHook::getId).sorted().map(ResourceLocation::toString).collect(Collectors.joining(", ")));
    }
    builder.append(":").append(System.lineSeparator());
    List<Modifier> list = modifiers.sorted(Comparator.comparingInt(Modifier::getPriority).reversed().thenComparing(Modifier::getId)).toList();
    table.add(list);

    table.build(builder);
    TConstruct.LOG.info(builder.toString());
    context.getSource().sendSuccess(() -> SUCCESS, true);
    return list.size();
  }
}
