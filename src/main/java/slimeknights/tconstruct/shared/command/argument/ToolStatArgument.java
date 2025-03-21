package slimeknights.tconstruct.shared.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.IdParser;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/** Argument for a tool stat type */
@SuppressWarnings("rawtypes")
@RequiredArgsConstructor(staticName = "stat")
public class ToolStatArgument<T extends IToolStat> implements ArgumentType<T> {
  private static final Collection<String> EXAMPLES = Arrays.asList("tconstruct:mining_speed", "tconstruct:durability");
  private static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType(name -> TConstruct.makeTranslation("command", "stat_type.not_found", name));
  private static final Dynamic2CommandExceptionType WRONG_TYPE = new Dynamic2CommandExceptionType((name, clazz) -> TConstruct.makeTranslation("command", "stat_type.wrong_type", name, clazz));

  /** Filter to limit types of tool stats supported */
  private final Class<T> filter;

  /** Creates a stat type argument for any tool stat */
  public static ToolStatArgument<IToolStat> stat() {
    return stat(IToolStat.class);
  }

  /** Gets the tool stat from the context */
  public static IToolStat<?> getStat(CommandContext<CommandSourceStack> context, String name) {
    return context.getArgument(name, IToolStat.class);
  }

  @Override
  public T parse(StringReader reader) throws CommandSyntaxException {
    ToolStatId name = new ToolStatId(IdParser.read(TConstruct.MOD_ID, reader));
    IToolStat<?> stat = ToolStats.getToolStat(name);
    if (stat == null) {
      throw NOT_FOUND.createWithContext(reader, name);
    }
    if (!filter.isInstance(stat)) {
      throw WRONG_TYPE.createWithContext(reader, name, filter.getSimpleName());
    }
    return filter.cast(stat);
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return TinkerSuggestionProvider.suggestResource(TConstruct.MOD_ID, ToolStats.getAllStats().stream().filter(filter::isInstance), builder, IToolStat::getName, IToolStat::getPrefix);
  }

  @Override
  public Collection<String> getExamples() {
    return EXAMPLES;
  }
}
