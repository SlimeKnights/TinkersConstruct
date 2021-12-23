package slimeknights.tconstruct.shared.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.NoArgsConstructor;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.modifiers.Modifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/** Argument type for a modifier */
@NoArgsConstructor(staticName = "modifier")
public class ModifierArgument implements ArgumentType<Modifier> {
  private static final Collection<String> EXAMPLES = Arrays.asList("tconstruct:haste", "tconstruct:luck");
  private static final DynamicCommandExceptionType MODIFIER_NOT_FOUND = new DynamicCommandExceptionType(name -> TConstruct.makeTranslation("command", "modifier", name));

  @Override
  public Modifier parse(StringReader reader) throws CommandSyntaxException {
    ResourceLocation loc = ResourceLocation.read(reader);
    if (!TinkerRegistries.MODIFIERS.containsKey(loc)) {
      throw MODIFIER_NOT_FOUND.create(loc);
    }
    return Objects.requireNonNull(TinkerRegistries.MODIFIERS.getValue(loc));
  }

  /** Gets a modifier from the command context */
  public static Modifier getModifier(CommandContext<CommandSource> context, String name) {
    return context.getArgument(name, Modifier.class);
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return ISuggestionProvider.suggestIterable(TinkerRegistries.MODIFIERS.getKeys(), builder);
  }

  @Override
  public Collection<String> getExamples() {
    return EXAMPLES;
  }
}
