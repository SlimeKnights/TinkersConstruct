package slimeknights.tconstruct.world.worldgen;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.WorldSavedData;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

public class SlimeIslandData extends WorldSavedData {

  private final List<StructureBoundingBox> islands = Lists.newArrayList();
  // I honestly don't know if we need a concurrent hashset, but can't be too sure for compatibility
  private final Map<ChunkPos, Long> chunksToGenerate = new ConcurrentHashMap<>();

  public SlimeIslandData(String name) {
    super(name);
  }

  public void markChunkForGeneration(int chunkX, int chunkZ, long seed) {
    chunksToGenerate.put(new ChunkPos(chunkX, chunkZ), seed);
  }

  public Optional<Long> getSeedForChunkToGenerate(int chunkX, int chunkZ) {
    return Optional.ofNullable(chunksToGenerate.get(new ChunkPos(chunkX, chunkZ)));
  }

  public boolean markChunkAsGenerated(int chunkX, int chunkZ) {
    return chunksToGenerate.remove(new ChunkPos(chunkX, chunkZ)) != null;
  }

  public List<StructureBoundingBox> getIslands() {
    return islands;
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
