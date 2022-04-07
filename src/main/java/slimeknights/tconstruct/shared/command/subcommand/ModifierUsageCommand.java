package slimeknights.tconstruct.shared.command.subcommand;

import com.google.common.collect.HashMultimap;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.util.TablePrinter;
import slimeknights.mantle.command.MantleCommand;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IModifierRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.shared.command.argument.SlotTypeArgument;
import slimeknights.tconstruct.shared.command.argument.SlotTypeArgument.OptionalSlotType;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Command that prints a list of all modifiers and how they are used in current datapacks */
public class ModifierUsageCommand {
  private static final Component SUCCESS = new TranslatableComponent("command.tconstruct.modifier_usage");

  /**
   * Registers this sub command with the root command
   * @param subCommand  Command builder
   */
  public static void register(LiteralArgumentBuilder<CommandSourceStack> subCommand) {
    subCommand.requires(sender -> sender.hasPermission(MantleCommand.PERMISSION_EDIT_SPAWN))
              .executes(context -> runForType(context, ModifierUsages.ALL, null))
              // modifier_usage all
              .then(Commands.literal("all").executes(context -> runForType(context, ModifierUsages.ALL, null)))
              // modifier_usage recipe [<slot_type>]
              .then(Commands.literal("recipe")
                            .then(Commands.argument("slot_type", SlotTypeArgument.slotType()).executes(ModifierUsageCommand::runRecipeWithFilter))
                            .executes(context -> runForType(context, ModifierUsages.RECIPE, null)))
              // modifier_usage material_trait
              .then(Commands.literal("material_trait").executes(context -> runForType(context, ModifierUsages.MATERIAL_TRAIT, null)))
              // modifier_usage tool_trait
              .then(Commands.literal("tool_trait").executes(context -> runForType(context, ModifierUsages.TOOL_TRAIT, null)))
              // modifier_usage unused
              .then(Commands.literal("unused").executes(context -> runForType(context, ModifierUsages.UNUSED, null)));
  }

  /** Runs the actual command */
  private static int runRecipeWithFilter(CommandContext<CommandSourceStack> context) {
    return runForType(context, ModifierUsages.RECIPE, SlotTypeArgument.getOptional(context, "slot_type"));
  }

  private static int runForType(CommandContext<CommandSourceStack> context, ModifierUsages filter, @Nullable OptionalSlotType slotFilter) {
    // recipe modifiers are used in a displayable modifier recipe
    HashMultimap<SlotType,Modifier> recipeModifiers = context.getSource().getLevel().getRecipeManager().byType(RecipeTypes.TINKER_STATION).values().stream()
                                                             .filter(r -> r instanceof IModifierRecipe)
                                                             .map(r -> (IModifierRecipe) r)
                                                             .collect(Collector.of(HashMultimap::create, (map, r) -> map.put(r.getSlotType(), r.getModifier()), (m1, m2) -> {
                                                               m1.putAll(m2);
                                                               return m1;
                                                             }));
    // material traits are used in material traits (kinda obvious)
    IMaterialRegistry matReg = MaterialRegistry.getInstance();
    Set<Modifier> materialTraits = matReg.getAllMaterials().stream()
                                         .flatMap(mat -> {
                                           MaterialId matId = mat.getIdentifier();
                                           return Stream.concat(matReg.getDefaultTraits(matId).stream(),
                                                                matReg.getAllStats(matId).stream()
                                                                      .filter(stat -> matReg.hasUniqueTraits(matId, stat.getIdentifier()))
                                                                      .flatMap(stat -> matReg.getTraits(matId, stat.getIdentifier()).stream()));
                                         })
                                         .map(ModifierEntry::getModifier)
                                         .collect(Collectors.toSet());
    // finally, tool traits we limit to anything in the modifiable tag
    Set<Modifier> toolTraits = RegistryHelper.getTagValueStream(Registry.ITEM, TinkerTags.Items.MODIFIABLE)
                                             .filter(item -> item instanceof IModifiable)
                                             .flatMap(item -> ((IModifiable) item).getToolDefinition().getData().getTraits().stream())
                                             .map(ModifierEntry::getModifier)
                                             .collect(Collectors.toSet());

    // next, get our list of modifiers
    Stream<Modifier> modifierStream;
    switch (filter) {
      case RECIPE:
        // filter to just one type of modifier if requested
        if (slotFilter != null) {
          modifierStream = recipeModifiers.get(slotFilter.slotType()).stream();
        } else {
          modifierStream = recipeModifiers.values().stream();
        }
        break;
      case MATERIAL_TRAIT:
        modifierStream = materialTraits.stream();
        break;
      case TOOL_TRAIT:
        modifierStream = toolTraits.stream();
        break;
      default:
        modifierStream = TinkerRegistries.MODIFIERS.get().getValues().stream();
        break;
    }
    // if requested, filter out all
    if (filter == ModifierUsages.UNUSED) {
      modifierStream = modifierStream.filter(modifier -> !recipeModifiers.containsValue(modifier) && !materialTraits.contains(modifier) && !toolTraits.contains(modifier));
    }

    // start building the table for output
    TablePrinter<ModifierUsageRow> table = new TablePrinter<>();
    table.header("ID", r -> r.modifierId().toString());
    if (filter != ModifierUsages.UNUSED) {
      if (filter != ModifierUsages.RECIPE || slotFilter == null) {
        table.header("Recipe", ModifierUsageRow::recipe);
      }
      if (filter != ModifierUsages.MATERIAL_TRAIT) {
        table.header("material Trait", r -> r.materialTrait() ? "Material trait" : "");
      }
      if (filter != ModifierUsages.TOOL_TRAIT) {
        table.header("tool Trait", r -> r.toolTrait() ? "Tool trait" : "");
      }
    }
    StringBuilder logOutput = new StringBuilder();
    logOutput.append(filter.logPrefix);
    if (slotFilter != null) {
      if (slotFilter.slotType() == null) {
        logOutput.append(" (slotless)");
      } else {
        logOutput.append(" (").append(slotFilter.slotType().getName()).append(")");
      }
    }
    logOutput.append(System.lineSeparator());

    // for all the modifiers (sorted), add table rows
    Collection<Modifier> finalList = modifierStream.sorted(Comparator.comparing(Modifier::getId)).toList();
    finalList.forEach(modifier -> {
      // determine which recipes use this by slot type
      List<String> recipeUsages = SlotType.getAllSlotTypes().stream()
                                          .filter(type -> recipeModifiers.containsEntry(type, modifier))
                                          .map(SlotType::getName)
                                          .collect(Collectors.toList());
      String recipes;
      if (recipeUsages.isEmpty()) {
        recipes = recipeModifiers.containsEntry(null, modifier) ? "slotless" : "";
      } else {
        recipes = String.join(", ", recipeUsages);
      }
      table.add(new ModifierUsageRow(modifier.getId(), recipes, toolTraits.contains(modifier), materialTraits.contains(modifier)));
    });

    // finally, output the table
    table.build(logOutput);
    TConstruct.LOG.info(logOutput.toString());
    context.getSource().sendSuccess(SUCCESS, true);
    return finalList.size();
  }

  /** Valid options for the modifier type argument */
  @RequiredArgsConstructor
  private enum ModifierUsages {
    UNUSED("Unused modifiers:"),
    RECIPE("Recipe modifiers:"),
    MATERIAL_TRAIT("Material trait modifiers:"),
    TOOL_TRAIT("Tool trait modifiers:"),
    ALL("All modifiers:");

    private final String logPrefix;
  }

  /**
   * Data class holding a single row of the output table
   */
  private record ModifierUsageRow(ModifierId modifierId, String recipe, boolean toolTrait, boolean materialTrait) {}
}
