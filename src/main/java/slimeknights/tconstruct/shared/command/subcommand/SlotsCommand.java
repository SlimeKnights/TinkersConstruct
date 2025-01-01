package slimeknights.tconstruct.shared.command.subcommand;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.command.MantleCommand;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.shared.command.HeldModifiableItemIterator;
import slimeknights.tconstruct.shared.command.argument.SlotTypeArgument;

import java.util.List;

/** Command to apply extra modifier slots, comparable to the creative modifier */
public class SlotsCommand {
  private static final String ADD_SUCCESS = TConstruct.makeTranslationKey("command", "slots.success.add.single");
  private static final String ADD_SUCCESS_MULTIPLE = TConstruct.makeTranslationKey("command", "slots.success.add.multiple");
  private static final String SET_SUCCESS = TConstruct.makeTranslationKey("command", "slots.success.set.single");
  private static final String SET_SUCCESS_MULTIPLE = TConstruct.makeTranslationKey("command", "slots.success.set.multiple");
  private static final SimpleCommandExceptionType INVALID_SLOT_COUNT = new SimpleCommandExceptionType(TConstruct.makeTranslation("command", "slots.failure.invalid_count"));
  private static final DynamicCommandExceptionType VALIDATION_ERROR = new DynamicCommandExceptionType(error -> (Component)error);

  /**
   * Registers this sub command with the root command
   * @param subCommand  Command builder
   */
  public static void register(LiteralArgumentBuilder<CommandSourceStack> subCommand) {
    subCommand.requires(sender -> sender.hasPermission(MantleCommand.PERMISSION_GAME_COMMANDS))
              .then(Commands.argument("targets", EntityArgument.entities())
                            // slots <target> add <slot_type> [<count>]
                            .then(Commands.literal("add")
                                          .then(Commands.argument("slot_type", SlotTypeArgument.slotType(false))
                                                        .executes(context -> run(context, Operation.ADD, 1))
                                                        .then(Commands.argument("count", IntegerArgumentType.integer())
                                                                      .executes(context -> run(context, Operation.ADD)))))
                            // slots <target> set <slot_type> <count>
                            .then(Commands.literal("set")
                                          .then(Commands.argument("slot_type", SlotTypeArgument.slotType(false))
                                                        .then(Commands.argument("count", IntegerArgumentType.integer(0))
                                                                      .executes(context -> run(context, Operation.SET))))));
  }

  /** Runs the command with a count argument */
  private static int run(CommandContext<CommandSourceStack> context, Operation op) throws CommandSyntaxException {
    return run(context, op, IntegerArgumentType.getInteger(context, "count"));
  }

  /** Runs the command */
  private static int run(CommandContext<CommandSourceStack> context, Operation op, int count) throws CommandSyntaxException {
    if (count == 0 && op != Operation.SET) {
      throw INVALID_SLOT_COUNT.create();
    }

    SlotType slotType = SlotTypeArgument.getSlotType(context, "slot_type");
    List<LivingEntity> successes = HeldModifiableItemIterator.apply(context, (living, stack) -> {
      // add slots
      ToolStack tool = ToolStack.copyFrom(stack);
      ModDataNBT slots = tool.getPersistentData();
      if (op == Operation.ADD) {
        slots.addSlots(slotType, count);
      } else {
        // for setting, we want to subtract slots from all sources, so this is the one true source
        slots.addSlots(slotType, count - tool.getFreeSlots(slotType));
      }
      tool.rebuildStats();

      // ensure no modifier problems after adding, mainly happens if we subtract slots
      Component toolValidation = tool.tryValidate();
      if (toolValidation != null) {
        throw VALIDATION_ERROR.create(toolValidation);
      }

      // if successful, update held item
      living.setItemInHand(InteractionHand.MAIN_HAND, tool.createStack(stack.getCount()));
      return true;
    });

    // success message
    CommandSourceStack source = context.getSource();
    int size = successes.size();
    if (op == Operation.ADD) {
      if (size == 1) {
        source.sendSuccess(() -> Component.translatable(ADD_SUCCESS, count, slotType.getDisplayName(), successes.get(0).getDisplayName()), true);
      } else {
        source.sendSuccess(() -> Component.translatable(ADD_SUCCESS_MULTIPLE, count, slotType.getDisplayName(), size), true);
      }
    } else {
      if (size == 1) {
        source.sendSuccess(() -> Component.translatable(SET_SUCCESS, slotType.getDisplayName(), count, successes.get(0).getDisplayName()), true);
      } else {
        source.sendSuccess(() -> Component.translatable(SET_SUCCESS_MULTIPLE, slotType.getDisplayName(), count, size), true);
      }
    }
    return size;
  }

  private enum Operation { ADD, SET }
}
