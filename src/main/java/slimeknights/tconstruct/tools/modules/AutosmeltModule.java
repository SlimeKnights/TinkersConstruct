package slimeknights.tconstruct.tools.modules;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.array.ArrayLoadable;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.recipe.SingleItemContainer;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/** Module implementing autosmelt */
@RequiredArgsConstructor
public class AutosmeltModule implements ModifierModule, ProcessLootModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<AutosmeltModule>defaultHooks(ModifierHooks.PROCESS_LOOT);
  @SuppressWarnings("unchecked") // no way to check, its generics with no concrete value
  public static final RecordLoadable<AutosmeltModule> LOADER = RecordLoadable.create(
    Loadables.RECIPE_TYPE.<RecipeType<? extends AbstractCookingRecipe>>flatXmap(type -> (RecipeType<? extends AbstractCookingRecipe>)type, type -> type)
      .list(ArrayLoadable.COMPACT).requiredField("recipe_types", m -> m.recipeTypes),
    FloatLoadable.PERCENT.requiredField("extra_drop_chance", m -> m.extraDropChance),
    AutosmeltModule::new);
  /** Inventory instance to use for recipe search */
  private static final SingleItemContainer INVENTORY = new SingleItemContainer();

  private final List<RecipeType<? extends AbstractCookingRecipe>> recipeTypes;
  private final float extraDropChance;
  /** Cache of relevant smelting recipes */
  private final Cache<Item, Optional<? extends AbstractCookingRecipe>> recipeCache = CacheBuilder
    .newBuilder()
    .maximumSize(64)
    .build();

  @SafeVarargs
  public AutosmeltModule(float extraDropChance, RecipeType<? extends AbstractCookingRecipe>... types) {
    this(List.of(types), extraDropChance);
  }

  @Override
  public RecordLoadable<AutosmeltModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /**
   * Called by the deprecated {@code AutosmeltModifier} to clear cache on reload.
   * @apiNote Will be removed in 1.21 when the deprecated class is removed.
   */
  @Deprecated
  @Internal
  public void clearCache() {
    recipeCache.invalidateAll();
  }

  /**
   * Gets a furnace recipe without using the cache
   * @param stack  Stack to try
   * @param world  World instance
   * @return  Furnace recipe
   */
  private Optional<? extends AbstractCookingRecipe> findRecipe(ItemStack stack, Level world) {
    INVENTORY.setStack(stack);
    // try each recipe type to see if we have a recipe for any of them
    Optional<? extends AbstractCookingRecipe> recipe = Optional.empty();
    for (RecipeType<? extends AbstractCookingRecipe> recipeType : recipeTypes) {
      recipe = world.getRecipeManager().getRecipeFor(recipeType, INVENTORY, world);
      if (recipe.isPresent()) {
        break;
      }
    }
    INVENTORY.setStack(ItemStack.EMPTY);
    return recipe;
  }

  /**
   * Gets a cached furnace recipe
   * @param stack  Stack for recipe
   * @param world  World instance
   * @return Cached recipe
   */
  @Nullable
  private AbstractCookingRecipe findCachedRecipe(ItemStack stack, Level world) {
    // don't use the cache if there is a tag, prevent breaking NBT sensitive recipes
    if (stack.hasTag()) {
      return findRecipe(stack, world).orElse(null);
    }
    try {
      return recipeCache.get(stack.getItem(), () -> findRecipe(stack, world)).orElse(null);
    } catch (ExecutionException e) {
      return null;
    }
  }

  /**
   * Smelts an item using the relevant furnace recipe
   * @param stack  Stack to smelt
   * @param world  World instance
   * @return  Smelted item, or original if no recipe
   */
  private ItemStack smeltItem(ItemStack stack, Level world) {
    // skip blacklisted entries
    if (stack.is(TinkerTags.Items.AUTOSMELT_BLACKLIST)) {
      return stack;
    }
    AbstractCookingRecipe recipe = findCachedRecipe(stack, world);
    if (recipe != null) {
      // fetch recipe result, may be input sensitive
      INVENTORY.setStack(stack);
      ItemStack output = recipe.assemble(INVENTORY, world.registryAccess());
      INVENTORY.setStack(ItemStack.EMPTY);
      // scale the stack size based on the input size
      if (stack.getCount() > 1) {
        // recipe output is a copy, safe to modify
        output.setCount(output.getCount() * stack.getCount());
      }
      return output;
    }
    return stack;
  }

  @Override
  public void processLoot(IToolStackView tool, ModifierEntry modifier, List<ItemStack> generatedLoot, LootContext context) {
    Level world = context.getLevel();
    if (!generatedLoot.isEmpty()) {
      ListIterator<ItemStack> iterator = generatedLoot.listIterator();
      while (iterator.hasNext()) {
        ItemStack stack = iterator.next();
        ItemStack smelted = smeltItem(stack, world);
        if (stack != smelted) {
          int level = modifier.intEffectiveLevel();

          // at higher levels of autosmelt, randomly boost the drops assuming chance is above 0
          if (level > 1 && extraDropChance > 0 && !smelted.is(TinkerTags.Items.AUTOSMELT_PLUS_BLACKLIST)) {
            // will add at most this number of items
            int extraItems = stack.getCount() * (level - 1);
            int bonus = 0;
            // if the chance is 1, just give them all and save the loop
            if (extraDropChance >= 1) {
              bonus = extraItems;
            } else {
              for (int i = 0; i < extraItems; i++) {
                if (context.getRandom().nextFloat() < extraDropChance) {
                  bonus += 1;
                }
              }
            }
            smelted.grow(bonus);
          }
          iterator.set(smelted);
        }
      }
    }
  }
}
