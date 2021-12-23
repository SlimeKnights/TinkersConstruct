package slimeknights.tconstruct.library.tools.part;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.utils.DomainDisplayName;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Represents an item that has a Material associated with it. The NBT of the itemstack identifies which material the
 * itemstack of this item has.
 */
public class MaterialItem extends Item implements IMaterialItem {
  private static final String ADDED_BY = TConstruct.makeTranslationKey("tooltip", "part.added_by");

  public MaterialItem(Properties properties) {
    super(properties);
  }

  /** Gets the material ID for the given NBT compound */
  private static Optional<MaterialId> getMaterialId(@Nullable CompoundNBT nbt) {
    return Optional.ofNullable(nbt)
                   .map(compoundNBT -> compoundNBT.getString(NBTTags.PART_MATERIAL))
                   .filter(string -> !string.isEmpty())
                   .map(MaterialId::tryCreate);
  }

  @Override
  public Optional<MaterialId> getMaterialId(ItemStack stack) {
    return getMaterialId(stack.getTag());
  }

  @Override
  public ItemStack withMaterialForDisplay(MaterialId materialId) {
    ItemStack stack = new ItemStack(this);
    stack.getOrCreateTag().putString(NBTTags.PART_MATERIAL, materialId.toString());
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
    if (this.isInGroup(group) && MaterialRegistry.isFullyLoaded()) {
      // if a specific material is set in the config, try adding that
      String showOnlyId = Config.COMMON.showOnlyPartMaterial.get();
      boolean added = false;
      if (!showOnlyId.isEmpty()) {
        MaterialId materialId = MaterialId.tryCreate(showOnlyId);
        if (materialId != null) {
          IMaterial material = MaterialRegistry.getMaterial(materialId);
          if (material != IMaterial.UNKNOWN && !material.isHidden() && canUseMaterial(material)) {
            items.add(this.withMaterial(MaterialRegistry.getMaterial(materialId)));
            added = true;
          }
        }
      }
      // if no material is set or we failed to find it, iterate all materials
      if (!added) {
        for (IMaterial material : MaterialRegistry.getInstance().getVisibleMaterials()) {
          if (this.canUseMaterial(material)) {
            items.add(this.withMaterial(material));
            // if a specific material was requested and not found, stop after first
            if (!showOnlyId.isEmpty()) {
              break;
            }
          }
        }
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
    return new TranslationTextComponent(materialKey).appendString(" ").appendSibling(new TranslationTextComponent(key));
  }

  @Nullable
  @Override
  public String getCreatorModId(ItemStack stack) {
    ResourceLocation id = getMaterialId(stack).map(loc -> (ResourceLocation)loc).orElse(getRegistryName());
    return id == null ? null : id.getNamespace();
  }


  /**
   * Adds the mod that added the material to the tooltip
   * @param tooltip   Tooltip list
   * @param material  Material to add
   */
  protected static void addModTooltip(IMaterial material, List<ITextComponent> tooltip) {
    if (material != IMaterial.UNKNOWN) {
      tooltip.add(StringTextComponent.EMPTY);
      tooltip.add(new TranslationTextComponent(ADDED_BY, DomainDisplayName.nameFor(material.getIdentifier().getNamespace())));
    }
  }

  @Override
  public boolean updateItemStackNBT(CompoundNBT nbt) {
    // if the material exists and was changed, update it
    if (nbt.contains("tag", NBT.TAG_COMPOUND)) {
      CompoundNBT tag = nbt.getCompound("tag");
      getMaterialId(tag).map(id -> {
        MaterialId resolved = MaterialRegistry.getInstance().resolve(id);
        return resolved == id ? null : resolved;
      }).ifPresent(id -> tag.putString(NBTTags.PART_MATERIAL, id.toString()));
    }
    return true;
  }
}
