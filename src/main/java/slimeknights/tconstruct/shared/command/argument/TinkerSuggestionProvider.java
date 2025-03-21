package slimeknights.tconstruct.shared.command.argument;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static net.minecraft.commands.SharedSuggestionProvider.matchesSubStr;

/** Suggestion helpers for Tinkers' Construct commands */
public interface TinkerSuggestionProvider {
  /**
   * Reimplementation of {@link net.minecraft.commands.SharedSuggestionProvider#filterResources(Iterable, String, Function, Consumer)} with an argument to change the default namespace.
   * @param defaultDomain   Default domain to use if the domain is unset
   * @param resources       List of resources to filter
   * @param input           Current input string
   * @param idGetter        Function mapping the resource to its ID
   * @param resultConsumer  Consumer handling results
   * @param <T>  Resource type
   */
  static <T> void filterResources(String defaultDomain, Iterable<T> resources, String input, Function<T, ResourceLocation> idGetter, Consumer<T> resultConsumer) {
    boolean hasNamespace = input.indexOf(':') > -1;
    for(T resource : resources) {
      ResourceLocation location = idGetter.apply(resource);
      // if we have a namespace, do a complete match
      if (hasNamespace) {
        String locationStr = location.toString();
        if (matchesSubStr(input, locationStr)) {
          resultConsumer.accept(resource);
        }
        // if no namespace, match either on a starting namespace or using the default namespace
      } else if (matchesSubStr(input, location.getNamespace()) || defaultDomain.equals(location.getNamespace()) && matchesSubStr(input, location.getPath())) {
        resultConsumer.accept(resource);
      }
    }
  }

  /**
   * Reimplementation of {@link net.minecraft.commands.SharedSuggestionProvider#suggestResource(Iterable, SuggestionsBuilder)} with an argument to change the default namespace.
   * @param defaultDomain   Default domain to use if the domain is unset
   * @param resources       List of resources to filter
   * @param builder         Builder for suggestion options
   */
  static CompletableFuture<Suggestions> suggestResource(String defaultDomain, Iterable<ResourceLocation> resources, SuggestionsBuilder builder) {
    String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
    filterResources(defaultDomain, resources, remaining, loc -> loc, loc -> builder.suggest(loc.toString()));
    return builder.buildFuture();
  }

  /**
   * Reimplementation of {@link net.minecraft.commands.SharedSuggestionProvider#suggestResource(Stream, SuggestionsBuilder)} with an argument to change the default namespace.
   * @param defaultDomain   Default domain to use if the domain is unset
   * @param resources       List of resources to filter
   * @param builder         Builder for suggestion options
   */
  static CompletableFuture<Suggestions> suggestResource(String defaultDomain, Stream<ResourceLocation> resources, SuggestionsBuilder builder) {
    return suggestResource(defaultDomain, resources::iterator, builder);
  }

  /**
   * Reimplementation of {@link net.minecraft.commands.SharedSuggestionProvider#suggestResource(Iterable, SuggestionsBuilder, Function, Function)} with an argument to change the default namespace.
   * @param defaultDomain   Default domain to use if the domain is unset
   * @param resources       List of resources to filter
   * @param builder         Builder for suggestion options
   * @param idGetter        Function mapping the resource to its ID
   * @param tooltip         Function mapping elements to their tooltip
   */
  static <T> CompletableFuture<Suggestions> suggestResource(String defaultDomain, Iterable<T> resources, SuggestionsBuilder builder, Function<T, ResourceLocation> idGetter, Function<T,Message> tooltip) {
    String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
    filterResources(defaultDomain, resources, remaining, idGetter, (resource) -> builder.suggest(idGetter.apply(resource).toString(), tooltip.apply(resource)));
    return builder.buildFuture();
  }

  /**
   * Reimplementation of {@link net.minecraft.commands.SharedSuggestionProvider#suggestResource(Stream, SuggestionsBuilder, Function, Function)} with an argument to change the default namespace.
   * @param defaultDomain   Default domain to use if the domain is unset
   * @param resources       List of resources to filter
   * @param builder         Builder for suggestion options
   * @param idGetter        Function mapping the resource to its ID
   * @param tooltip         Function mapping elements to their tooltip
   */
  static <T> CompletableFuture<Suggestions> suggestResource(String defaultDomain, Stream<T> resources, SuggestionsBuilder builder, Function<T, ResourceLocation> idGetter, Function<T, Message> tooltip) {
    return suggestResource(defaultDomain, resources::iterator, builder, idGetter, tooltip);
  }
}
