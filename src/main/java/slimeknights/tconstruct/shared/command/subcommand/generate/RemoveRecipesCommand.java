package slimeknights.tconstruct.shared.command.subcommand.generate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.FalseCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import slimeknights.mantle.command.MantleCommand;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.array.ArrayLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.predicate.ContextItemPredicate;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static slimeknights.mantle.util.JsonHelper.DEFAULT_GSON;

/** Command to generate recipes that disable smelting ingots in a furnace */
public class RemoveRecipesCommand {
  /** Translation key for successfully removing recipes */
  private static final String KEY_SUCCESS = TConstruct.makeTranslationKey("command", "generate.remove_recipes");
  /** Error on invalid item or tag ID */
  private static final DynamicCommandExceptionType ITEM_NOT_FOUND = new DynamicCommandExceptionType(id -> TConstruct.makeTranslation("command", "item.not_found", id));
  /** Error on invalid preset */
  private static final DynamicCommandExceptionType PRESET_NOT_FOUND = new DynamicCommandExceptionType(id -> TConstruct.makeTranslation("command", "generate.remove_recipes.preset_not_found", id));
  /** Error when failing to save a single recipe */
  private static final DynamicCommandExceptionType FAILED_SAVE = new DynamicCommandExceptionType(id -> TConstruct.makeTranslation("command", "generate.remove_recipes.failed_id", id));
  /** Loadable for saving a list of recipe types */
  public static final Loadable<List<RecipeType<?>>> RECIPE_TYPES = TinkerLoadables.RECIPE_TYPE.list(ArrayLoadable.COMPACT);
  /** Key for {@link ContextItemPredicate} for the castable item predicate */
  public static final String KEY_CASTABLE_ITEM = "castable_item";
  /** Suggestion builder for recipe IDs */
  private static final SuggestionProvider<CommandSourceStack> SUGGESTS_RECIPES = (context, builder)
    -> SharedSuggestionProvider.suggestResource(context.getSource().getRecipeManager().getRecipeIds(), builder);

  /**
   * Registers this sub command with the root command
   *
   * @param subCommand Command builder
   * @param context    Context to fetch the recipe type argument
   */
  public static void register(LiteralArgumentBuilder<CommandSourceStack> subCommand, CommandBuildContext context) {
    subCommand.requires(sender -> sender.hasPermission(MantleCommand.PERMISSION_GAME_COMMANDS))
      .then(Commands.literal("preset")
        .then(Commands.argument("preset", StringArgumentType.word())
          .executes(RemoveRecipesCommand::runPreset)))
      .then(Commands.literal("result")
        .then(Commands.argument("recipe_type", ResourceArgument.resource(context, Registries.RECIPE_TYPE))
          .then(Commands.argument("result", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM))
            .executes(RemoveRecipesCommand::runByResult)
            .then(Commands.argument("input", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM))
              .executes(RemoveRecipesCommand::runResultInput)))))
      .then(Commands.literal("input")
        .then(Commands.argument("recipe_type", ResourceArgument.resource(context, Registries.RECIPE_TYPE))
          .then(Commands.argument("input", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM))
            .executes(RemoveRecipesCommand::runByInput))))
      .then(Commands.literal("id")
        .then(Commands.argument("recipe", ResourceLocationArgument.id()).suggests(SUGGESTS_RECIPES)
          .executes(RemoveRecipesCommand::byId)));
  }

  /** Gets the item predicate */
  private static Predicate<Item> getPredicate(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
    Either<ResourceKey<Item>, TagKey<Item>> items = ResourceOrTagKeyArgument.getResourceOrTagKey(context, name, Registries.ITEM, ITEM_NOT_FOUND).unwrap();
    return items.map(
      key -> item -> item.builtInRegistryHolder().is(key),
      tag -> item -> item.builtInRegistryHolder().is(tag));
  }

  /** Runs the command for provided arguments */
  private static int runByResult(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    long startTime = System.nanoTime();
    Holder<RecipeType<?>> recipeType = ResourceArgument.getResource(context, "recipe_type", Registries.RECIPE_TYPE);
    return run(context, List.of(recipeType.get()), getPredicate(context, "result"), null, startTime);
  }

  /** Runs the command for provided arguments */
  private static int runByInput(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    long startTime = System.nanoTime();
    Holder<RecipeType<?>> recipeType = ResourceArgument.getResource(context, "recipe_type", Registries.RECIPE_TYPE);
    return run(context, List.of(recipeType.get()), null, getPredicate(context, "input"), startTime);
  }

  /** Runs the command for provided arguments */
  private static int runResultInput(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    long startTime = System.nanoTime();
    Holder<RecipeType<?>> recipeType = ResourceArgument.getResource(context, "recipe_type", Registries.RECIPE_TYPE);
    return run(context, List.of(recipeType.get()), getPredicate(context, "result"), getPredicate(context, "input"), startTime);
  }

  /** Gets the location of a preset in datapacks */
  public static ResourceLocation presetLocation(String preset) {
    return TConstruct.getResource("command/remove_recipes/" + preset + ".json");
  }

  /** Runs the command using a JSON preset */
  private static int runPreset(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    long startTime = System.nanoTime();
    String preset = StringArgumentType.getString(context, "preset");

    // load in the preset JSON
    ResourceLocation presetLocation = presetLocation(preset);
    ServerLevel level = context.getSource().getLevel();
    Optional<Resource> resource = level.getServer().getResourceManager().getResource(TConstruct.getResource("command/remove_recipes/" + preset + ".json"));
    if (resource.isPresent()) {
      JsonObject json = JsonHelper.getJson(resource.get(), presetLocation);
      if (json != null) {
        try {
          // load in preset context, note this is lazily initialized so it only parses recipes if needed
          ContextItemPredicate.provideContext(KEY_CASTABLE_ITEM, new CastingPredicate(level));

          // parse the preset
          IJsonPredicate<Item> remove = ItemPredicate.LOADER.getOrDefault(json, "result");
          IJsonPredicate<Item> input = ItemPredicate.LOADER.getOrDefault(json, "input");
          List<RecipeType<?>> recipeTypes = RECIPE_TYPES.getIfPresent(json, "recipe_type");

          // run command
          int result = run(context, recipeTypes,
            // map any to null to allow quick evaluation
            remove == ItemPredicate.ANY ? null : remove::matches,
            input == ItemPredicate.ANY ? null : input::matches,
            startTime);

          // clear context to prevent a level leak
          ContextItemPredicate.removeContext(KEY_CASTABLE_ITEM);
          return result;
        } catch (RuntimeException e) {
          TConstruct.LOG.error("Failed to parse preset {} from pack '{}'", presetLocation, resource.get().sourcePackId(), e);
        }
      }
    } else {
      TConstruct.LOG.error("Failed to locate preset {}", presetLocation);
    }
    throw PRESET_NOT_FOUND.create(preset);
  }

  /** Runs the command */
  @SuppressWarnings("unchecked")  // not like we are using the generics at all
  private static <C extends Container, T extends Recipe<C>> int run(CommandContext<CommandSourceStack> context, List<RecipeType<?>> recipeTypes, @Nullable Predicate<Item> removeResult, @Nullable Predicate<Item> removeInput, long startTime) {
    // iterate all recipes for the type storing recipes that craft the tag
    ServerLevel level = context.getSource().getLevel();
    RegistryAccess access = level.registryAccess();
    List<ResourceLocation> recipes = new ArrayList<>();
    for (RecipeType<?> recipeType : recipeTypes) {
      for (Recipe<?> recipe : context.getSource().getLevel().getRecipeManager().getAllRecipesFor((RecipeType<T>) recipeType)) {
        // result must match or not be requested
        if (removeResult == null || removeResult.test(recipe.getResultItem(access).getItem())) {
          // no input predicate? we are done
          if (removeInput == null) {
            recipes.add(recipe.getId());
          } else {
            // at least one ingredient must match the ingredient predicate
            ingredientLoop:
            for (Ingredient ingredient : recipe.getIngredients()) {
              for (ItemStack stack : ingredient.getItems()) {
                if (removeInput.test(stack.getItem())) {
                  recipes.add(recipe.getId());
                  break ingredientLoop;
                }
              }
            }
          }
        }
      }
    }

    // determine the path for the resulting datapack
    Path pack = GeneratePackUtil.getDatapackPath(level.getServer());
    GeneratePackUtil.saveMcmeta(pack);

    // create the object for removing recipes
    JsonObject json = new JsonObject();
    json.add("conditions", CraftingHelper.serialize(new ICondition[]{FalseCondition.INSTANCE}));
    String jsonString = DEFAULT_GSON.toJson(json);

    int successes = 0;
    Path data = pack.resolve(PackType.SERVER_DATA.getDirectory());
    for (ResourceLocation id : recipes) {
      Path path = data.resolve(id.getNamespace() + "/recipes/" + id.getPath() + ".json");
      try {
        Files.createDirectories(path.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
          writer.write(jsonString);
          successes += 1;
        }
      } catch(IOException e){
        TConstruct.LOG.error("Couldn't save recipe {}", id, e);
      }
    }

    // send success
    int successFinal = successes;
    float time = (System.nanoTime() - startTime) / 1000000f;
    context.getSource().sendSuccess(() -> Component.translatable(KEY_SUCCESS, successFinal, time, GeneratePackUtil.getOutputComponent(pack)), true);
    return successes;
  }

  /** Removes a recipe by ID */
  private static int byId(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    long startTime = System.nanoTime();
    ResourceLocation id = ResourceLocationArgument.getId(context, "recipe");

    // determine the path for the resulting datapack
    Path pack = GeneratePackUtil.getDatapackPath(context.getSource().getServer());
    GeneratePackUtil.saveMcmeta(pack);

    // create the object for removing recipes
    JsonObject json = new JsonObject();
    json.add("conditions", CraftingHelper.serialize(new ICondition[]{FalseCondition.INSTANCE}));
    String jsonString = DEFAULT_GSON.toJson(json);

    Path data = pack.resolve(PackType.SERVER_DATA.getDirectory());
    Path path = data.resolve(id.getNamespace() + "/recipes/" + id.getPath() + ".json");
    try {
      Files.createDirectories(path.getParent());
      try (BufferedWriter writer = Files.newBufferedWriter(path)) {
        writer.write(jsonString);
      }
    } catch(IOException e){
      TConstruct.LOG.error("Couldn't save recipe {}", id, e);
      throw FAILED_SAVE.create(id);
    }

    // send success
    float time = (System.nanoTime() - startTime) / 1000000f;
    context.getSource().sendSuccess(() -> Component.translatable(KEY_SUCCESS, 1, time, GeneratePackUtil.getOutputComponent(pack)), true);
    return 1;
  }

  /**
   * Lazily loaded predicate matching any castable item.
   * FIXME: this is a pretty big hack, is there a better way to do this? Probably requires a cache builder plus an API way to get the tag from an output.
   */
  @RequiredArgsConstructor
  private static class CastingPredicate implements Predicate<Item> {
    private final Level level;
    private Set<Item> items = null;

    /** Gets the set of matching items */
    private Set<Item> getItems() {
      if (items == null) {
        List<Item> builder = new ArrayList<>();
        RegistryAccess access = level.registryAccess();
        for (ICastingRecipe recipe : level.getRecipeManager().getAllRecipesFor(TinkerRecipeTypes.CASTING_TABLE.get())) {
          ItemStack result = recipe.getResultItem(access);
          // if it's an item casting, check if it's a tag
          if (recipe instanceof ItemCastingRecipe itemCasting) {
            // not a better way right now to check if it's a tag output, so just serialize then fetch the tag from JSON
            JsonElement element = itemCasting.getResult().serialize(false);
            if (element.isJsonObject()) {
              JsonObject json = element.getAsJsonObject();
              if (json.has("tag")) {
                for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(Loadables.ITEM_TAG.getIfPresent(json, "tag"))) {
                  builder.add(holder.get());
                }
                continue;
              }
            }
          }
          // not a tag output? add the single item
          builder.add(result.getItem());
        }
        items = Set.copyOf(builder);
      }
      return items;
    }

    @Override
    public boolean test(Item item) {
      return getItems().contains(item);
    }
  }
}
