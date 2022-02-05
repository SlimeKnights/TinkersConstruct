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
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/** Argument for a material type */
@RequiredArgsConstructor(staticName = "material")
public class MaterialArgument implements ArgumentType<IMaterial> {
  private static final Collection<String> EXAMPLES = Arrays.asList("tconstruct:wood", "tconstruct:iron");
  private static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType(name -> TConstruct.makeTranslation("command", "material.not_found", name));

  /** Gets the tool stat from the context */
  public static IMaterial getMaterial(CommandContext<CommandSourceStack> context, String name) {
    return context.getArgument(name, IMaterial.class);
  }

  @Override
  public IMaterial parse(StringReader reader) throws CommandSyntaxException {
    MaterialId name = new MaterialId(ResourceLocation.read(reader));
    IMaterial material = MaterialRegistry.getMaterial(name);
    if (material == IMaterial.UNKNOWN) {
      throw NOT_FOUND.createWithContext(reader, name);
    }
    return material;
  }

  @Override
  public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
    return SharedSuggestionProvider.suggestResource(MaterialRegistry.getMaterials().stream().<ResourceLocation>map(IMaterial::getIdentifier)::iterator, builder);
  }

  @Override
  public Collection<String> getExamples() {
    return EXAMPLES;
  }
}
