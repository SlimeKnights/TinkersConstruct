package slimeknights.tconstruct.shared.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.shared.command.SlotTypeArgument.SlotTypeFilter;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@NoArgsConstructor(staticName = "slotType")
public class SlotTypeArgument implements ArgumentType<SlotTypeFilter> {
  /* Tag collection name is invalid */
  private static final DynamicCommandExceptionType SLOT_TYPE_NOT_FOUND = new DynamicCommandExceptionType(name -> new TranslationTextComponent("command.tconstruct.slot_type.not_found", name));

  @Override
  public SlotTypeFilter parse(StringReader reader) throws CommandSyntaxException {
    String name = reader.readString();
    if (name.equals("slotless")) {
      return new SlotTypeFilter(null);
    }
    SlotType type = SlotType.getIfPresent(name);
    if (type != null) {
      return new SlotTypeFilter(type);
    }
    throw SLOT_TYPE_NOT_FOUND.create(name);
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return ISuggestionProvider.suggest(Stream.concat(Stream.of("slotless"), SlotType.getAllSlotTypes().stream().map(SlotType::getName)), builder);
  }

  @Data
  public static class SlotTypeFilter {
    @Nullable
    private final SlotType slotType;
  }
}
