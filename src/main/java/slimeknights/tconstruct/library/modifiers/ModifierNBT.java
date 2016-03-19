package slimeknights.tconstruct.library.modifiers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;

/**
 * Represents the NBT data saved for a modifier.
 */
public class ModifierNBT {

  public String identifier;
  public int color;
  public int level;
  public String extraInfo;

  public ModifierNBT() {
    identifier = "";
    color = 0xffffff;
    level = 0;
  }

  public ModifierNBT(IModifier modifier) {
    this.identifier = modifier.getIdentifier();
    this.level = 0;
    this.color = Util.enumChatFormattingToColor(TextFormatting.GRAY);
  }

  public ModifierNBT(NBTTagCompound tag) {
    this();
    read(tag);
  }

  public static ModifierNBT readTag(NBTTagCompound tag) {
    ModifierNBT data = new ModifierNBT();
    if(tag != null) {
      data.read(tag);
    }

    return data;
  }

  public void read(NBTTagCompound tag) {
    identifier = tag.getString("identifier");
    color = tag.getInteger("color");
    level = tag.getInteger("level");
    extraInfo = tag.getString("extraInfo");
  }

  public void write(NBTTagCompound tag) {
    tag.setString("identifier", identifier);
    tag.setInteger("color", color);
    if(level > 0) {
      tag.setInteger("level", level);
    }
    else {
      tag.removeTag("level");
    }
    if(extraInfo != null && !extraInfo.isEmpty()) {
      tag.setString("extraInfo", extraInfo);
    }
  }

  public String getColorString() {
    return CustomFontColor.encodeColor(color);
  }

  public static <T extends ModifierNBT> T readTag(NBTTagCompound tag, Class<T> clazz) {
    try {
      T data = clazz.newInstance();
      data.read(tag);
      return data;
    } catch(ReflectiveOperationException e) {
      TinkerRegistry.log.error(e);
      return null;
    }
  }

  public static IntegerNBT readInteger(NBTTagCompound tag) {
    return ModifierNBT.readTag(tag, IntegerNBT.class);
  }

  public static BooleanNBT readBoolean(NBTTagCompound tag) {
    return ModifierNBT.readTag(tag, BooleanNBT.class);
  }


  /**
   * Single boolean value
   */
  public static class BooleanNBT extends ModifierNBT {

    public boolean status;

    public BooleanNBT() {
    }

    public BooleanNBT(IModifier modifier, boolean status) {
      super(modifier);
      this.status = status;
    }

    @Override
    public void write(NBTTagCompound tag) {
      super.write(tag);
      tag.setBoolean("status", status);
    }

    @Override
    public void read(NBTTagCompound tag) {
      super.read(tag);
      status = tag.getBoolean("status");
    }
  }

  /**
   * Data can be applied multiple times up to a maximum.
   */
  public static class IntegerNBT extends ModifierNBT {

    public int current;
    public int max;

    public IntegerNBT() {
    }

    public IntegerNBT(IModifier modifier, int current, int max) {
      super(modifier);
      this.current = current;
      this.max = max;

      this.extraInfo = calcInfo();
    }

    @Override
    public void write(NBTTagCompound tag) {
      calcInfo();
      super.write(tag);
      tag.setInteger("current", current);
      tag.setInteger("max", max);
    }

    @Override
    public void read(NBTTagCompound tag) {
      super.read(tag);
      current = tag.getInteger("current");
      max = tag.getInteger("max");

      extraInfo = calcInfo();
    }

    public String calcInfo() {
      if(max > 0) {
        return String.format("%d / %d", current, max);
      }

      return current > 0 ? String.valueOf(current) : "";
    }
  }
}
