package slimeknights.tconstruct.library.recipe.partbuilder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.field.MergingField;
import slimeknights.tconstruct.library.json.field.MergingField.MissingMode;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.material.IMaterialValue;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic to make a tool part from material items in the part builder.
 * @see ItemPartRecipe
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PartRecipe implements IPartBuilderRecipe, IMultiRecipe<IDisplayPartBuilderRecipe> {
  public static final RecordLoadable<PartRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    LoadableRecipeSerializer.RECIPE_GROUP,
    Pattern.PARSER.requiredField("pattern", PartRecipe::getPattern),
    IngredientLoadable.DISALLOW_EMPTY.defaultField("pattern_item", DEFAULT_PATTERNS, r -> r.patternItem),
    IntLoadable.FROM_ONE.requiredField("cost", PartRecipe::getCost),
    BooleanLoadable.INSTANCE.defaultField("allow_uncraftable", false, false, r -> r.allowUncraftable),
    new MergingField<>(TinkerLoadables.MATERIAL_ITEM.requiredField("item", r -> r.output), "result", MissingMode.DISALLOWED),
    new MergingField<>(IntLoadable.FROM_ONE.defaultField("count", 1, r -> r.outputCount), "result", MissingMode.CREATE),
    PartRecipe::new);

  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final Pattern pattern;
  protected final Ingredient patternItem;
  /** Recipe material cost */
  @Getter
  protected final int cost;
  /** If true, this recipe can craft items normally not craftable in the part builder */
  @Getter
  @Accessors(fluent = true)
  protected final boolean allowUncraftable;
  /** Recipe result, used to fetch a material */
  protected final IMaterialItem output;
  /** Count for the recipe output */
  protected final int outputCount;

  /** @deprecated use {@link #PartRecipe(ResourceLocation, String, Pattern, Ingredient, int, boolean, IMaterialItem, int)} */
  @Deprecated(forRemoval = true)
  public PartRecipe(ResourceLocation id, String group, Pattern pattern, Ingredient patternItem, int cost, IMaterialItem output, int outputCount) {
    this(id, group, pattern, patternItem, cost, false, output, outputCount);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.partRecipeSerializer.get();
  }

  @Override
  public boolean partialMatch(IPartBuilderContainer inv) {
    // first, must have a pattern
    if (!patternItem.test(inv.getPatternStack())) {
      return false;
    }
    // if there is a material item, it must have a valid material and be craftable
    ItemStack stack = inv.getStack();
    if (!stack.isEmpty()) {
      // no sense allowing if there is no change
      if (stack.getItem() == output) {
        return false;
      }
      IMaterialValue materialRecipe = inv.getMaterial();
      if (materialRecipe == null) {
        return false;
      }
      MaterialVariant material = materialRecipe.getMaterial();
      return (allowUncraftable || material.get().isCraftable()) && output.canUseMaterial(material.getId());
    }
    // no material item? return match in case we get one later
    return true;
  }

  /**
   * Checks if the recipe is valid for the given input. Assumes {@link #partialMatch(IPartBuilderContainer)} is true
   * @param inv    Inventory instance
   * @param world  World instance
   * @return  True if this recipe matches
   */
  @Override
  public boolean matches(IPartBuilderContainer inv, Level world) {
    // must have a material
    IMaterialValue materialRecipe = inv.getMaterial();
    if (materialRecipe != null) {
      // material must be craftable, usable in the item, and have a cost we can afford
      MaterialVariant material = materialRecipe.getMaterial();
      return (allowUncraftable || material.get().isCraftable()) && output.canUseMaterial(material.getId())
             && inv.getStack().getCount() >= materialRecipe.getItemsUsed(cost);
    }
    return false;
  }

  /** @deprecated use {@link #getRecipeOutput(MaterialVariantId)} */
  @Deprecated
  @Override
  public ItemStack getResultItem(RegistryAccess access) {
    return new ItemStack(output);
  }

  /**
   * Gets the output of the recipe for display
   * @param material  Material to use
   * @return  Output of the recipe
   */
  @SuppressWarnings("WeakerAccess")
  public ItemStack getRecipeOutput(MaterialVariantId material, int count) {
    ItemStack stack = output.withMaterial(material);
    stack.setCount(count);
    return stack;
  }

  /** @deprecated use {@link #getRecipeOutput(MaterialVariantId, int)} */
  @Deprecated(forRemoval = true)
  public ItemStack getRecipeOutput(MaterialVariantId material) {
    return getRecipeOutput(material, outputCount);
  }

  @Override
  public ItemStack assemble(IPartBuilderContainer inv, RegistryAccess access) {
    MaterialVariant material = MaterialVariant.UNKNOWN;
    int count = outputCount;
    IMaterialValue materialRecipe = inv.getMaterial();
    if (materialRecipe != null) {
      material = materialRecipe.getMaterial();
      // if no leftover, give them more parts provided we have the patterns for it
      int value = materialRecipe.getValue();
      if (!materialRecipe.hasLeftover() && value > cost) {
        count = outputCount * value / cost;
      }
    }
    return this.getRecipeOutput(material.getVariant(), count);
  }

  /** Cache of recipes for display in JEI */
  @Nullable
  private List<IDisplayPartBuilderRecipe> multiRecipes;

  @Override
  public List<IDisplayPartBuilderRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      // start building the recipe
      List<MaterialVariant> materials = new ArrayList<>();
      List<ItemStack> materialItems = new ArrayList<>();
      List<ItemStack> hiddenInputs = new ArrayList<>();
      List<ItemStack> resultItems = new ArrayList<>();

      // iterate materials to generate recipes
      for (IMaterial material : MaterialRegistry.getMaterials()) {
        // require the material to be craftable and valid for this part
        if ((allowUncraftable || material.isCraftable()) && output.canUseMaterial(material)) {
          List<MaterialVariantId> variants = MaterialRecipeCache.getVariants(material.getIdentifier())
            .stream().filter(id -> !MaterialRecipeCache.getRecipes(id).isEmpty()).toList();
          if (variants.isEmpty()) continue;

          // if the size is 1, add the variant itself as display variant
          MaterialVariantId id;
          MaterialVariant variant;
          List<ItemStack> newItems = new ArrayList<>();
          if (variants.size() == 1) {
            id = variants.get(0);
            variant = MaterialVariant.of(id);

            // process items
            MaterialRecipeCache.addItems(id, cost, newItems);
          } else {
            // if we have multiple variants, use the base as display variant
            id = material.getIdentifier();
            variant = MaterialVariant.of(material);

            // process items
            for (MaterialVariantId variantId : variants) {
              MaterialRecipeCache.addItems(variantId, cost, newItems);
            }
          }

          // if no items, skip this material - shouldn't happen
          if (newItems.isEmpty()) continue;

          // first item becomes our display item
          materialItems.add(newItems.get(0));
          // all other items become focusable
          hiddenInputs.addAll(newItems.subList(1, newItems.size()));

          // add the material info
          materials.add(variant);
          resultItems.add(output.withMaterialForDisplay(id));
        }
      }

      // safety: make sure we got results
      if (materials.isEmpty()) {
        multiRecipes = List.of();
      } else {
        multiRecipes = List.of(new DisplayRecipe(
          List.copyOf(materialItems), List.copyOf(materials), List.copyOf(resultItems),
          List.of(patternItem.getItems()), List.copyOf(hiddenInputs))
        );
      }
    }
    return multiRecipes;
  }

  /** Gets the display items for the given material */
  public static List<ItemStack> getItems(MaterialVariantId id, int cost) {
    List<ItemStack> stacks = new ArrayList<>();
    if (id.hasVariant()) {
      MaterialRecipeCache.addItems(id, cost, stacks);
    } else {
      for (MaterialVariantId variant : MaterialRecipeCache.getVariants(id.getId())) {
        MaterialRecipeCache.addItems(variant, cost, stacks);
      }
    }
    return stacks;
  }

  /** Display recipe handling dynamic focus */
  @Getter
  @RequiredArgsConstructor
  private class DisplayRecipe implements IDisplayPartBuilderRecipe.DisplayOnly {
    private final List<ItemStack> materialItems;
    private final List<MaterialVariant> materials;
    private final List<ItemStack> resultItems;
    private final List<ItemStack> patternItems;
    private final List<ItemStack> hiddenInputs;

    @Override
    public ResourceLocation getId() {
      return id;
    }

    @Override
    public int getCost() {
      return cost;
    }

    @Override
    public Pattern getPattern() {
      return pattern;
    }

    @Override
    public MaterialVariant getMaterial() {
      return materials.get(0);
    }

    @Override
    public List<MaterialVariant> getMaterials(MaterialVariant focusMaterial, ItemStack focusStack, boolean focusOutput) {
      // if focusing on a material, just display that
      if (!focusMaterial.isEmpty()) {
        // if it's a base ID, animate its variant recipes
        return List.of(focusMaterial);
      }
      // if focusing on an item, use it to determine our material
      if (!focusStack.isEmpty()) {
        if (focusOutput) {
          // outputs are tool parts, so ask the material item
          MaterialVariantId variant = output.getMaterial(focusStack);
          if (!MaterialId.UNKNOWN.equals(variant)) {
            // if it's a base ID, animate its variant recipes
            return List.of(MaterialVariant.of(variant));
          }
        } else {
          // if the item has a material, return that directly, no need to animate
          MaterialRecipe recipe = MaterialRecipeCache.findRecipe(focusStack);
          if (recipe != MaterialRecipe.EMPTY) {
            return List.of(recipe.getMaterial());
          }
        }
      }
      return getMaterials();
    }

    @Override
    public List<ItemStack> getMaterialItems(MaterialVariant focusMaterial, ItemStack focusStack, boolean focusOutput) {
      if (!focusMaterial.isEmpty()) {
        return PartRecipe.getItems(focusMaterial.getId(), cost);
      }
      if (!focusStack.isEmpty()) {
        if (focusOutput) {
          // outputs are tool parts, so ask the material item
          MaterialVariantId variant = output.getMaterial(focusStack);
          if (!MaterialId.UNKNOWN.equals(variant)) {
            return PartRecipe.getItems(variant, cost);
          }
        } else {
          // if the item has a material, return it directly, no need to animate
          MaterialRecipe recipe = MaterialRecipeCache.findRecipe(focusStack);
          if (recipe != MaterialRecipe.EMPTY) {
            return List.of(focusStack.copyWithCount(recipe.getItemsUsed(cost)));
          }
        }
      }
      return materialItems;
    }

    @Override
    public List<ItemStack> getResultItems(MaterialVariant focusMaterial, ItemStack focusStack, boolean focusOutput) {
      if (!focusMaterial.isEmpty()) {
        return List.of(output.withMaterial(focusMaterial.getVariant()));
      }
      if (!focusStack.isEmpty()) {
        if (focusOutput) {
          MaterialVariantId variant = output.getMaterial(focusStack);
          if (!MaterialId.UNKNOWN.equals(variant)) {
            return List.of(output.withMaterial(variant));
          }
        } else {
          MaterialRecipe recipe = MaterialRecipeCache.findRecipe(focusStack);
          if (recipe != MaterialRecipe.EMPTY) {
            return List.of(output.withMaterial(recipe.getMaterial().getVariant()));
          }
        }
      }
      return resultItems;
    }
  }
}
