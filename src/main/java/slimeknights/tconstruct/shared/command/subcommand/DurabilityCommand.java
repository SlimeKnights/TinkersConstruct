package slimeknights.tconstruct.shared.command.subcommand;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.command.MantleCommand;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.shared.command.HeldModifiableItemIterator;

import java.util.List;

/** Command to apply extra modifier slots, comparable to the creative modifier */
public class DurabilityCommand {
  private static final String ADD_SUCCESS = TConstruct.makeTranslationKey("command", "durability.success.add.single");
  private static final String ADD_SUCCESS_MULTIPLE = TConstruct.makeTranslationKey("command", "durability.success.add.multiple");
  private static final String SET_SUCCESS = TConstruct.makeTranslationKey("command", "durability.success.set.single");
  private static final String SET_SUCCESS_MULTIPLE = TConstruct.makeTranslationKey("command", "durability.success.set.multiple");
  private static final SimpleCommandExceptionType INVALID_ADD_COUNT = new SimpleCommandExceptionType(TConstruct.makeTranslation("command", "durability.failure.invalid_amount"));

  /**
   * Registers this sub command with the root command
   * @param subCommand  Command builder
   */
  public static void register(LiteralArgumentBuilder<CommandSourceStack> subCommand) {
    subCommand.requires(sender -> sender.hasPermission(MantleCommand.PERMISSION_GAME_COMMANDS))
      .then(Commands.argument("targets", EntityArgument.entities())
        // durability <target> add <amount>
        .then(Commands.literal("add")
          .then(Commands.argument("amount", IntegerArgumentType.integer())
            .executes(context -> run(context, Operation.ADD))))
        // durability <target> set <amount>
        .then(Commands.literal("set")
          .then(Commands.argument("amount", IntegerArgumentType.integer(0))
            .executes(context -> run(context, Operation.SET)))));
  }

  /** Runs the command */
  private static int run(CommandContext<CommandSourceStack> context, Operation op) throws CommandSyntaxException {
    int amount = IntegerArgumentType.getInteger(context, "amount");
    if (amount == 0 && op != Operation.SET) {
      throw INVALID_ADD_COUNT.create();
    }

    List<LivingEntity> successes = HeldModifiableItemIterator.apply(context, TinkerTags.Items.DURABILITY, (living, stack) -> {
      // add slots
      ToolStack tool = ToolStack.copyFrom(stack);
      if (op == Operation.ADD) {
        // since damage is a negative, need to subtract argument
        tool.setDamage(Math.max(0, tool.getDamage() - amount));
      } else {
        // setter is for damage, which is reduction from durability. So set by subtracting from that
        tool.setDamage(Math.max(0, tool.getStats().getInt(ToolStats.DURABILITY) - amount));
      }
      // if successful, update held item
      living.setItemInHand(InteractionHand.MAIN_HAND, tool.copyStack(stack));
      return true;
    });

    // success message
    CommandSourceStack source = context.getSource();
    int size = successes.size();
    if (op == Operation.ADD) {
      if (size == 1) {
        source.sendSuccess(() -> Component.translatable(ADD_SUCCESS, amount, successes.get(0).getDisplayName()), true);
      } else {
        source.sendSuccess(() -> Component.translatable(ADD_SUCCESS_MULTIPLE, amount, size), true);
      }
    } else {
      if (size == 1) {
        source.sendSuccess(() -> Component.translatable(SET_SUCCESS, amount, successes.get(0).getDisplayName()), true);
      } else {
        source.sendSuccess(() -> Component.translatable(SET_SUCCESS_MULTIPLE, amount, size), true);
      }
    }
    return size;
  }

  private enum Operation { ADD, SET }
}
