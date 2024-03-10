package slimeknights.tconstruct.shared.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.command.argument.MaterialArgument;
import slimeknights.tconstruct.shared.command.argument.ModifierArgument;
import slimeknights.tconstruct.shared.command.argument.ModifierHookArgument;
import slimeknights.tconstruct.shared.command.argument.SlotTypeArgument;
import slimeknights.tconstruct.shared.command.argument.ToolStatArgument;
import slimeknights.tconstruct.shared.command.subcommand.GeneratePartTexturesCommand;
import slimeknights.tconstruct.shared.command.subcommand.ModifierPriorityCommand;
import slimeknights.tconstruct.shared.command.subcommand.ModifierUsageCommand;
import slimeknights.tconstruct.shared.command.subcommand.ModifiersCommand;
import slimeknights.tconstruct.shared.command.subcommand.SlotsCommand;
import slimeknights.tconstruct.shared.command.subcommand.StatsCommand;

import java.util.function.Consumer;

public class TConstructCommand {

  /** Registers all TConstruct command related content */
  public static void init() {
    ArgumentTypes.register(TConstruct.resourceString("slot_type"), SlotTypeArgument.class, new EmptyArgumentSerializer<>(SlotTypeArgument::slotType));
    ArgumentTypes.register(TConstruct.resourceString("tool_stat"), ToolStatArgument.class, new EmptyArgumentSerializer<>(ToolStatArgument::stat));
    ArgumentTypes.register(TConstruct.resourceString("modifier"), ModifierArgument.class, new EmptyArgumentSerializer<>(ModifierArgument::modifier));
    ArgumentTypes.register(TConstruct.resourceString("material"), MaterialArgument.class, new EmptyArgumentSerializer<>(MaterialArgument::material));
    ArgumentTypes.register(TConstruct.resourceString("modifier_hook"), ModifierHookArgument.class, new EmptyArgumentSerializer<>(ModifierHookArgument::modifierHook));

    // add command listener
    MinecraftForge.EVENT_BUS.addListener(TConstructCommand::registerCommand);
  }

  /** Registers a sub command for the root Mantle command */
  private static void register(LiteralArgumentBuilder<CommandSourceStack> root, String name, Consumer<LiteralArgumentBuilder<CommandSourceStack>> consumer) {
    LiteralArgumentBuilder<CommandSourceStack> subCommand = Commands.literal(name);
    consumer.accept(subCommand);
    root.then(subCommand);
  }

  /** Event listener to register the Mantle command */
  private static void registerCommand(RegisterCommandsEvent event) {
    LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(TConstruct.MOD_ID);

    // sub commands
    register(builder, "modifiers", ModifiersCommand::register);
    register(builder, "tool_stats", StatsCommand::register);
    register(builder, "slots", SlotsCommand::register);
    register(builder, "report", b -> {
      register(b, "modifier_usage", ModifierUsageCommand::register);
      register(b, "modifier_priority", ModifierPriorityCommand::register);
    });
    register(builder, "generate_part_textures", GeneratePartTexturesCommand::register);

    // register final command
    event.getDispatcher().register(builder);
  }
}
