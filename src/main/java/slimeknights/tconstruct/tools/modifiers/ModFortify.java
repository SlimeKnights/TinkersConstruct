package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import slimeknights.mantle.util.RecipeMatch;
import slimeknights.tconstruct.library.TinkerAPIException;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolTagUtil;
import slimeknights.tconstruct.tools.TinkerTools;

public class ModFortify extends Modifier {

  protected final Material material;

  public ModFortify(Material material) {
    super("fortify" + material.getIdentifier());

    if(!material.hasStats(ToolMaterialStats.TYPE)) {
      throw new TinkerAPIException(String.format("Trying to add a fortify-modifier for a material without tool stats: %s", material.getIdentifier()));
    }

    this.material = material;
    addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this, material.textColor), ModifierAspect.harvestOnly);

    ItemStack blade = TinkerTools.swordBlade.getItemstackWithMaterial(material);
    ItemStack binding = TinkerTools.binding.getItemstackWithMaterial(material);
    addRecipeMatch(new RecipeMatch.ItemCombination(1, blade, binding));
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    // nothing needed besides aspects
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    NBTTagCompound tag = TagUtil.getToolTag(rootCompound);
    ToolMaterialStats stats = material.getStats(ToolMaterialStats.TYPE);
    ToolTagUtil.setHarvestLevel(tag, stats.harvestLevel);
  }
}
