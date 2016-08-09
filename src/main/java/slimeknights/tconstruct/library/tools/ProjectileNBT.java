package slimeknights.tconstruct.library.tools;

import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.utils.Tags;

public class ProjectileNBT extends ToolNBT {

  public float accuracy;

  public ProjectileNBT() {
  }

  public ProjectileNBT(NBTTagCompound tag) {
    super(tag);
    this.accuracy = 1f;
  }

  @Override
  public void read(NBTTagCompound tag) {
    super.read(tag);
    this.accuracy = tag.getFloat(Tags.ACCURACY);
  }

  @Override
  public void write(NBTTagCompound tag) {
    super.write(tag);
    tag.setFloat(Tags.ACCURACY, accuracy);
  }
}
