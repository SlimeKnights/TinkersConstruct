package tconstruct.library.tinkering.modifiers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

/**
 * Represents the NBT data saved for a modifier.
 */
public class ModifierNBT {

  protected final String key;
  public String identifier;
  public EnumChatFormatting color;
  public int level;

  public ModifierNBT(String key) {
    this.key = key;
  }

  public static ModifierNBT read(NBTTagCompound tag, String key) {
    ModifierNBT data = new ModifierNBT(key);

    NBTTagCompound subTag = tag.getCompoundTag(key);
    if (subTag != null) {
      data.identifier = subTag.getString("identifier");
      data.color = EnumChatFormatting.func_175744_a(subTag.getInteger("color"));
      data.level = subTag.getInteger("level");
      if (data.level == 0) {
        data.level = 1;
      }
    }

    return data;
  }

  public void write(NBTTagCompound tag) {
    NBTTagCompound subTag = tag.getCompoundTag(key);
    subTag.setString("identifier", identifier);
    subTag.setInteger("color", color.getColorIndex());
    if (level > 0) {
      subTag.setInteger("level", level);
    }
    tag.setTag(key, subTag);
  }

  public String getInfo() {
    return String.valueOf(level);
  }

  /**
   * Single boolean value
   */
  public static class Boolean extends ModifierNBT {

    public boolean status;

    public Boolean(String key) {
      super(key);
    }

    @Override
    public void write(NBTTagCompound tag) {
      tag.setBoolean(key, status);
    }

    public static Boolean read(NBTTagCompound tag, String key) {
      Boolean data = new Boolean(key);
      data.status = tag.getBoolean(key);
      return data;
    }

    public static void write(boolean value, NBTTagCompound tag, String key) {
      Boolean data = read(tag, key);
      data.status = value; // we don't actually use the old value if it exists. but meh.

      data.write(tag);
    }
  }

  /**
   * Data can be applied multiple times up to a maximum.
   */
  public static class Integer extends ModifierNBT {

    public int current;
    public int max;

    public Integer(String key) {
      super(key);
    }

    @Override
    public void write(NBTTagCompound tag) {
      NBTTagCompound subTag = tag.getCompoundTag(key);
      subTag.setInteger("current", current);
      subTag.setInteger("max", max);
      tag.setTag(key, subTag);
    }

    public static Integer read(NBTTagCompound tag, String key) {
      Integer data = new Integer(key);
      NBTTagCompound subTag = tag.getCompoundTag(key);
      if (subTag != null) {
        data.current = subTag.getInteger("current");
        data.max = subTag.getInteger("max");
      }
      return data;
    }

    @Override
    public String getInfo() {
      if (max > 0) {
        return String.format("%d / %d", current, max);
      }

      return String.valueOf(current);
    }
  }
}
