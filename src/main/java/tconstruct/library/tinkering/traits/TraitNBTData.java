package tconstruct.library.tinkering.traits;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import tconstruct.library.tinkering.modifiers.ModifierNBT;

public class TraitNBTData extends ModifierNBT {
  public String identifier;
  public EnumChatFormatting color;
  public int level;

  public TraitNBTData(String key) {
    super(key);
  }

  @Override
  public void write(NBTTagCompound tag) {
    NBTTagCompound subTag = tag.getCompoundTag(key);
    subTag.setString("trait", identifier);
    subTag.setInteger("color", color.getColorIndex());
    subTag.setInteger("level", level);
    tag.setTag(key, subTag);
  }

  public static TraitNBTData read(NBTTagCompound tag, String key) {
    TraitNBTData data = new TraitNBTData(key);

    NBTTagCompound subTag = tag.getCompoundTag(key);
    if (subTag != null) {
      data.identifier = subTag.getString("trait");
      data.color = EnumChatFormatting.func_175744_a(subTag.getInteger("color"));
      data.level = subTag.getInteger("level");
    }

    return data;
  }
}
