package slimeknights.tconstruct.library.tools;

import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.utils.Tags;

public class BowNBT extends ToolNBT {

  public float drawSpeed;
  public float range;

  public BowNBT() {
  }

  public BowNBT(NBTTagCompound tag) {
    super(tag);

    this.range = 10f;
    this.drawSpeed = 4f;
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
