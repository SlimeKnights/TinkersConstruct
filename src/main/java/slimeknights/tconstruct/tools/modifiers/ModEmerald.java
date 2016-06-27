package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.library.utils.TagUtil;

public class ModEmerald extends ToolModifier {

  public ModEmerald() {
    super("emerald", 0x41f384);

    addAspects(new ModifierAspect.SingleAspect(this), new ModifierAspect.DataAspect(this), ModifierAspect.freeModifier);
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    ToolNBT data = TagUtil.getToolStats(rootCompound);
    ToolNBT base = TagUtil.getOriginalToolStats(rootCompound);

    data.durability += base.durability / 2;

    if(data.harvestLevel < HarvestLevels.DIAMOND) {
      data.harvestLevel++;
    }

    TagUtil.setToolTag(rootCompound, data.get());
  }
}
