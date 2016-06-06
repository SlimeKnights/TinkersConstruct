package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.modifiers.ModifierAspect;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

public class ModSharpness extends ToolModifier {

  private final int max;

  public ModSharpness(int max) {
    super("sharpness", 0xfff6f6);

    this.max = max;

    addAspects(new ModifierAspect.MultiAspect(this, 5, max, 1));
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    ModifierNBT.IntegerNBT data = ModifierNBT.readInteger(modifierTag);

    ToolNBT toolData = TagUtil.getOriginalToolStats(rootCompound);
    float attack = toolData.attack;
    int level = data.current / max;
    for(int count = data.current; count > 0; count--) {
      if(attack <= 10f) {
        // linear scaling from 0.05 to 0.035 per piece till 10 damage
        attack += 0.05f - 0.025f * attack / 10f;
      }
      else if(attack <= 20f) {
        // 0.035 to 0.02
        attack += 0.025f - 0.01 * attack / 20f;
      }
      else {
        // flat +0.02
        attack += 0.015;
      }
    }

    // each full level gives a flat 0.25 bonus (1/8 heart), not influenced by dimishing returns
    attack += level * 0.25f;

    // save it to the tool
    NBTTagCompound tag = TagUtil.getToolTag(rootCompound);
    attack -= toolData.attack;
    attack += tag.getFloat(Tags.ATTACK);
    tag.setFloat(Tags.ATTACK, attack);
  }

  @Override
  public String getTooltip(NBTTagCompound modifierTag, boolean detailed) {
    return getLeveledTooltip(modifierTag, detailed);
  }
}
