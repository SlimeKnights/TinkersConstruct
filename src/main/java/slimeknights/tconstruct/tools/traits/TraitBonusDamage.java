package slimeknights.tconstruct.tools.traits;

import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

/** A general trait that adds damage to tools */
public class TraitBonusDamage extends AbstractTrait {

  protected final float damage;

  public TraitBonusDamage(String identifier, float damage) {
    super(identifier, 0xffffff);

    this.damage = damage;
  }

  @Override
  public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
    // apply bonus damage if it hasn't been applied yet
    if(!TinkerUtil.hasTrait(rootCompound, identifier)) {
      // +damage
      ToolNBT data = TagUtil.getToolStats(rootCompound);
      data.attack += damage;
      TagUtil.setToolTag(rootCompound, data.get());
    }
    super.applyEffect(rootCompound, modifierTag);
  }
}
