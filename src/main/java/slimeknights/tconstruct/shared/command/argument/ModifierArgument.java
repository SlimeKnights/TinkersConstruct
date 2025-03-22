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
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.utils.IdParser;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/** Argument type for a modifier */
@NoArgsConstructor(staticName = "modifier")
public class ModifierArgument implements ArgumentType<Modifier> {
  private static final Collection<String> EXAMPLES = Arrays.asList("tconstruct:haste", "tconstruct:luck");
  private static final DynamicCommandExceptionType MODIFIER_NOT_FOUND = new DynamicCommandExceptionType(name -> TConstruct.makeTranslation("command", "modifier.not_found", name));

  @Override
  public Modifier parse(StringReader reader) throws CommandSyntaxException {
    ModifierId loc = new ModifierId(IdParser.read(TConstruct.MOD_ID, reader));
    if (!ModifierManager.INSTANCE.contains(loc)) {
      throw MODIFIER_NOT_FOUND.create(loc);
    }
    return ModifierManager.getValue(loc);
  }

  /** Gets a modifier from the command context */
  public static Modifier getModifier(CommandContext<CommandSourceStack> context, String name) {
    return context.getArgument(name, Modifier.class);
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return TinkerSuggestionProvider.suggestResource(TConstruct.MOD_ID, ModifierManager.INSTANCE.getAllValues(), builder, Modifier::getId, Modifier::getDisplayName);
  }

  @Override
  public Collection<String> getExamples() {
    return EXAMPLES;
  }
}
