package slimeknights.tconstruct.tools.ranged.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;

public class BoltCore extends ToolPart {

  public static final String TAG_Material2 = Tags.PART_MATERIAL + "2";

  public BoltCore(int cost) {
    super(cost);
  }

  @Override
  public ItemStack getItemstackWithMaterial(Material material) {
    return combineMaterials(super.getItemstackWithMaterial(TinkerMaterials.wood), material);
  }

  public static Material getHeadMaterial(ItemStack stack) {
    NBTTagCompound tag = TagUtil.getTagSafe(stack);

    return TinkerRegistry.getMaterial(tag.getString(TAG_Material2));
  }

  public static ItemStack combineMaterials(ItemStack core, Material cover) {
    ItemStack combined = core.copy();
    NBTTagCompound tag = TagUtil.getTagSafe(core);
    tag.setString(TAG_Material2, cover.identifier);
    combined.setTagCompound(tag);

    return combined;
  }

  public ItemStack buildInternalItemstackForCrafting(Material material) {
    return super.getItemstackWithMaterial(material);
  }
}
