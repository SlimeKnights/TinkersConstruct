package slimeknights.tconstruct.library.tinkering;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.utils.NBTTags;

import javax.annotation.Nullable;
import java.util.List;
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
                   .map(compoundNBT -> compoundNBT.getString(NBTTags.PART_MATERIAL))
                   .filter(string -> !string.isEmpty())
                   .map(MaterialId::tryCreate);
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
          if (material != IMaterial.UNKNOWN && canUseMaterial(material)) {
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
    return new TranslationTextComponent(materialKey).appendString(" ").append(new TranslationTextComponent(key));
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
    addModTooltip(getMaterial(stack), tooltip);
  }

    /**
     * Adds the mod that added the material to the tooltip
     * @param tooltip   Tooltip list
     * @param material  Material to add
     */
  protected static void addModTooltip(IMaterial material, List<ITextComponent> tooltip) {
    if (MaterialRegistry.getInstance().getMaterial(material.getIdentifier()) != IMaterial.UNKNOWN) {
      for (ModInfo modInfo : ModList.get().getMods()) {
        if (modInfo.getModId().equalsIgnoreCase(material.getIdentifier().getNamespace())) {
          tooltip.add(new StringTextComponent(""));
          tooltip.add(new TranslationTextComponent("tooltip.part.material_added_by", modInfo.getDisplayName()));
          break;
        }
      }
    }
  }
}
