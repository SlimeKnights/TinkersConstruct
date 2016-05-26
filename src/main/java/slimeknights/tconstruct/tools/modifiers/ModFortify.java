package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerAPIException;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.tools.TinkerTools;

public class ModFortify extends ToolModifier {

  public final Material material;

  public ModFortify(Material material) {
    super("fortify" + material.getIdentifier(), material.materialTextColor);

    if(!material.hasStats(HeadMaterialStats.TYPE)) {
      throw new TinkerAPIException(String.format("Trying to add a fortify-modifier for a material without tool stats: %s", material.getIdentifier()));
    }

    this.material = material;
    addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this), ModifierAspect.harvestOnly);

    ItemStack kit = TinkerTools.sharpeningKit.getItemstackWithMaterial(material);
    ItemStack flint = new ItemStack(Items.FLINT) ;
    addRecipeMatch(new RecipeMatch.ItemCombination(1, kit, flint));
  }

  @Override
  public String getLocalizedName() {
    return Util.translate(LOC_Name, "fortify") + " (" + material.getLocalizedName() + ")";
  }

  @Override
  public String getLocalizedDesc() {
    return Util.translateFormatted(String.format(LOC_Desc, "fortify"), material.getLocalizedName());
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    NBTTagCompound tag = TagUtil.getToolTag(rootCompound);
    HeadMaterialStats stats = material.getStats(HeadMaterialStats.TYPE);
    tag.setInteger(Tags.HARVESTLEVEL, stats.harvestLevel);

    // Remove other fortify modifiers, only the last one applies
    NBTTagList tagList = TagUtil.getModifiersTagList(rootCompound);
    for(int i = 0; i < tagList.tagCount(); i++) {
      NBTTagCompound mod = tagList.getCompoundTagAt(i);
      ModifierNBT data = ModifierNBT.readTag(mod);

      // only up to ourselves
      if(data.identifier.equals(this.identifier)) {
        break;
      }

      // remove other fortify occurences
      if(data.identifier.startsWith("fortify")) {
        tagList.removeTag(i);
        i--; // adjust counter
      }
    }

    TagUtil.setModifiersTagList(rootCompound, tagList);
  }

  @Override
  public boolean hasTexturePerMaterial() {
    return true;
  }
}
