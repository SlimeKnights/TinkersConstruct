package slimeknights.tconstruct.shared.command.subcommand;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableInt;
import slimeknights.mantle.command.MantleCommand;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRequirements;
import slimeknights.tconstruct.library.recipe.tinkerstation.ValidatedResult;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.shared.command.HeldModifiableItemIterator;
import slimeknights.tconstruct.shared.command.argument.ModifierArgument;

import java.util.List;

/** Command to apply a modifier to a tool without using slots */
public class ModifiersCommand {
  private static final String ADD_SUCCESS = TConstruct.makeTranslationKey("command", "modifiers.success.add.single");
  private static final String ADD_SUCCESS_MULTIPLE = TConstruct.makeTranslationKey("command", "modifiers.success.add.multiple");
  private static final String REMOVE_SUCCESS = TConstruct.makeTranslationKey("command", "modifiers.success.remove.single");
  private static final String REMOVE_SUCCESS_MULTIPLE = TConstruct.makeTranslationKey("command", "modifiers.success.remove.multiple");
  private static final DynamicCommandExceptionType MODIFIER_ERROR = new DynamicCommandExceptionType(error -> (Component)error);
  private static final Dynamic2CommandExceptionType CANNOT_REMOVE = new Dynamic2CommandExceptionType((name, entity) -> TConstruct.makeTranslation("command", "modifiers.failure.too_few_levels", name, entity));

  /**
   * Registers this sub command with the root command
   * @param subCommand  Command builder
   */
  public static void register(LiteralArgumentBuilder<CommandSourceStack> subCommand) {
    subCommand.requires(sender -> sender.hasPermission(MantleCommand.PERMISSION_GAME_COMMANDS))
              .then(Commands.argument("targets", EntityArgument.entities())
                            // modifiers <target> add <modifier> [<level>]
                            .then(Commands.literal("add")
                                          .then(Commands.argument("modifier", ModifierArgument.modifier())
                                                        .executes(context -> add(context, 1))
                                                        .then(Commands.argument("level", IntegerArgumentType.integer(1))
                                                                      .executes(context -> add(context, IntegerArgumentType.getInteger(context, "level"))))))
                            // modifiers <target> remove <modifier> [<level>]
                            .then(Commands.literal("remove")
                                          .then(Commands.argument("modifier", ModifierArgument.modifier())
                                                        .executes(context -> remove(context, -1))
                                                        .then(Commands.argument("level", IntegerArgumentType.integer(1))
                                                                      .executes(context -> remove(context, IntegerArgumentType.getInteger(context, "level")))))));
  }

  /** Runs the command */
  private static int add(CommandContext<CommandSourceStack> context, int level) throws CommandSyntaxException {
    Modifier modifier = ModifierArgument.getModifier(context, "modifier");
    List<LivingEntity> successes = HeldModifiableItemIterator.apply(context, (living, stack) -> {
      // add modifier
      ToolStack tool = ToolStack.from(stack);

      // first, see if we can add the modifier
      int currentLevel = tool.getModifierLevel(modifier);
      List<ModifierEntry> modifiers = tool.getModifierList();
      for (ModifierRequirements requirements : ModifierRecipeLookup.getRequirements(modifier.getId())) {
        ValidatedResult result = requirements.check(stack, level + currentLevel, modifiers);
        if (result.hasError()) {
          throw MODIFIER_ERROR.create(result.getMessage());
        }
      }
      tool = tool.copy();
      tool.addModifier(modifier.getId(), level);

      // ensure no modifier problems after adding
      ValidatedResult toolValidation = tool.validate();
      if (toolValidation.hasError()) {
        throw MODIFIER_ERROR.create(toolValidation.getMessage());
      }

      // if successful, update held item
      living.setItemInHand(InteractionHand.MAIN_HAND, tool.createStack(stack.getCount()));
      return true;
    });

    // success message
    CommandSourceStack source = context.getSource();
    int size = successes.size();
    if (size == 1) {
      source.sendSuccess(new TranslatableComponent(ADD_SUCCESS, modifier.getDisplayName(level), successes.get(0).getDisplayName()), true);
    } else {
      source.sendSuccess(new TranslatableComponent(ADD_SUCCESS_MULTIPLE, modifier.getDisplayName(level), size), true);
    }
    return size;
  }

  /** Runs the command */
  private static int remove(CommandContext<CommandSourceStack> context, int level) throws CommandSyntaxException {
    Modifier modifier = ModifierArgument.getModifier(context, "modifier");
    MutableInt maxRemove = new MutableInt(1);
    List<LivingEntity> successes = HeldModifiableItemIterator.apply(context, (living, stack) -> {
      // add modifier
      ToolStack tool = ToolStack.from(stack);

      // first, see if the modifier exists
      int currentLevel = tool.getUpgrades().getLevel(modifier.getId());
      if (currentLevel == 0) {
        throw CANNOT_REMOVE.create(modifier.getDisplayName(level), living.getName());
      }
      int removeLevel = level == -1 ? currentLevel : level;
      if (removeLevel > maxRemove.intValue()) {
        maxRemove.setValue(removeLevel);
      }
      tool = tool.copy();

      // first remove hook, primarily for removing raw NBT which is highly discouraged using
      int newLevel = currentLevel - removeLevel;
      if (newLevel <= 0) {
        modifier.beforeRemoved(tool, tool.getRestrictedNBT());
      }

      // remove the actual modifier
      tool.removeModifier(modifier.getId(), removeLevel);

      // ensure the tool is still valid
      ValidatedResult validated = tool.validate();
      if (validated.hasError()) {
        throw MODIFIER_ERROR.create(validated.getMessage());
      }

      // second remove hook, useful for removing modifier specific state data
      if (newLevel <= 0) {
        modifier.onRemoved(tool);
      }
      // if this was the last level, validate the tool is still valid without it
      if (newLevel <= 0) {
        validated = modifier.validate(tool, 0);
        if (validated.hasError()) {
          throw MODIFIER_ERROR.create(validated.getMessage());
        }
      }
      // check the modifier requirements
      ItemStack resultStack = tool.createStack(stack.getCount()); // creating a stack to make it as accurate as possible, though the old stack should be sufficient
      validated = ModifierRecipeLookup.checkRequirements(resultStack, tool);
      if (validated.hasError()) {
        throw MODIFIER_ERROR.create(validated.getMessage());
      }

      // if successful, update held item
      living.setItemInHand(InteractionHand.MAIN_HAND, tool.createStack(stack.getCount()));
      return true;
    });

    // success message
    CommandSourceStack source = context.getSource();
    int size = successes.size();
    if (size == 1) {
      source.sendSuccess(new TranslatableComponent(REMOVE_SUCCESS, modifier.getDisplayName(maxRemove.intValue()), successes.get(0).getDisplayName()), true);
    } else {
      source.sendSuccess(new TranslatableComponent(REMOVE_SUCCESS_MULTIPLE, modifier.getDisplayName(maxRemove.intValue()), size), true);
    }
    return size;
  }
}
