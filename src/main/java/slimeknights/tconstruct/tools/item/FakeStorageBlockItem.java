package slimeknights.tconstruct.tools.item;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.part.IRepairKitItem;
import slimeknights.tconstruct.library.tools.part.block.MaterialBlockItem;

import java.util.function.Predicate;

/** Simple implementation of a storage block for use with materials that lack blocks. */
public class FakeStorageBlockItem extends MaterialBlockItem implements IRepairKitItem {
  /** Cache of whether tag is present for each ingot */
  private final Object2BooleanArrayMap<MaterialId> missingItemCache = new Object2BooleanArrayMap<>();
  /** Getter to resolve the tag */
  private final Predicate<MaterialId> missingItemGetter = material -> FakeIngotItem.hasItem(material, getRepairAmount());

  private final int repairAmount;
  private final TagKey<IMaterial> validMaterials;
  public FakeStorageBlockItem(Block block, Properties properties, int repairAmount, TagKey<IMaterial> validMaterials) {
    super(block, properties);
    this.repairAmount = repairAmount;
    this.validMaterials = validMaterials;
    RecipeCacheInvalidator.addReloadListener(client -> missingItemCache.clear());
  }

  @Override
  public float getRepairAmount() {
    return repairAmount;
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
