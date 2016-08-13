package slimeknights.tconstruct.library.tools;

import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.utils.Tags;

public class ProjectileLauncherNBT extends ToolNBT {

  public float drawSpeed;
  public float range;

  public ProjectileLauncherNBT() {
  }

  public ProjectileLauncherNBT(NBTTagCompound tag) {
    super(tag);

    this.range = 10f;
    this.drawSpeed = 4f;
  }

  public ProjectileLauncherNBT limb(BowMaterialStats... bowlimbs) {
    speed = 0;
    range = 0;

    for(BowMaterialStats limb : bowlimbs) {
      if(limb != null) {
        speed += limb.drawspeed;
        range += limb.range;
      }
    }

    speed /= (float) bowlimbs.length;
    range /= (float) bowlimbs.length;

    speed = Math.max(0.001f, speed);
    range = Math.min(0.001f, range);

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
  }

  @Override
  public void write(NBTTagCompound tag) {
    super.write(tag);
    tag.setFloat(Tags.DRAWSPEED, drawSpeed);
    tag.setFloat(Tags.RANGE, range);
  }
}
