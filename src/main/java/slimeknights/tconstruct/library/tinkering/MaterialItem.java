package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.utils.Tags;

import java.util.Optional;

/**
 * Represents an item that has a Material associated with it. The metadata of an itemstack identifies which material the
 * itemstack of this item has.
 */
public class MaterialItem extends Item implements IMaterialItem {

  public MaterialItem(Settings properties) {
    super(properties);
  }

  @Override
  public Optional<MaterialId> getMaterialId(ItemStack stack) {
    return Optional.ofNullable(stack.getTag())
                   .map(compoundNBT -> compoundNBT.getString(Tags.PART_MATERIAL))
                   .map(MaterialId::new);
  }

  @Override
  public ItemStack withMaterialForDisplay(MaterialId materialId) {
    ItemStack stack = new ItemStack(this);
    stack.getOrCreateTag().putString(Tags.PART_MATERIAL, materialId.toString());
    return stack;
  }

  @Override
  public ItemStack withMaterial(IMaterial material) {
    if (canUseMaterial(material)) {
      return withMaterialForDisplay(material.getIdentifier());
    }
    return new ItemStack(this);
  }

  @Override
  public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items) {
    if (this.isIn(group)) {
      if (MaterialRegistry.initialized()) {
        for (IMaterial material : MaterialRegistry.getInstance().getMaterials()) {
          if (this.canUseMaterial(material)) {
            items.add(this.withMaterial(material));
          }
        }
      } else {
        items.add(new ItemStack(this));
      }
    }
  }

  @Override
  public Text getName(ItemStack stack) {
    // if no material, return part name directly
    IMaterial material = getMaterial(stack);
    if (material == IMaterial.UNKNOWN) {
      return super.getName(stack);
    }
    String key = this.getTranslationKey(stack);
    Identifier loc = material.getIdentifier();
    // if there is a specific name, use that
    String fullKey = String.format("%s.%s.%s", key, loc.getNamespace(), loc.getPath());
    if (Util.canTranslate(fullKey)) {
      return new TranslatableText(fullKey);
    }
    // try material name prefix next
    String materialKey = material.getTranslationKey();
    String materialPrefix = materialKey + ".format";
    if (Util.canTranslate(materialPrefix)) {
      return new TranslatableText(materialPrefix, new TranslatableText(key));
    }
    // format as "<material> <item name>"
    return new TranslatableText(materialKey).append(" ").append(new TranslatableText(key));
  }
}
