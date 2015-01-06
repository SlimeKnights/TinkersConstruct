package tconstruct.library.weapons;

import net.minecraft.nbt.NBTTagCompound;

import tconstruct.library.tinkering.Material;
import tconstruct.library.tools.TinkersTool;

public class TinkerWeapon extends TinkersTool {

  @Override
  protected NBTTagCompound buildTag(Material[] materials) {
    return null;
  }

  @Override
  public String getItemType() {
    return "weapon";
  }

  @Override
  public String[] getInformation() {
    // todo
    return new String[0];
  }
}
