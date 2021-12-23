package slimeknights.tconstruct.shared.command.subcommand;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lombok.RequiredArgsConstructor;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.command.MantleCommand;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.shared.command.HeldModifiableItemIterator;
import slimeknights.tconstruct.shared.command.argument.ToolStatArgument;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.slotless.StatOverrideModifier;

import java.util.List;
import java.util.Locale;

/** Command to modify a tool's stats */
public class StatsCommand {
  private static final String SUCCESS_KEY_PREFIX = TConstruct.makeTranslationKey("command", "stats.success.");
  private static final String RESET_ALL_SINGLE = TConstruct.makeTranslationKey("command", "stats.success.reset.all.single");
  private static final String RESET_ALL_MULTIPLE = TConstruct.makeTranslationKey("command", "stats.success.reset.all.multiple");
  private static final String RESET_STAT_SINGLE = TConstruct.makeTranslationKey("command", "stats.success.reset.stat.single");
  private static final String RESET_STAT_MULTIPLE = TConstruct.makeTranslationKey("command", "stats.success.reset.stat.multiple");
  private static final SimpleCommandExceptionType INVALID_ADD = new SimpleCommandExceptionType(TConstruct.makeTranslation("command", "stats.failure.invalid_add"));
  private static final SimpleCommandExceptionType INVALID_MULTIPLY = new SimpleCommandExceptionType(TConstruct.makeTranslation("command", "stats.failure.invalid_multiply"));
  private static final DynamicCommandExceptionType MODIFIER_ERROR = new DynamicCommandExceptionType(error -> (ITextComponent)error);

  /**
   * Registers this sub command with the root command
   * @param subCommand  Command builder
   */
  public static void register(LiteralArgumentBuilder<CommandSource> subCommand) {
    subCommand.requires(sender -> sender.hasPermissionLevel(MantleCommand.PERMISSION_GAME_COMMANDS))
              .then(Commands.argument("targets", EntityArgument.entities())
                            // stats <target> bonus add|set <stat_type> <value>
                            .then(Commands.literal("bonus")
                                          .then(Commands.literal("add")
                                                        .then(Commands.argument("stat_type", ToolStatArgument.stat())
                                                                      .then(Commands.argument("value", FloatArgumentType.floatArg())
                                                                                    .executes(context -> update(context, Type.BONUS, Operation.MODIFY)))))
                                          .then(Commands.literal("set")
                                                        .then(Commands.argument("stat_type", ToolStatArgument.stat())
                                                                      .then(Commands.argument("value", FloatArgumentType.floatArg())
                                                                                    .executes(context -> update(context, Type.BONUS, Operation.SET))))))
                            // stats <target> multiplier multiply|set <float_stat> <value>
                            .then(Commands.literal("multiplier")
                                          .then(Commands.literal("multiply")
                                                        .then(Commands.argument("float_stat", ToolStatArgument.stat(FloatToolStat.class))
                                                                      .then(Commands.argument("value", FloatArgumentType.floatArg(0))
                                                                                    .executes(context -> update(context, Type.MULTIPLY, Operation.MODIFY)))))
                                          .then(Commands.literal("set")
                                                        .then(Commands.argument("float_stat", ToolStatArgument.stat(FloatToolStat.class))
                                                                      .then(Commands.argument("value", FloatArgumentType.floatArg(0))
                                                                                    .executes(context -> update(context, Type.MULTIPLY, Operation.SET))))))
                            // stats <target> reset [<stat_type>]
                            .then(Commands.literal("reset")
                                          .executes(StatsCommand::resetAll)
                                          .then(Commands.argument("stat_type", ToolStatArgument.stat())
                                                        .executes(StatsCommand::resetStat))));
  }

  /** Modifies a tool stat with the given operation */
  private static int update(CommandContext<CommandSource> context, Type type, Operation op) throws CommandSyntaxException {
    float value = FloatArgumentType.getFloat(context, "value");
    // simplifies later operations if we skip operations that do nothing
    if (op == Operation.MODIFY) {
      if (value == 0 && type == Type.BONUS) {
        throw INVALID_ADD.create();
      }
      if (value == 1 && type == Type.MULTIPLY) {
        throw INVALID_MULTIPLY.create();
      }
    }

    IToolStat<?> stat = ToolStatArgument.getStat(context, type.stat);
    List<LivingEntity> successes = HeldModifiableItemIterator.apply(context, (living, stack) -> {
      ToolStack tool = ToolStack.copyFrom(stack);

      // apply the proper operation
      boolean needsModifier;
      StatOverrideModifier stats = TinkerModifiers.statOverride.get();
      if (type == Type.BONUS) {
        if (op == Operation.MODIFY) {
          needsModifier = stats.addBonus(tool, stat, value);
        } else {
          needsModifier = stats.setBonus(tool, stat, value);
        }
      } else {
        FloatToolStat floatStat = (FloatToolStat) stat;
        if (op == Operation.MODIFY) {
          needsModifier = stats.multiply(tool, floatStat, value);
        } else {
          needsModifier = stats.setMultiplier(tool, floatStat, value);
        }
      }

      // ensure the modifier is present if needed/not present if not needed
      int level = tool.getUpgrades().getLevel(stats);
      boolean hasModifier = level > 0;
      if (needsModifier && !hasModifier) {
        tool.addModifier(stats, 1);
      } else if (hasModifier && !needsModifier) {
        tool.removeModifier(stats, level);
      } else {
        tool.rebuildStats();
      }

      // ensure the tool is still valid
      ValidatedResult validated = tool.validate();
      if (validated.hasError()) {
        throw MODIFIER_ERROR.create(validated.getMessage());
      }

      // if successful, update held item
      living.setHeldItem(Hand.MAIN_HAND, tool.createStack());
      return true;
    });

    // success message
    CommandSource source = context.getSource();
    int size = successes.size();
    String successKey = SUCCESS_KEY_PREFIX + type.key + "." + op.key + ".";
    if (size == 1) {
      source.sendFeedback(new TranslationTextComponent(successKey + "single", stat.getPrefix(), value, successes.get(0).getDisplayName()), true);
    } else {
      source.sendFeedback(new TranslationTextComponent(successKey + "multiple", stat.getPrefix(), value, size), true);
    }
    return size;
  }

  /** Resets all stats to default */
  private static int resetStat(CommandContext<CommandSource> context) throws CommandSyntaxException {
    IToolStat<?> stat = ToolStatArgument.getStat(context, "stat_type");
    List<LivingEntity> successes = HeldModifiableItemIterator.apply(context, (living, stack) -> {
      ToolStack tool = ToolStack.from(stack);
      StatOverrideModifier stats = TinkerModifiers.statOverride.get();
      int currentLevel = tool.getModifierLevel(stats);
      if (currentLevel > 0) {
        tool = tool.copy();

        // try removing both bonus and multiplier
        if (stat instanceof FloatToolStat) {
          stats.setMultiplier(tool, (FloatToolStat)stat, 1);
        }
        boolean needsModifier = stats.setBonus(tool, stat, 0);

        // ensure the modifier is removed if no longer needed
        if (!needsModifier) {
          tool.removeModifier(stats, currentLevel);
        } else {
          tool.rebuildStats();
        }

        // ensure the tool is still valid
        ValidatedResult validated = tool.validate();
        if (validated.hasError()) {
          throw MODIFIER_ERROR.create(validated.getMessage());
        }

        // if successful, update held item
        living.setHeldItem(Hand.MAIN_HAND, tool.createStack());
      }
      return true;
    });

    // success message
    CommandSource source = context.getSource();
    int size = successes.size();
    if (size == 1) {
      source.sendFeedback(new TranslationTextComponent(RESET_STAT_SINGLE, stat.getPrefix(), successes.get(0).getDisplayName()), true);
    } else {
      source.sendFeedback(new TranslationTextComponent(RESET_STAT_MULTIPLE, stat.getPrefix(), size), true);
    }
    return size;
  }

  /** Resets all stats to default */
  private static int resetAll(CommandContext<CommandSource> context) throws CommandSyntaxException {
    List<LivingEntity> successes = HeldModifiableItemIterator.apply(context, (living, stack) -> {
      // remove modifier
      ToolStack tool = ToolStack.from(stack);
      StatOverrideModifier stats = TinkerModifiers.statOverride.get();
      int level = tool.getModifierLevel(stats);
      if (level > 0) {
        tool = tool.copy();
        tool.removeModifier(stats, level);
        stats.onRemoved(tool);

        // ensure the tool is still valid
        ValidatedResult validated = tool.validate();
        if (validated.hasError()) {
          throw MODIFIER_ERROR.create(validated.getMessage());
        }

        // if successful, update held item
        living.setHeldItem(Hand.MAIN_HAND, tool.createStack());
      }
      return true;
    });

    // success message
    CommandSource source = context.getSource();
    int size = successes.size();
    if (size == 1) {
      source.sendFeedback(new TranslationTextComponent(RESET_ALL_SINGLE, successes.get(0).getDisplayName()), true);
    } else {
      source.sendFeedback(new TranslationTextComponent(RESET_ALL_MULTIPLE, size), true);
    }
    return size;
  }

  @RequiredArgsConstructor
  private enum Type {
    BONUS("stat_type"),
    MULTIPLY("float_stat");
    private final String key = name().toLowerCase(Locale.US);
    private final String stat;
  }
  private enum Operation {
    MODIFY, SET;
    private final String key = name().toLowerCase(Locale.US);
  }
}
