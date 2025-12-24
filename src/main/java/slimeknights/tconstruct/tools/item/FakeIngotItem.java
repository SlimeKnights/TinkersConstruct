package slimeknights.tconstruct.tools.item;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import net.minecraft.tags.TagKey;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeCache;

import java.util.function.Predicate;

/** Simple implementation of an ingot for use with materials that lack ingots. Class could be reused for other 1 cost composite materials. */
public class FakeIngotItem extends RepairKitItem {
  /** Cache of whether tag is present for each ingot */
  private final Object2BooleanArrayMap<MaterialId> missingItemCache = new Object2BooleanArrayMap<>();
  /** Getter to resolve the tag */
  private final Predicate<MaterialId> missingItemGetter = material -> hasItem(material, getRepairAmount());

  private final TagKey<IMaterial> validMaterials;
  public FakeIngotItem(Properties properties, int value, TagKey<IMaterial> validMaterials) {
    super(properties, value);
    this.validMaterials = validMaterials;
    RecipeCacheInvalidator.addReloadListener(client -> missingItemCache.clear());
  }

  /** Checks if an item exists with the given amount */
  public static boolean hasItem(MaterialId material, float amount) {
    // if we have a match, it's not missing
    for (MaterialRecipe recipe : MaterialRecipeCache.getRecipes(material)) {
      if (recipe.getValue() / (float) recipe.getNeeded() == amount) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean canUseMaterial(MaterialId material) {
    IMaterialRegistry registry = MaterialRegistry.getInstance();
    if (registry.isInTag(material, validMaterials)) {
      return missingItemCache.computeIfAbsent(material, missingItemGetter);
    }
    return false;
  }

  @Override
  public boolean canRepairInCraftingTable() {
    return false;
  }
}
