package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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

  public MaterialItem(Properties properties) {
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
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
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
  public ITextComponent getDisplayName(ItemStack stack) {
    // if no material, return part name directly
    IMaterial material = getMaterial(stack);
    if (material == IMaterial.UNKNOWN) {
      return super.getDisplayName(stack);
    }
    String key = this.getTranslationKey(stack);
    ResourceLocation loc = material.getIdentifier();
    // if there is a specific name, use that
    String fullKey = String.format("%s.%s.%s", key, loc.getNamespace(), loc.getPath());
    if (Util.canTranslate(fullKey)) {
      return new TranslationTextComponent(fullKey);
    }
    // try material name prefix next
    String materialKey = material.getTranslationKey();
    String materialPrefix = materialKey + ".format";
    if (Util.canTranslate(materialPrefix)) {
      return new TranslationTextComponent(materialPrefix, new TranslationTextComponent(key));
    }
    // format as "<material> <item name>"
    return new TranslationTextComponent(materialKey).appendString(" ").append(new TranslationTextComponent(key));
  }
}
