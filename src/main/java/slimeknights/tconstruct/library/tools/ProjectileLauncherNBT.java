package slimeknights.tconstruct.library.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

public class ProjectileLauncherNBT extends ToolNBT {

  public float drawSpeed;
  public float range;
  public float bonusDamage;

  public ProjectileLauncherNBT() {
    drawSpeed = 1f;
    range = 1f;
    bonusDamage = 0f;
  }

  public ProjectileLauncherNBT(NBTTagCompound tag) {
    super(tag);
  }

  public ProjectileLauncherNBT limb(BowMaterialStats... bowlimbs) {
    drawSpeed = 0;
    range = 0;
    bonusDamage = 0;

    for(BowMaterialStats limb : bowlimbs) {
      if(limb != null) {
        drawSpeed += limb.drawspeed;
        range += limb.range;
        bonusDamage += limb.bonusDamage;
      }
    }

    drawSpeed /= (float) bowlimbs.length;
    range /= (float) bowlimbs.length;
    bonusDamage /= (float) bowlimbs.length;

    drawSpeed = Math.max(0.001f, drawSpeed);
    range = Math.max(0.001f, range);

    return this;
  }

  public ProjectileLauncherNBT bowstring(BowStringMaterialStats... bowstrings) {
    float modifier = 0f;

    for(BowStringMaterialStats bowstring : bowstrings) {
      if(bowstring != null) {
        modifier += bowstring.modifier;
      }
    }

    modifier /= (float) bowstrings.length;
    this.durability = Math.round((float) this.durability * modifier);
    this.durability = Math.max(1, this.durability);

    return this;
  }

  @Override
  public void read(NBTTagCompound tag) {
    super.read(tag);
    this.drawSpeed = tag.getFloat(Tags.DRAWSPEED);
    this.range = tag.getFloat(Tags.RANGE);
    this.bonusDamage = tag.getFloat(Tags.PROJECTILE_BONUS_DAMAGE);
  }

  @Override
  public void write(NBTTagCompound tag) {
    super.write(tag);
    tag.setFloat(Tags.DRAWSPEED, drawSpeed);
    tag.setFloat(Tags.RANGE, range);
    tag.setFloat(Tags.PROJECTILE_BONUS_DAMAGE, bonusDamage);
  }

  public static ProjectileLauncherNBT from(ItemStack itemStack) {
    return new ProjectileLauncherNBT(TagUtil.getToolTag(itemStack));
  }
}
