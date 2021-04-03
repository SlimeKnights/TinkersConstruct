package slimeknights.tconstruct.tools.modifiers.ability;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.world.World;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.recipe.SingleItemInventory;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class AutosmeltModifier extends SingleUseModifier {
  /** Cache of relevant smelting recipes */
  private final Cache<Item,Optional<FurnaceRecipe>> recipeCache = CacheBuilder
    .newBuilder()
    .maximumSize(64)
    .build();
  /** Inventory instance to use for recipe search */
  private final SingleItemInventory inventory = new SingleItemInventory();

  public AutosmeltModifier() {
    super(0xBA541C);
    RecipeCacheInvalidator.addReloadListener(client -> {
      if (!client) {
        recipeCache.invalidateAll();
      }
    });
  }

  /**
   * Gets a furnace recipe without using the cache
   * @param stack  Stack to try
   * @param world  World instance
   * @return  Furnace recipe
   */
  private Optional<FurnaceRecipe> findRecipe(ItemStack stack, World world) {
    inventory.setStack(stack);
    return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, inventory, world);
  }

  /**
   * Gets a cached furnace recipe
   * @param stack  Stack for recipe
   * @param world  World instance
   * @return Cached recipe
   */
  @Nullable
  private FurnaceRecipe findCachedRecipe(ItemStack stack, World world) {
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
  private ItemStack smeltItem(ItemStack stack, World world) {
    // skip blacklisted entries
    if (TinkerTags.Items.AUTOSMELT_BLACKLIST.contains(stack.getItem())) {
      return stack;
    }
    FurnaceRecipe recipe = findCachedRecipe(stack, world);
    if (recipe != null) {
      inventory.setStack(stack);
      ItemStack output = recipe.getCraftingResult(inventory);
      if (stack.getCount() > 1) {
        // recipe output is a copy, safe to modify
        output.setCount(output.getCount() * stack.getCount());
      }
      return output;
    }
    return stack;
  }

  @Override
  public List<ItemStack> processLoot(ToolStack tool, int level, List<ItemStack> generatedLoot, LootContext context) {
    World world = context.getWorld();
    if (!generatedLoot.isEmpty()) {
      return generatedLoot.stream()
                          .map(stack -> smeltItem(stack, world))
                          .filter(stack -> !stack.isEmpty())
                          .collect(Collectors.toList());
    }
    return generatedLoot;
  }
}
