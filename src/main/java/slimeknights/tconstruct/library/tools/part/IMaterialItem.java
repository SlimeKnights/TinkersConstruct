package slimeknights.tconstruct.library.tools.part;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

/**
 * Items implementing this interface contain a material
 */
public interface IMaterialItem extends ItemLike {
  /** Tag used in NBT for the material ID */
  String MATERIAL_TAG = "Material";

  /**
   * Returns the material ID of the part this itemstack holds.
   *
   * @return Material ID or {@link IMaterial#UNKNOWN_ID} if invalid
   */
  MaterialVariantId getMaterial(ItemStack stack);

  /** Returns the item with the given material, bypassing material validation */
  default ItemStack withMaterialForDisplay(MaterialVariantId materialId) {
    ItemStack stack = new ItemStack(this);
    // FIXME: it is odd that we assume the NBT format in this method but not in getMaterial, should be consistent in the implementation location
    stack.getOrCreateTag().putString(MATERIAL_TAG, materialId.toString());
    return stack;
  }

  /** Returns the item with the given material, validating it */
  default ItemStack withMaterial(MaterialVariantId material) {
    if (canUseMaterial(material.getId())) {
      return withMaterialForDisplay(material);
    }
    return new ItemStack(this);
  }

  /**
   * Returns true if the material can be used for this toolpart
   */
  default boolean canUseMaterial(MaterialId mat) {
    return true;
  }

  /** Returns true if the material can be used for this toolpart, simply an alias for {@link #canUseMaterial(MaterialId)} */
  default boolean canUseMaterial(IMaterial mat) {
    return canUseMaterial(mat.getIdentifier());
  }

  /**
   * Gets the material from a given item stack
   * @param stack  Item stack containing a material item
   * @return  Material, or unknown if none
   */
  static MaterialVariantId getMaterialFromStack(ItemStack stack) {
    if ((stack.getItem() instanceof IMaterialItem)) {
      return ((IMaterialItem) stack.getItem()).getMaterial(stack);
    }
    return IMaterial.UNKNOWN_ID;
  }

  /**
   * Gets the given item stack with this material applied
   * @param stack     Stack instance
   * @param material  Material
   * @return  Stack with material, or original stack if not a material item
   */
  static ItemStack withMaterial(ItemStack stack, MaterialVariantId material) {
    Item item = stack.getItem();
    if (item instanceof IMaterialItem) {
      ItemStack output = ((IMaterialItem) item).withMaterial(material);
      if (stack.hasTag()) {
        assert stack.getTag() != null;
        assert output.getTag() != null;
        output.getTag().merge(stack.getTag());
      }
      return output;
    }
    return stack;
  }
}
