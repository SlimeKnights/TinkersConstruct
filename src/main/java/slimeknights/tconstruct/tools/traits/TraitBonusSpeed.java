package slimeknights.tconstruct.tools.traits;

import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

/** A general trait that adds speed to tools */
public class TraitBonusSpeed extends AbstractTrait {

  protected final float speed;

  public TraitBonusSpeed(String identifier, float speed) {
    super(identifier, 0xffffff);

    this.speed = speed;
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    // apply bonus damage if it hasn't been applied yet
    if(!TinkerUtil.hasTrait(rootCompound, identifier)) {
      // +damage
      ToolNBT data = TagUtil.getToolStats(rootCompound);
      data.speed += speed;
      TagUtil.setToolTag(rootCompound, data.get());
    }
    super.applyEffect(rootCompound, modifierTag);
  }
}
