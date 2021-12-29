package slimeknights.tconstruct.library.tools.part;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;

import java.util.Optional;

/**
 * Items implementing this interface contain a material
 */
public interface IMaterialItem extends ItemLike {
  /**
   * Returns the material ID of the part this itemstack holds.
   *
   * @return Material or Material.UNKNOWN if invalid
   */
  Optional<MaterialId> getMaterialId(ItemStack stack);

  /**
   * Returns the material of the part this itemstack holds.
   *
   * @return Material or Material.UNKNOWN if invalid
   */
  default IMaterial getMaterial(ItemStack stack) {
    return getMaterialId(stack)
      .map(MaterialRegistry::getMaterial)
      .filter(this::canUseMaterial)
      .orElse(IMaterial.UNKNOWN);
  }

  /**
   * Returns the item with the given material
   */
  ItemStack withMaterialForDisplay(MaterialId material);

  /**
   * Returns the item with the given material
   */
  ItemStack withMaterial(IMaterial material);

  /**
   * Returns true if the material can be used for this toolpart
   */
  default boolean canUseMaterial(IMaterial mat) {
    return true;
  }

  /**
   * Gets the material from a given item stack
   * @param stack  Item stack containing a material item
   * @return  Material, or unknown if none
   */
  static MaterialId getMaterialIdFromStack(ItemStack stack) {
    if ((stack.getItem() instanceof IMaterialItem)) {
      return ((IMaterialItem) stack.getItem()).getMaterialId(stack)
                                              .orElse(IMaterial.UNKNOWN_ID);
    }
    return IMaterial.UNKNOWN_ID;
  }

  /**
   * Gets the material from a given item stack
   * @param stack  Item stack containing a material item
   * @return  Material, or unknown if none
   */
  static IMaterial getMaterialFromStack(ItemStack stack) {
    if ((stack.getItem() instanceof IMaterialItem)) {
      return ((IMaterialItem) stack.getItem()).getMaterial(stack);
    }
    return IMaterial.UNKNOWN;
  }

  /**
   * Gets the given item stack with this material applied
   * @param stack     Stack instance
   * @param material  Material
   * @return  Stack with material, or original stack if not a material item
   */
  static ItemStack withMaterial(ItemStack stack, IMaterial material) {
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
