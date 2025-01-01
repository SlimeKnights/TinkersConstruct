package slimeknights.tconstruct.library.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.TConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

/**
 * Replaces blocks in a structure palette with another set of blocks
 */
@SuppressWarnings("deprecation")  // I wish IDEA let you declare a deprecation in a source is wrong globally
public abstract class AbstractStructureRepalleter extends GenericNBTProvider {
  private final Multimap<ResourceLocation,RepaletteTask> structures = HashMultimap.create();

  private final ExistingFileHelper existingFileHelper;
  private final String modId;
  public AbstractStructureRepalleter(PackOutput packOutput, ExistingFileHelper existingFileHelper, String modId) {
    super(packOutput, Target.DATA_PACK, "structures");
    this.existingFileHelper = existingFileHelper;
    this.modId = modId;
  }

  /** Use {@link #repalette(ResourceLocation, String, boolean, Replacement...)} to add structures to process here */
  public abstract void addStructures();

  private ListTag repaletteNBT(ListTag palette, Map<String,String> repalette) {
    // simply iterate the palette list and make adjustments
    for (int i = 0; i < palette.size(); i++) {
      CompoundTag block = palette.getCompound(i);
      String newName = repalette.get(block.getString("Name"));
      if (newName != null) {
        block.putString("Name", newName);
      }
    }
    return palette;
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    addStructures();
    List<CompletableFuture<?>> tasks = new ArrayList<>();
    for (Entry<ResourceLocation,Collection<RepaletteTask>> entry : structures.asMap().entrySet()) {
      ResourceLocation original = entry.getKey();

      try (InputStream io = existingFileHelper.getResource(original, PackType.SERVER_DATA, ".nbt", "structures").open()) {
        CompoundTag inputNBT = NbtIo.readCompressed(io);
        for (RepaletteTask task : entry.getValue()) {
          // start by fetching the palette, we assume its not randomized
          CompoundTag newStructure = inputNBT.copy();
          ListTag palette = newStructure.getList("palette", Tag.TAG_COMPOUND);

          // if we have a single palette, modify directly
          if (task.replacements.length == 1) {
            repaletteNBT(palette, task.replacements[0].build());
          } else {
            // multiple means we are building a randomized palette
            newStructure.remove("palette");
            ListTag palettes = new ListTag();
            for (Replacement replacement : task.replacements) {
              palettes.add(repaletteNBT(palette.copy(), replacement.build()));
            }
            newStructure.put("palettes", palettes);
          }
          // if requested, run it through the structure template to cleanup NBT (e.g. compact palettes)
          if (task.reprocess) {
            StructureTemplate template = new StructureTemplate();
            template.load(BuiltInRegistries.BLOCK.asLookup(), newStructure);
            newStructure = template.save(new CompoundTag());
          }
          tasks.add(saveNBT(cache, new ResourceLocation(modId, task.location), newStructure));
        }
      }
      catch (IOException e) {
        TConstruct.LOG.error("Couldn't read NBT for {}", original, e);
      }
    }
    return GenericDataProvider.allOf(tasks);
  }

  /** Starts a builder for repaletting the given structure into the given output. Note calling multple times with an output not give the same builder. */
  protected Replacement replacement() {
    return new Replacement();
  }

  /**
   * Repalattes the given structure to the given target. If multiple replacements are used, the structure will randomly choose one of them.
   * @param original       Original structure to load
   * @param target         Output name
   * @param reprocess      If true, runs the structure through {@link StructureTemplate} to cleanup NBT. Will be slower but lets you compact the palette
   * @param replacements   List of replacements to make.
   */
  protected void repalette(ResourceLocation original, String target, boolean reprocess, Replacement... replacements) {
    if (replacements.length == 0) {
      throw new IllegalArgumentException("Must have at least 1 replacement");
    }
    structures.put(original, new RepaletteTask(target, reprocess, replacements));
  }

  /** Record of a location replacement pair */
  private record RepaletteTask(String location, boolean reprocess, Replacement[] replacements) {}

  /** Builder for a palette replacement */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Replacement {
    private final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
    private Map<String,String> built;

    /** Adds a mapping replacing from with to */
    public Replacement addMapping(ResourceLocation from, ResourceLocation to) {
      built = null;
      builder.put(from.toString(), to.toString());
      return this;
    }

    /** Adds a mapping replacing from with to */
    public Replacement addMapping(Block from, Block to) {
      return addMapping(BuiltInRegistries.BLOCK.getKey(from), BuiltInRegistries.BLOCK.getKey(to));
    }

    /** Builds this replacement */
    private Map<String,String> build() {
      if (built == null) {
        built = builder.build();
      }
      return built;
    }

    /** Creates a copy of this replacement, so you can start multiple palettes from the same root */
    public Replacement copy() {
      Replacement replacement = new Replacement();
      replacement.builder.putAll(build());
      return replacement;
    }
  }
}
