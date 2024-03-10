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
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/** Argument type for a modifier */
@NoArgsConstructor(staticName = "modifierHook")
public class ModifierHookArgument implements ArgumentType<ModifierHook<?>> {
  private static final Collection<String> EXAMPLES = Arrays.asList("tconstruct:tool_stats", "tconstruct:tooltip");
  private static final DynamicCommandExceptionType HOOK_NOT_FOUND = new DynamicCommandExceptionType(name -> TConstruct.makeTranslation("command", "modifier_hook.not_found", name));

  @Override
  public ModifierHook<?> parse(StringReader reader) throws CommandSyntaxException {
    ResourceLocation loc = ResourceLocation.read(reader);
    ModifierHook<?> hook = ModifierHooks.getHook(loc);
    if (hook == null) {
      throw HOOK_NOT_FOUND.create(loc);
    }
    return hook;
  }

  /** Gets a modifier from the command context */
  public static ModifierHook<?> getModifier(CommandContext<CommandSourceStack> context, String name) {
    return context.getArgument(name, ModifierHook.class);
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return SharedSuggestionProvider.suggestResource(ModifierHooks.listAllIDs(), builder);
  }

  @Override
  public Collection<String> getExamples() {
    return EXAMPLES;
  }
}
