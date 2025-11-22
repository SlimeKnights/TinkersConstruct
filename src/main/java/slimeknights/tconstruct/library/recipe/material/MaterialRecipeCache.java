package slimeknights.tconstruct.library.recipe.material;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator.DuelSidedListener;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

/** Cache of details related to materials */
public class MaterialRecipeCache {
  /** Full list of recipes in the cache */
  private static final List<MaterialRecipe> RECIPES = new ArrayList<>();
  /** Lookup from item ID to recipe */
  private static final Map<Item, MaterialRecipe> RECIPE_BY_ITEM = new ConcurrentHashMap<>();
  /** Lookup from material variant ID to recipe */
  private static final Multimap<MaterialVariantId, MaterialRecipe> RECIPES_BY_MATERIAL = HashMultimap.create();
  /** Map from material variant ID to item stack list for display */
  private static final Map<MaterialVariantId, List<ItemStack>> ITEMS_BY_MATERIAL = new ConcurrentHashMap<>();

  /** Mapping from material ID to all variants for the material */
  private static final Multimap<MaterialId, MaterialVariantId> KNOWN_VARIANTS = HashMultimap.create();
  /** List of all material variants in sorted order. See also {@link IMaterialRegistry#getVisibleMaterials()} */
  @Nullable
  private static List<MaterialVariantId> SORTED_VARIANTS = null;

  /** Listener for clearing the cache */
  private static final DuelSidedListener LISTENER = RecipeCacheInvalidator.addDuelSidedListener(() -> {
    RECIPES.clear();
    RECIPE_BY_ITEM.clear();
    RECIPES_BY_MATERIAL.clear();
    ITEMS_BY_MATERIAL.clear();
    KNOWN_VARIANTS.clear();
    SORTED_VARIANTS = null;
  });

  /** Registers a recipe with the cache */
  public static void registerRecipe(MaterialRecipe recipe) {
    if (recipe.getValue() > 0) {
      // ensure c ache does not need to be cleared
      LISTENER.checkClear();
      // add recipe for item lookup; too early to resolve ingredient
      RECIPES.add(recipe);
      // mark the variant as known
      MaterialVariantId variant = recipe.getMaterial().getVariant();
      addKnownVariant(variant);
      // add lookup for the variant
      RECIPES_BY_MATERIAL.put(variant, recipe);
    }
  }

  /**
   * Locates a recipe by stack
   * @param stack  Stack to check
   * @return Recipe, or {@link MaterialRecipe#EMPTY} if no match.
   */
  public static MaterialRecipe findRecipe(ItemStack stack) {
    if (stack.isEmpty()) {
      return MaterialRecipe.EMPTY;
    }
    return RECIPE_BY_ITEM.computeIfAbsent(stack.getItem(), item -> {
      for (MaterialRecipe recipe : RECIPES) {
        if (recipe.getIngredient().test(stack)) {
          return recipe;
        }
      }
      return MaterialRecipe.EMPTY;
    });
  }

  /** Gets a list of all material recipes */
  public static Collection<MaterialRecipe> getAllRecipes() {
    return RECIPES;
  }

  /** Gets all recipes for the given material variant */
  public static Collection<MaterialRecipe> getRecipes(MaterialVariantId variant) {
    return RECIPES_BY_MATERIAL.get(variant);
  }

  /** Cache lookup function for items by materials */
  private static final Function<MaterialVariantId,List<ItemStack>> GET_ITEMS_BY_MATERIAL = variant ->
    getRecipes(variant).stream().flatMap(r -> {
      Stream<ItemStack> stacks = Arrays.stream(r.getIngredient().getItems());
      // if we need multiple, increase the stack size of the display stacks
      if (r.needed > 1) {
        int size = r.needed;
        stacks = stacks.map(stack -> stack.copyWithCount(size));
      }
      return stacks;
    }).toList();

  /** Gets all recipes for the given material variant */
  public static List<ItemStack> getItems(MaterialVariantId variant) {
    return ITEMS_BY_MATERIAL.computeIfAbsent(variant, GET_ITEMS_BY_MATERIAL);
  }


  /* Material variants */

  /** Registers a material variant for the lookups. */
  public static void addKnownVariant(MaterialVariantId variant) {
    LISTENER.checkClear();
    KNOWN_VARIANTS.put(variant.getId(), variant);
    // null cache of sorted variants as its outdated now
    SORTED_VARIANTS = null;
  }

  /** Gets a list of known material variants for the given material ID */
  public static Collection<MaterialVariantId> getVariants(MaterialId materialId) {
    Collection<MaterialVariantId> variants = KNOWN_VARIANTS.get(materialId);
    if (variants.isEmpty()) {
      return List.of(materialId);
    }
    return Collections.unmodifiableCollection(variants);
  }

  /** Gets a sorted list of all known material variants */
  public static List<MaterialVariantId> getAllVariants() {
    if (SORTED_VARIANTS == null) {
      Comparator<MaterialVariantId> variantSorter = Comparator.comparing(MaterialVariantId::getVariant);
      SORTED_VARIANTS = MaterialRegistry.getInstance().getVisibleMaterials().stream()
        .flatMap(material -> {
          // if no variants are registered, just list the material itself; useful for uncraftable materials
          MaterialId id = material.getIdentifier();
          Collection<MaterialVariantId> variants = KNOWN_VARIANTS.get(id);
          if (variants.isEmpty()) {
            return Stream.of(id);
          }
          return variants.stream().sorted(variantSorter);
        }).toList();
    }
    return SORTED_VARIANTS;
  }
}
