package slimeknights.tconstruct.shared.command.subcommand;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import lombok.RequiredArgsConstructor;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.apache.commons.lang3.mutable.MutableInt;
import slimeknights.mantle.command.GeneratePackHelper;
import slimeknights.mantle.command.MantleCommand;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.item.ItemPredicate;
import slimeknights.mantle.fluid.transfer.FluidContainerTransferManager;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer.TransferDirection;
import slimeknights.mantle.fluid.transfer.IFluidContainerTransfer.TransferResult;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.LogicHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeLookup;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeLookup.MeltingFluid;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/** Generates melting recipes based on crafting recipes */
public class GenerateMeltingRecipesCommand {
  /** Location of the config JSON file */
  public static final ResourceLocation MELTING_CONFIGURATION = TConstruct.getResource("command/generate_melting_recipes.json");
  /** KEY for successfully running command */
  private static final String KEY_SUCCESS = TConstruct.makeTranslationKey("command", "generate.melting_recipes");
  /** Error on invalid config JSON */
  private static final SimpleCommandExceptionType CONFIG_INVALID = new SimpleCommandExceptionType(TConstruct.makeTranslation("command", "generate.melting_recipes.invalid_config"));
  /** Recipes to skip when considering melting */
  private static final Loadable<List<ResourceLocation>> SKIP_RECIPES = Loadables.RESOURCE_LOCATION.list(0);

  /**
   * Registers this sub command with the root command
   *
   * @param subCommand Command builder
   * @param context    Context to fetch the recipe type argument
   */
  public static void register(LiteralArgumentBuilder<CommandSourceStack> subCommand, CommandBuildContext context) {
    subCommand.requires(sender -> sender.hasPermission(MantleCommand.PERMISSION_GAME_COMMANDS))
      .then(Commands.argument("recipe_type", ResourceArgument.resource(context, Registries.RECIPE_TYPE))
        .executes(GenerateMeltingRecipesCommand::run));
  }

  /** Runs the command */
  @SuppressWarnings("unchecked")  // not like we are using the generics at all
  private static <C extends Container, T extends Recipe<C>> int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    long startTime = System.nanoTime();
    Holder<RecipeType<?>> recipeType = ResourceArgument.getResource(context, "recipe_type", Registries.RECIPE_TYPE);

    // determine the path for the resulting datapack
    ServerLevel level = context.getSource().getLevel();
    Path pack = GeneratePackHelper.getDatapackPath(level.getServer());
    GeneratePackHelper.saveMcmeta(pack);

    // load in configuration from JSON, gives far more control than command arguments can fit and lets us ship a default
    // anything matching this may receive a recipe
    IJsonPredicate<Item> melt;
    IJsonPredicate<Item> inputs;
    IJsonPredicate<Item> ignore;
    List<ResourceLocation> skipRecipes;
    Optional<Resource> resource = level.getServer().getResourceManager().getResource(MELTING_CONFIGURATION);
    config: {
      if (resource.isPresent()) {
        JsonObject configuration = JsonHelper.getJson(resource.get(), MELTING_CONFIGURATION);
        if (configuration != null) {
          try {
            melt = ItemPredicate.LOADER.getOrDefault(configuration, "melt");
            inputs = ItemPredicate.LOADER.getOrDefault(configuration, "inputs");
            ignore = ItemPredicate.LOADER.getOrDefault(configuration, "ignore");
            skipRecipes = SKIP_RECIPES.getOrDefault(configuration, "skip_recipes", List.of());
            break config;
          } catch (JsonParseException e) {
            TConstruct.LOG.error("Failed to parse configuration {} from pack '{}'", MELTING_CONFIGURATION, resource.get().sourcePackId(), e);
          }
        }
      }
      throw CONFIG_INVALID.create();
    }

    // iterate all recipes for the type storing recipes that craft the tag
    RegistryAccess access = level.registryAccess();
    Comparator<MeltingResult> nameComparator = Comparator.<MeltingResult,ResourceLocation>comparing(r -> Loadables.FLUID.getKey(r.fluid.getFluid())).reversed();
    MutableInt successes = new MutableInt(0);
    Path data = pack.resolve(PackType.SERVER_DATA.getDirectory());
    Consumer<FinishedRecipe> consumer = recipe -> {
      ResourceLocation id = recipe.getId();
      Path path = data.resolve(id.getNamespace() + "/recipes/" + id.getPath() + ".json");
      if (GeneratePackHelper.saveJson(recipe.serializeRecipe(), path)) {
        successes.increment();
      }
    };

    // start building a list of melting recipes, but don't save them yet; duplicate recipes means we may need to take an intersection
    Map<Item,List<MeltingResult>> newRecipes = new HashMap<>();
    BiFunction<List<MeltingResult>,List<MeltingResult>,List<MeltingResult>> merger = (first, second) -> MeltingResult.intersection(nameComparator, first, second);

    // iterate all recipes and try adding a melting recipe
    MeltingCache cache = new MeltingCache();
    for (Recipe<?> recipe : level.getRecipeManager().getAllRecipesFor((RecipeType<T>) recipeType.get())) {
      // skip any recipes that are specifically blacklisted
      if (skipRecipes.contains(recipe.getId())) {
        continue;
      }
      ItemStack resultStack = recipe.getResultItem(access);
      // don't bother with results that have NBT unless its a damagable item, in which case we ignore NBT and hope for the best
      // also skip anything already meltable
      Item result = resultStack.getItem();
      if (resultStack.isEmpty() || (resultStack.hasTag() && !result.canBeDepleted()) || !melt.matches(result) || MeltingRecipeLookup.canMelt(result)) {
        continue;
      }
      List<MeltingResult> fluids = new ArrayList<>();

      // in order to melt the result, we need to find what it's made of. Iterate all ingredients and turn them into a single result object
      ingredientSearch: {
        for (Ingredient ingredient : recipe.getIngredients()) {
          // skip empty ingredients, just saves some steps really
          if (ingredient.isEmpty()) {
            continue;
          }
          // for each ingredient, convert it into the smallest fluid. If we have multiple or anything invalid, give up
          MeltingResult ingredientFluid = null;
          boolean didIgnore = false; // if true, we skipped something and are ineligible for a fluid next
          for (ItemStack stack : ingredient.getItems()) {
            // if the ingredient has NBT, nothing we can do here
            // also skip if the item is disallowed as an input
            if (stack.isEmpty() || stack.hasTag() || !inputs.matches(stack.getItem())) {
              break ingredientSearch;
            }
            // first, try getting its fluid
            MeltingResult newFluid = cache.get(stack.getItem());
            // if it's not meltable, give up if its not ignorable
            if (newFluid == MeltingResult.EMPTY) {
              // if this item is not ignorable, give up.
              // if we found a fluid, give up. can't mix fluid and not fluid
              if (ingredientFluid != null || !ignore.matches(stack.getItem())) {
                break ingredientSearch;
              }
              didIgnore = true;

              // if we ignored an item, give up. Can't mix fluid and non-fluid
            } else if (didIgnore) {
              break ingredientSearch;

              // first match? set ingredient fluid
            } else if (ingredientFluid == null) {
              ingredientFluid = newFluid;
              // second match? take the smaller fluid
            } else if (MeltingResult.matches(ingredientFluid, newFluid)) {
              ingredientFluid = MeltingResult.min(ingredientFluid, newFluid);
            } else {
              break ingredientSearch;
            }
          }
          // did we find a fluid? other reason we make it this far is the ingredient was entirely ignored
          // also must have ignored no inputs, ig
          addResult:
          if (ingredientFluid != null) {
            // find an existing result that is the same type to merge into
            ListIterator<MeltingResult> iterator = fluids.listIterator();
            while (iterator.hasNext()) {
              MeltingResult current = iterator.next();
              // return non-null means they matched and we merged
              // null means they did not match
              if (MeltingResult.matches(current, ingredientFluid)) {
                iterator.set(MeltingResult.merge(current, ingredientFluid));
                break addResult;
              }
            }
            // if no existing result,
            fluids.add(ingredientFluid);
          }
        }

        // do we have a result?
        if (!fluids.isEmpty()) {
          // sort fluids by name for consistency, makes merging easier
          fluids.sort(nameComparator);

          // if the output stack size is greater than 1, shrink all inputs
          // no need to worry about the builder size, count gets ignored there
          int count = resultStack.getCount();
          if (count > 1) {
            ListIterator<MeltingResult> iterator = fluids.listIterator();
            while (iterator.hasNext()) {
              MeltingResult current = iterator.next();
              iterator.set(current.withAmount(current.fluid.getAmount() / count));
            }
          }

          // add the new recipe to the map, combining with the previous as needed
          newRecipes.merge(result, fluids, merger);
          continue;
        }
      }
      newRecipes.put(result, List.of());
    }

    // freeze the cache, prevents new generated recipes from clearing it without a full reload
    MeltingRecipeLookup.freeze();

    // add all new recipes
    Comparator<MeltingResult> resultComparator = Comparator.comparing(MeltingResult::temperature).reversed()
      .thenComparing(Comparator.comparing((MeltingResult r) -> r.fluid.getAmount()).reversed());
    for (Entry<Item,List<MeltingResult>> entry : newRecipes.entrySet()) {
      Item result = entry.getKey();
      List<MeltingResult> fluids = entry.getValue();
      if (!fluids.isEmpty()) {
        // make the biggest output the main output
        fluids.sort(resultComparator);

        // first is the result
        MeltingResult first = fluids.get(0);
        MeltingRecipeBuilder builder = MeltingRecipeBuilder.melting(Ingredient.of(result), first.toOutput(), first.temperature, 1.0f);
        for (int i = 1; i < fluids.size(); i++) {
          builder.addByproduct(fluids.get(i).toOutput());
        }
        // mark it damagable if its true
        if (result.canBeDepleted()) {
          // we don't know the proper unit size, but 10mb is pretty likely
          builder.setDamagable(10);
        }
        ResourceLocation id = Loadables.ITEM.getKey(result);
        builder.save(consumer, new ResourceLocation("tinkers_generated", "melting/" + id.getNamespace() + '/' + id.getPath()));
      }
    }

    // resume regular cache operation
    MeltingRecipeLookup.unfreeze();

    // print success
    float time = (System.nanoTime() - startTime) / 1000000f;
    context.getSource().sendSuccess(() -> Component.translatable(KEY_SUCCESS, successes.intValue(), time, GeneratePackHelper.getOutputComponent(pack)), true);
    return successes.intValue();
  }

  /** Helper to make it easier to keep tags consistent in fluid outputs */
  private record MeltingResult(FluidStack fluid, @Nullable TagKey<Fluid> tag, int temperature) {
    /** Empty instance for cache saving */
    public static final MeltingResult EMPTY = new MeltingResult(FluidStack.EMPTY, null, 0);

    /** Converts a fluid output into a melting result */
    public static MeltingResult from(MeltingFluid meltingFluid) {
      FluidOutput output = meltingFluid.result();
      FluidStack fluid = output.get();
      if (fluid.isEmpty()) {
        return EMPTY;
      }
      return new MeltingResult(fluid, output.getTag(), meltingFluid.temperature());
    }

    /** Creates a transfer from a fluid stack instance */
    public static MeltingResult from(FluidStack fluid) {
      return new MeltingResult(fluid, null, Math.max(100, fluid.getFluid().getFluidType().getTemperature(fluid) - 300));
    }

    /** Creates a copy of this with the given amount */
    private MeltingResult withAmount(int amount) {
      if (amount <= 0) {
        return EMPTY;
      }
      FluidStack copy = fluid.copy();
      copy.setAmount(amount);
      return new MeltingResult(copy, tag, temperature);
    }

    /** Creates a fluid output for this object */
    public FluidOutput toOutput() {
      if (tag != null) {
        return FluidOutput.fromTag(tag, fluid.getAmount(), fluid.getTag());
      }
      return FluidOutput.fromStack(fluid);
    }

    /** Creates a copy of the simpler result with the new amount */
    private static MeltingResult simpler(MeltingResult first, MeltingResult second) {
      // this tag being null means this is simpler, other is either null or a tag
      if (first.tag == null) {
        return first;
      }
      // this tag is not null, either they match or other is simpler as the only null
      return second;
    }

    /** Checks if two melting results are the same fluid, ignoring size */
    public static boolean matches(MeltingResult first, MeltingResult second) {
      // if both have a tag, match on tag equality
      if (first.tag != null && second.tag != null) {
        return first.tag.equals(second.tag);
      }
      // if either lack a tag, do exact fluid
      return first.fluid.isFluidEqual(second.fluid);
    }

    /** Combines two results into a larger result. Precondition is {@link #matches(MeltingResult, MeltingResult)} is true. */
    public static MeltingResult merge(MeltingResult first, MeltingResult second) {
      return simpler(first, second).withAmount(first.fluid.getAmount() + second.fluid.getAmount());
    }

    /** Takes the smaller of two results. Precondition is {@link #matches(MeltingResult, MeltingResult)} is true. */
    public static MeltingResult min(MeltingResult first, MeltingResult second) {
      int firstAmount = first.fluid.getAmount();
      int secondAmount = second.fluid.getAmount();
      // same amounts? no extra work to do
      if (firstAmount == secondAmount) {
        return simpler(first, second);
      }
      // first is smaller
      if (firstAmount < secondAmount) {
        // first is simpler? no copy needed
        if (first.tag == null || second.tag != null) {
          return first;
        }
        return second.withAmount(firstAmount);
      }
      // second is smaller, if simpler also needs no copy
      if (second.tag == null || first.tag != null) {
        return second;
      }
      return first.withAmount(secondAmount);
    }

    /** Combines two lists of possible outputs to take the intersection */
    public static List<MeltingResult> intersection(Comparator<MeltingResult> comparator, List<MeltingResult> list1, List<MeltingResult> list2) {
      if (list1.isEmpty()) {
        return list1;
      }
      if (list2.isEmpty()) {
        return list2;
      }
      // iterate through the two together, keeping matching elements
      Iterator<MeltingResult> iterator1 = list1.iterator();
      Iterator<MeltingResult> iterator2 = list2.iterator();
      MeltingResult value1 = iterator1.next();
      MeltingResult value2 = iterator2.next();
      List<MeltingResult> intersection = new ArrayList<>(Math.min(list1.size(), list2.size()));
      do {
        // since the two lists are sorted using the comparator, they can only match in order. Take the min of any matches
        if (matches(value1, value2)) {
          intersection.add(MeltingResult.min(value1, value2));
          // if we reached the end of the list, we are done
          if (!iterator1.hasNext() || !iterator2.hasNext()) {
            break;
          }
          iterator1.next();
          iterator2.next();
        } else {
          // if they don't match, progress the smaller. They should never equal
          int comparison = comparator.compare(value1, value2);
          assert comparison != 0;
          Iterator<MeltingResult> toAdvance = comparison < 0 ? iterator1 : iterator2;
          // if we are out of data on the smaller, we are done
          if (!toAdvance.hasNext()) {
            break;
          }
          toAdvance.next();
        }
      }
      while (true);

      // as soon as a list is at the end, we can guarantee everything else in the other list won't have a match, so just return what we found
      // it might be empty, meaning we skip this input
      return intersection;
    }
  }

  /** Helper for locating melting recipes */
  @RequiredArgsConstructor
  private static class MeltingCache {
    private final Map<Item, MeltingResult> cache = new HashMap<>();
    private final Function<Item, MeltingResult> getter = item -> {
      ItemStack stack = new ItemStack(item);
      IFluidContainerTransfer transfer = FluidContainerTransferManager.INSTANCE.getTransfer(stack, FluidStack.EMPTY);
      if (transfer != null) {
        FluidTank tank = new FluidTank(10000);
        TransferResult transferResult = transfer.transfer(stack, FluidStack.EMPTY, tank, TransferDirection.EMPTY_ITEM);
        if (transferResult != null) {
          return MeltingResult.from(transferResult.fluid());
        }
      }
      // handle buckets directly as its faster
      if (item instanceof BucketItem bucket) {
        Fluid fluid = bucket.getFluid();
        if (fluid != Fluids.EMPTY) {
          return MeltingResult.from(new FluidStack(fluid, FluidType.BUCKET_VOLUME));
        }
      }
      // fluid capability check
      try {
        IFluidHandlerItem capability = LogicHelper.orElseNull(stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM));
        if (capability != null) {
          FluidStack contained = capability.getFluidInTank(0);
          if (!contained.isEmpty()) {
            return MeltingResult.from(contained);
          }
        }
      } catch (Exception e) {
        TConstruct.LOG.error("Failed to read fluid handler capability from {}", item, e);
      }
      return MeltingResult.from(MeltingRecipeLookup.findFluid(item));
    };

    /** Gets the value from the cache */
    public MeltingResult get(Item item) {
      return cache.computeIfAbsent(item, getter);
    }
  }
}
