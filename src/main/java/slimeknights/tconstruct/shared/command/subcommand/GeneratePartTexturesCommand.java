package slimeknights.tconstruct.shared.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.command.ModIdArgument;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.shared.command.argument.MaterialArgument;
import slimeknights.tconstruct.shared.network.GeneratePartTexturesPacket;
import slimeknights.tconstruct.shared.network.GeneratePartTexturesPacket.Operation;

/** Command to generate tool textures using the palette logic */
public class GeneratePartTexturesCommand {
  private static final Component SUCCESS = TConstruct.makeTranslation("command", "generate_part_textures.start");

  /**
   * Registers this sub command with the root command
   * @param subCommand  Command builder
   */
  public static void register(LiteralArgumentBuilder<CommandSourceStack> subCommand) {
    subCommand.requires(source -> source.getEntity() instanceof ServerPlayer)
              // generate_part_textures all|missing [<mod_id>|<material>]
              .then(Commands.literal("all")
                            .executes(context -> run(context, Operation.ALL, "", ""))
                            .then(Commands.argument("mod_id", ModIdArgument.modIdArgument()).executes(context -> runModId(context, Operation.ALL)))
                            .then(Commands.argument("material", MaterialArgument.material()).executes(context -> runMaterial(context, Operation.ALL))))
              .then(Commands.literal("missing")
                            .executes(context -> run(context, Operation.MISSING, "", ""))
                            .then(Commands.argument("mod_id", ModIdArgument.modIdArgument()).executes(context -> runModId(context, Operation.MISSING)))
                            .then(Commands.argument("material", MaterialArgument.material()).executes(context -> runMaterial(context, Operation.MISSING))));
  }

  /** Runs the command, filtered by a material */
  private static int runMaterial(CommandContext<CommandSourceStack> context, Operation filter) throws CommandSyntaxException {
    MaterialId material = MaterialArgument.getMaterial(context, "material").getIdentifier();
    return run(context, filter, material.getNamespace(), material.getPath());
  }

  /** Runs the command, filtered by a mod ID */
  private static int runModId(CommandContext<CommandSourceStack> context, Operation filter) throws CommandSyntaxException {
    return run(context, filter, context.getArgument("mod_id", String.class), "");
  }

  /** Runs the command */
  private static int run(CommandContext<CommandSourceStack> context, Operation filter, String modId, String materialName) throws CommandSyntaxException {
    CommandSourceStack source = context.getSource();
    source.sendSuccess(() -> SUCCESS, true);
    TinkerNetwork.getInstance().sendTo(new GeneratePartTexturesPacket(filter, modId, materialName), source.getPlayerOrException());
    return 0;
  }
}
