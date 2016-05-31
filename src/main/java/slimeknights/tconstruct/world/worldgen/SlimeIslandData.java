package slimeknights.tconstruct.world.worldgen;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import java.util.List;

import javax.annotation.Nonnull;

public class SlimeIslandData extends WorldSavedData {

  public final List<StructureBoundingBox> islands = Lists.newArrayList();

  public SlimeIslandData(String name) {
    super(name);
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbt) {
    islands.clear();

    NBTTagList tagList = nbt.getTagList("slimeislands", 11);
    for(int i = 0; i < tagList.tagCount(); i++) {
      islands.add(new StructureBoundingBox(tagList.getIntArrayAt(i)));
    }
  }

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
    NBTTagList tagList = new NBTTagList();
    for(StructureBoundingBox sbb : islands) {
      tagList.appendTag(sbb.toNBTTagIntArray());
    }

    nbt.setTag("slimeislands", tagList);

    return nbt;
  }
}
