package slimeknights.tconstruct.shared.command.subcommand;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.command.MantleCommand;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.shared.command.HeldModifiableItemIterator;
import slimeknights.tconstruct.shared.command.TConstructCommand;
import slimeknights.tconstruct.shared.command.argument.MaterialArgument;
import slimeknights.tconstruct.shared.command.argument.MaterialStatsArgument;
import slimeknights.tconstruct.shared.command.argument.MaterialVariantArgument;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/** Command to work with materials on a tool */
public class MaterialsCommand {
  private static final String ADD_SUCCESS = TConstruct.makeTranslationKey("command", "materials.success.set.single");
  private static final String ADD_SUCCESS_MULTIPLE = TConstruct.makeTranslationKey("command", "materials.success.set.multiple");
  private static final Dynamic2CommandExceptionType INVALID_INDEX = new Dynamic2CommandExceptionType((index, name) -> TConstruct.makeTranslation("command", "materials.failure.invalid_index", index, name));
  private static final Dynamic2CommandExceptionType INVALID_STATS = new Dynamic2CommandExceptionType((statType, stats) -> TConstruct.makeTranslation("command", "materials.failure.invalid_stats", statType, stats));
  private static final Component NO_TRAITS = TConstruct.makeTranslation("command", "materials.failure.no_traits").withStyle(ChatFormatting.ITALIC);

  /**
   * Registers this sub command with the root command
   * @param subCommand  Command builder
   */
  public static void register(LiteralArgumentBuilder<CommandSourceStack> subCommand) {
    subCommand.requires(sender -> sender.hasPermission(MantleCommand.PERMISSION_GAME_COMMANDS))
              // materials set <target> <index> <material>
              .then(Commands.literal("set")
                            .then(Commands.argument("targets", EntityArgument.entities())
                                          .then(Commands.argument("index", IntegerArgumentType.integer(0))
                                                        .then(Commands.argument("material", MaterialVariantArgument.material())
                                                                      .executes(MaterialsCommand::set)))))
              // materials stats <stat_type> [material]
              .then(Commands.literal("stats")
                            .then(Commands.argument("stat_type", MaterialStatsArgument.stats())
                                          .executes(MaterialsCommand::defaultStats)
                                          .then(Commands.argument("material", MaterialArgument.material())
                                                        .executes(MaterialsCommand::stats))))
              // materials traits <material> [stat_type]
              .then(Commands.literal("traits")
                            .then(Commands.argument("material", MaterialArgument.material())
                                          .executes(context -> traits(context, null))
                                          .then(Commands.argument("stat_type", MaterialStatsArgument.stats())
                                                        .executes(MaterialsCommand::traits))));
  }

  /** Sets the material at the index */
  private static int set(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    int index = IntegerArgumentType.getInteger(context, "index");
    MaterialVariantId material = MaterialVariantArgument.getMaterial(context, "material");
    List<LivingEntity> successes = HeldModifiableItemIterator.apply(context, (living, stack) -> {
      // add modifier
      ToolStack original = ToolStack.from(stack);
      // validate the index
      if (index >= ToolMaterialHook.stats(original.getDefinition()).size()) {
        throw INVALID_INDEX.create(index, living.getDisplayName());
      }
      // set the material
      ToolStack tool = original.copy();
      tool.replaceMaterial(index, material);

      // ensure no modifier problems after swapping
      Component toolValidation = tool.tryValidate();
      if (toolValidation != null) {
        throw TConstructCommand.COMPONENT_ERROR.create(toolValidation);
      }
      toolValidation = ModifierRemovalHook.onRemoved(original, tool);
      if (toolValidation != null) {
        throw TConstructCommand.COMPONENT_ERROR.create(toolValidation);
      }

      // if successful, update held item
      living.setItemInHand(InteractionHand.MAIN_HAND, tool.createStack(stack.getCount()));
      return true;
    });

    // success message
    CommandSourceStack source = context.getSource();
    int size = successes.size();
    if (size == 1) {
      source.sendSuccess(Component.translatable(ADD_SUCCESS, index, MaterialTooltipCache.getDisplayName(material), successes.get(0).getDisplayName()), true);
    } else {
      source.sendSuccess(Component.translatable(ADD_SUCCESS_MULTIPLE, index, MaterialTooltipCache.getDisplayName(material), size), true);
    }
    return size;
  }

  /** Sets the material at the index */
  private static int defaultStats(CommandContext<CommandSourceStack> context) {
    MaterialStatType<?> statType = MaterialStatsArgument.getStat(context, "stat_type");
    MutableComponent output = TConstruct.makeTranslation("command", "materials.success.stats.default", statType.getDefaultStats().getLocalizedName());
    for (Component component : statType.getDefaultStats().getLocalizedInfo()) {
      output.append("\n* ").append(component);
    }
    context.getSource().sendSuccess(output, true);
    return 1;
  }

  /** Sets the material at the index */
  private static int stats(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    MaterialId material = MaterialArgument.getMaterial(context, "material").getIdentifier();
    MaterialStatType<?> statType = MaterialStatsArgument.getStat(context, "stat_type");
    Optional<IMaterialStats> stats = MaterialRegistry.getInstance().getMaterialStats(material, statType.getId());
    if (stats.isPresent()) {
      MutableComponent output = TConstruct.makeTranslation("command", "materials.success.stats.material", stats.get().getLocalizedName(), MaterialTooltipCache.getDisplayName(material));
      for (Component component : stats.get().getLocalizedInfo()) {
        output.append("\n* ").append(component);
      }
      context.getSource().sendSuccess(output, true);
      return 1;
    } else {
      throw INVALID_STATS.create(statType.getId(), MaterialTooltipCache.getDisplayName(material));
    }
  }

  /** Sets the material at the index */
  private static int traits(CommandContext<CommandSourceStack> context) {
    return traits(context, MaterialStatsArgument.getStat(context, "stat_type"));
  }

  /** Sets the material at the index */
  private static int traits(CommandContext<CommandSourceStack> context, @Nullable MaterialStatType<?> statType) {
    MaterialId material = MaterialArgument.getMaterial(context, "material").getIdentifier();
    List<ModifierEntry> traits;
    MutableComponent output;
    if (statType == null) {
      traits = MaterialRegistry.getInstance().getDefaultTraits(material);
      output = TConstruct.makeTranslation("command", "materials.success.traits.default", MaterialTooltipCache.getDisplayName(material));
    } else {
      traits = MaterialRegistry.getInstance().getTraits(material, statType.getId());
      output = TConstruct.makeTranslation("command", "materials.success.traits.stat", statType.getDefaultStats().getLocalizedName(), MaterialTooltipCache.getDisplayName(material));
    }
    // if no traits, add special empty string
    if (traits.isEmpty()) {
      output.append("\n* ").append(NO_TRAITS);
    } else {
      for (ModifierEntry trait : traits) {
        output.append("\n* ").append(trait.getDisplayName());
      }
    }
    context.getSource().sendSuccess(output, true);
    return traits.size();
  }
}
