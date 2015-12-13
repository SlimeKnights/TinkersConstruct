package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

public class ModHaste extends Modifier {

  private final int max;

  public ModHaste(int max) {
    super("haste");

    this.max = max;

    addAspects(new ModifierAspect.MultiAspect(this, EnumChatFormatting.DARK_RED, 5, max, 1), ModifierAspect.harvestOnly);
  }

  @Override
  public void updateNBT(NBTTagCompound modifierTag) {
    // taken care of by the aspect
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);

    ToolNBT toolData = TagUtil.getOriginalToolStats(rootCompound);
    float speed = toolData.speed;
    int level = data.current / max;
    for(int count = data.current; count > 0; count--) {
      if(speed <= 10f) {
        // linear scaling from 0.08 to 0.06 per piece till 10 miningspeed
        speed += 0.08f - 0.02f * speed / 10f;
      }
      else if(speed <= 20f) {
        speed += 0.06f - 0.04 * speed / 20f;
      }
      else {
        speed += 0.01;
      }
    }

    // each full level gives a flat 0.1 bonus, not influenced by dimishing returns
    speed += level * 0.1f;

    // save it to the tool
    NBTTagCompound tag = TagUtil.getToolTag(rootCompound);
    speed -= toolData.speed;
    speed += tag.getFloat(Tags.MININGSPEED);
    tag.setFloat(Tags.MININGSPEED, speed);
  }

  @Override
  public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
    // the most important function in the whole file!
    ModifierNBT data = ModifierNBT.readInteger(modifierTag);

    String basic = super.getTooltip(modifierTag, false);

    for(int i = data.level; i > 1; i--) {
      if(StatCollector.canTranslate(String.format(LOC_Name + i, getIdentifier()))) {
        basic = StatCollector.translateToLocal(String.format(LOC_Name + i, getIdentifier()));
        break;
      }
    }

    if(detailed) {
      return basic + " " + data.extraInfo;
    }

    return basic;
  }
}
