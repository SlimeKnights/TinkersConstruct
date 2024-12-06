package slimeknights.tconstruct.shared.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.NoArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/** Argument type for a material stat type */
@NoArgsConstructor(staticName = "stats")
public class MaterialStatsArgument implements ArgumentType<MaterialStatType<?>> {
    private static final Collection<String> EXAMPLES = Arrays.asList("tconstruct:head", "tconstruct:limb");
    private static final DynamicCommandExceptionType MODIFIER_NOT_FOUND = new DynamicCommandExceptionType(name -> TConstruct.makeTranslation("command", "material_stat.not_found", name));

    @Override
    public MaterialStatType<?> parse(StringReader reader) throws CommandSyntaxException {
      MaterialStatsId loc = new MaterialStatsId(ResourceLocation.read(reader));
      MaterialStatType<?> statType = MaterialRegistry.getInstance().getStatType(loc);
      if (statType == null) {
        throw MODIFIER_NOT_FOUND.create(loc);
      }
      return statType;
    }

    /** Gets a modifier from the command context */
    public static MaterialStatType<?> getStat(CommandContext<CommandSourceStack> context, String name) {
      return context.getArgument(name, MaterialStatType.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      return SharedSuggestionProvider.suggestResource(MaterialRegistry.getInstance().getAllStatTypeIds(), builder);
    }

    @Override
    public Collection<String> getExamples() {
      return EXAMPLES;
    }
  }
