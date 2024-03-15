package slimeknights.tconstruct.shared.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.shared.command.argument.SlotTypeArgument.OptionalSlotType;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/** Argument of a modifier slot type */
@RequiredArgsConstructor(staticName = "slotType")
public class SlotTypeArgument implements ArgumentType<OptionalSlotType> {
  private static final Collection<String> EXAMPLES = Arrays.asList("upgrades", "abilities");
  private static final DynamicCommandExceptionType SLOT_TYPE_NOT_FOUND = new DynamicCommandExceptionType(name -> Component.translatable("command.tconstruct.slot_type.not_found", name));

  /** If true, slotless is allowed, producing null for a filter */
  private final boolean allowSlotless;

  /** Makes a slot type argument for no types */
  public static SlotTypeArgument slotType() {
    return slotType(true);
  }

  /** Gets a modifier from the command context */
  public static OptionalSlotType getOptional(CommandContext<CommandSourceStack> context, String name) {
    return context.getArgument(name, OptionalSlotType.class);
  }

  /** Gets a modifier from the command context */
  public static SlotType getSlotType(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
    SlotType slot = getOptional(context, name).slotType();
    if (slot == null) {
      throw SLOT_TYPE_NOT_FOUND.create("slotless");
    }
    return slot;
  }

  @Override
  public OptionalSlotType parse(StringReader reader) throws CommandSyntaxException {
    String name = reader.readString();
    if (allowSlotless && name.equals("slotless")) {
      return new OptionalSlotType(null);
    }
    SlotType type = SlotType.getIfPresent(name);
    if (type != null) {
      return new OptionalSlotType(type);
    }
    throw SLOT_TYPE_NOT_FOUND.createWithContext(reader, name);
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    Stream<String> stream = SlotType.getAllSlotTypes().stream().map(SlotType::getName);
    if (allowSlotless) {
      stream = Stream.concat(stream, Stream.of("slotless"));
    }
    return SharedSuggestionProvider.suggest(stream, builder);
  }

  @Override
  public Collection<String> getExamples() {
    return EXAMPLES;
  }

  /** Object holding a nullable slot type */
  public record OptionalSlotType(@Nullable SlotType slotType) {}
}
