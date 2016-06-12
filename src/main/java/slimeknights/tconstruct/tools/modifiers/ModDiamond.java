package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.library.utils.TagUtil;

public class ModDiamond extends ToolModifier {

  public ModDiamond() {
    super("diamond", 0x8cf4e2);

    addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this), ModifierAspect.freeModifier);
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    ToolNBT data = TagUtil.getToolStats(rootCompound);
    data.durability += 500;

    if(data.harvestLevel < HarvestLevels.OBSIDIAN) {
      data.harvestLevel++;
    }

    data.attack += 1f;
    data.speed += 0.5f;

    TagUtil.setToolTag(rootCompound, data.get());
  }
}
