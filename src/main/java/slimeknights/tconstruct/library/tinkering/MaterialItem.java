package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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

  public static IMaterial getMaterialFromStack(ItemStack stack) {
    if ((stack.getItem() instanceof IMaterialItem)) {
      return ((IMaterialItem) stack.getItem()).getMaterial(stack);
    }

    return IMaterial.UNKNOWN;
  }

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

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    if (this.isInGroup(group)) {
      if (MaterialRegistry.initialized()) {
        for (IMaterial material : MaterialRegistry.getInstance().getMaterials()) {
          items.add(this.getItemstackWithMaterial(material));
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
    return new TranslationTextComponent(materialKey).appendText(" ").appendSibling(new TranslationTextComponent(key));
  }
}
