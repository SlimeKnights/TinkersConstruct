package slimeknights.tconstruct.shared.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import slimeknights.tconstruct.TConstruct;

import java.util.function.Consumer;

public class TConstructCommand {

  /** Registers all TConstruct command related content */
  public static void init() {
    ArgumentTypes.register(TConstruct.resourceString("slot_type"), SlotTypeArgument.class, new ArgumentSerializer<>(SlotTypeArgument::slotType));

    // add command listener
    MinecraftForge.EVENT_BUS.addListener(TConstructCommand::registerCommand);
  }

  /** Registers a sub command for the root Mantle command */
  private static void register(LiteralArgumentBuilder<CommandSource> root, String name, Consumer<LiteralArgumentBuilder<CommandSource>> consumer) {
    LiteralArgumentBuilder<CommandSource> subCommand = Commands.literal(name);
    consumer.accept(subCommand);
    root.then(subCommand);
  }

  /** Event listener to register the Mantle command */
  private static void registerCommand(RegisterCommandsEvent event) {
    LiteralArgumentBuilder<CommandSource> builder = Commands.literal(TConstruct.MOD_ID);

    // sub commands
    register(builder, "modifier_usage", ModifierUsageCommand::register);

    // register final command
    event.getDispatcher().register(builder);
  }
}
