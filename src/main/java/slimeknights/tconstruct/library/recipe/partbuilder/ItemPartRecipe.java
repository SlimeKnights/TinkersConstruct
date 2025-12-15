package slimeknights.tconstruct.library.recipe.partbuilder;

import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.material.IMaterialValue;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Recipe to craft an ordinary item using the part builder
 */
public class ItemPartRecipe implements IDisplayPartBuilderRecipe {
  public static final RecordLoadable<ItemPartRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    MaterialVariantId.LOADABLE.defaultField("material", IMaterial.UNKNOWN_ID, r -> r.material.getVariant()),
    Pattern.PARSER.requiredField("pattern", ItemPartRecipe::getPattern),
    IngredientLoadable.DISALLOW_EMPTY.defaultField("pattern_item", DEFAULT_PATTERNS, r -> r.patternItem),
    IntLoadable.FROM_ZERO.defaultField("cost", 0, ItemPartRecipe::getCost),
    ItemOutput.Loadable.REQUIRED_STACK.requiredField("result", r -> r.result),
    ItemPartRecipe::new).validate((recipe, error) -> {
      if (recipe.cost == 0 && !recipe.material.isEmpty()) {
        throw error.create("Cost must be greater than zero if material is defined");
      }
      return recipe;
    });

  @Getter
  private final ResourceLocation id;
  @Getter
  private final MaterialVariant material;
  @Getter
  private final Pattern pattern;
  private final Ingredient patternItem;
  @Getter
  private final int cost;
  private final ItemOutput result;

  public ItemPartRecipe(ResourceLocation id, MaterialVariantId material, Pattern pattern, Ingredient patternItem, int cost, ItemOutput result) {
    this.id = id;
    this.material = MaterialVariant.of(material);
    this.pattern = pattern;
    this.patternItem = patternItem;
    this.cost = cost;
    this.result = result;
  }

  @Override
  public boolean partialMatch(IPartBuilderContainer inv) {
    // first, must have a pattern
    if (!patternItem.test(inv.getPatternStack())) {
      return false;
    }
    // if there is a material item, it must have a valid material and be craftable
    if (!inv.getStack().isEmpty()) {
      // no material means we expect no stack in the material slot
      if (material.isEmpty()) {
        return false;
      }
      IMaterialValue materialRecipe = inv.getMaterial();
      return materialRecipe != null && material.matchesVariant(materialRecipe.getMaterial());
    }
    // no material item? return match in case we get one later
    return true;
  }

  @Override
  public boolean matches(IPartBuilderContainer inv, Level worldIn) {
    if (material.isEmpty()) {
      return inv.getStack().isEmpty();
    }
    IMaterialValue materialRecipe = inv.getMaterial();
    return materialRecipe != null && material.matchesVariant(materialRecipe.getMaterial())
           && inv.getStack().getCount() >= materialRecipe.getItemsUsed(cost);
  }

  @Override
  public int getItemsUsed(IPartBuilderContainer inv) {
    if (material.isEmpty()) {
      return 0;
    }
    return IDisplayPartBuilderRecipe.super.getItemsUsed(inv);
  }

  @Override
  public ItemStack getResultItem(RegistryAccess access) {
    return result.get();
  }

  @Override
  public ItemStack assemble(IPartBuilderContainer inv, RegistryAccess access) {
    ItemStack result = getResultItem(access).copy();
    IMaterialValue materialRecipe = inv.getMaterial();
    if (materialRecipe != null) {
      // if no leftover, give them more parts provided we have the patterns for it
      int value = materialRecipe.getValue();
      if (!materialRecipe.hasLeftover() && value > cost) {
        result.setCount(result.getCount() * value / cost);
      }
    }
    return result;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.itemPartBuilderSerializer.get();
  }


  /* JEI */

  private List<ItemStack> materialItems;

  @Override
  public List<ItemStack> getPatternItems() {
    return Arrays.asList(patternItem.getItems());
  }

  @Override
  public List<ItemStack> getMaterialItems() {
    if (materialItems == null) {
      // if unknown, nothing to display. Used for no material input
      if (material.isUnknown()) {
        materialItems = List.of();
      } else {
        MaterialVariantId material = this.material.getVariant();
        // if we have a variant, only need to fetch the one list
        if (!material.getVariant().isEmpty()) {
          materialItems = MaterialRecipeCache.getItems(material);
        } else {
          // fetch the root and all variants
          List<ItemStack> items = new ArrayList<>(MaterialRecipeCache.getItems(material));
          for (MaterialVariantId variant : MaterialRecipeCache.getVariants(material.getId())) {
            items.addAll(MaterialRecipeCache.getItems(variant));
          }
          this.materialItems = List.copyOf(items);
        }
      }
    }
    return materialItems;
  }

  @Override
  public List<ItemStack> getResultItems() {
    return List.of(result.get());
  }
}
