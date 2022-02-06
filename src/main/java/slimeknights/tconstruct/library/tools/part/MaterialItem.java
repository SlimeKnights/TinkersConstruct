package slimeknights.tconstruct.library.tools.part;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
  private static Optional<MaterialId> getMaterialId(@Nullable CompoundTag nbt) {
    return Optional.ofNullable(nbt)
                   .map(compoundNBT -> compoundNBT.getString(NBTTags.PART_MATERIAL))
                   .filter(string -> !string.isEmpty())
                   .map(MaterialId::tryParse);
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
  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
    if (this.allowdedIn(group) && MaterialRegistry.isFullyLoaded()) {
      // if a specific material is set in the config, try adding that
      String showOnlyId = Config.COMMON.showOnlyPartMaterial.get();
      boolean added = false;
      if (!showOnlyId.isEmpty()) {
        MaterialId materialId = MaterialId.tryParse(showOnlyId);
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
  public Component getName(ItemStack stack) {
    // if no material, return part name directly
    IMaterial material = getMaterial(stack);
    if (material == IMaterial.UNKNOWN) {
      return super.getName(stack);
    }
    String key = this.getDescriptionId(stack);
    ResourceLocation loc = material.getIdentifier();
    // if there is a specific name, use that
    String fullKey = String.format("%s.%s.%s", key, loc.getNamespace(), loc.getPath());
    if (Util.canTranslate(fullKey)) {
      return new TranslatableComponent(fullKey);
    }
    // try material name prefix next
    String materialKey = material.getTranslationKey();
    String materialPrefix = materialKey + ".format";
    if (Util.canTranslate(materialPrefix)) {
      return new TranslatableComponent(materialPrefix, new TranslatableComponent(key));
    }
    // format as "<material> <item name>"
    return new TranslatableComponent(materialKey).append(" ").append(new TranslatableComponent(key));
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
  protected static void addModTooltip(IMaterial material, List<Component> tooltip) {
    if (material != IMaterial.UNKNOWN) {
      tooltip.add(TextComponent.EMPTY);
      tooltip.add(new TranslatableComponent(ADDED_BY, DomainDisplayName.nameFor(material.getIdentifier().getNamespace())));
    }
  }

  @Override
  public void verifyTagAfterLoad(CompoundTag nbt) {
    // if the material exists and was changed, update it
    if (nbt.contains("tag", Tag.TAG_COMPOUND)) {
      CompoundTag tag = nbt.getCompound("tag");
      getMaterialId(tag).map(id -> {
        MaterialId resolved = MaterialRegistry.getInstance().resolve(id);
        return resolved == id ? null : resolved;
      }).ifPresent(id -> tag.putString(NBTTags.PART_MATERIAL, id.toString()));
    }
  }
}
