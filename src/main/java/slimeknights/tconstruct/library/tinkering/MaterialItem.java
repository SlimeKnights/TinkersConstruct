package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.utils.Tags;

import java.util.Optional;

/**
 * Represents an item that has a Material associated with it. The metadata of an itemstack identifies which material the
 * itemstack of this item has.
 */
public class MaterialItem extends Item implements IMaterialItem {

  public MaterialItem(Properties properties) {
    super(properties);
  }

  @Override
  public IMaterial getMaterial(ItemStack stack) {
    return Optional.ofNullable(stack.getTag())
      .map(compoundNBT -> compoundNBT.getString(Tags.PART_MATERIAL))
      .map(MaterialId::new)
      .map(MaterialRegistry::getMaterial)
      .orElse(IMaterial.UNKNOWN);
  }

  @Override
  public ItemStack getItemstackWithMaterial(IMaterial material) {
    ItemStack stack = new ItemStack(this);
    CompoundNBT nbt = new CompoundNBT();
    nbt.putString(Tags.PART_MATERIAL, material.getIdentifier().toString());
    stack.setTag(nbt);

    return stack;
  }
}
