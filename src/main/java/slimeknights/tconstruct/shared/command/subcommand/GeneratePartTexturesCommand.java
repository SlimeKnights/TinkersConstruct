package slimeknights.tconstruct.shared.command.subcommand;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.server.command.ModIdArgument;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.shared.command.argument.MaterialArgument;
import slimeknights.tconstruct.shared.network.GeneratePartTexturesPacket;
import slimeknights.tconstruct.shared.network.GeneratePartTexturesPacket.Operation;

/** Command to generate tool textures using the palette logic */
public class GeneratePartTexturesCommand {
  private static final ITextComponent SUCCESS = TConstruct.makeTranslation("command", "generate_part_textures.start");

  /**
   * Registers this sub command with the root command
   * @param subCommand  Command builder
   */
  public static void register(LiteralArgumentBuilder<CommandSource> subCommand) {
    subCommand.requires(source -> source.getEntity() instanceof ServerPlayerEntity)
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
  private static int runMaterial(CommandContext<CommandSource> context, Operation filter) throws CommandSyntaxException {
    MaterialId material = MaterialArgument.getMaterial(context, "material").getIdentifier();
    return run(context, filter, material.getNamespace(), material.getPath());
  }

  /** Runs the command, filtered by a mod ID */
  private static int runModId(CommandContext<CommandSource> context, Operation filter) throws CommandSyntaxException {
    return run(context, filter, context.getArgument("mod_id", String.class), "");
  }

  /** Runs the command */
  private static int run(CommandContext<CommandSource> context, Operation filter, String modId, String materialName) throws CommandSyntaxException {
    CommandSource source = context.getSource();
    source.sendFeedback(SUCCESS, true);
    TinkerNetwork.getInstance().sendTo(new GeneratePartTexturesPacket(filter, modId, materialName), source.asPlayer());
    return 0;
  }
}
