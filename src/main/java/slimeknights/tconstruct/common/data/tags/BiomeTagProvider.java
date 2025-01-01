package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.BiomeTags.IS_BADLANDS;
import static net.minecraft.tags.BiomeTags.IS_BEACH;
import static net.minecraft.tags.BiomeTags.IS_DEEP_OCEAN;
import static net.minecraft.tags.BiomeTags.IS_FOREST;
import static net.minecraft.tags.BiomeTags.IS_HILL;
import static net.minecraft.tags.BiomeTags.IS_MOUNTAIN;
import static net.minecraft.tags.BiomeTags.IS_NETHER;
import static net.minecraft.tags.BiomeTags.IS_OCEAN;
import static net.minecraft.tags.BiomeTags.IS_RIVER;
import static net.minecraft.tags.BiomeTags.IS_TAIGA;
import static net.minecraft.world.level.biome.Biomes.END_BARRENS;
import static net.minecraft.world.level.biome.Biomes.END_HIGHLANDS;
import static net.minecraft.world.level.biome.Biomes.END_MIDLANDS;
import static net.minecraft.world.level.biome.Biomes.SMALL_END_ISLANDS;

@SuppressWarnings("unchecked")
public class BiomeTagProvider extends BiomeTagsProvider {
  public BiomeTagProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
    super(packOutput, lookupProvider, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags(Provider provider) {
    this.tag(TinkerTags.Biomes.CLAY_ISLANDS).addTags(IS_DEEP_OCEAN, IS_OCEAN, IS_BEACH, IS_RIVER, IS_MOUNTAIN, IS_BADLANDS, IS_HILL);
    this.tag(TinkerTags.Biomes.EARTHSLIME_ISLANDS).addTags(IS_DEEP_OCEAN, IS_OCEAN);
    this.tag(TinkerTags.Biomes.SKYSLIME_ISLANDS).addTags(IS_DEEP_OCEAN, IS_OCEAN, IS_BEACH, IS_RIVER, IS_MOUNTAIN, IS_BADLANDS, IS_HILL, IS_TAIGA, IS_FOREST);
    this.tag(TinkerTags.Biomes.BLOOD_ISLANDS).addTags(IS_NETHER);
    this.tag(TinkerTags.Biomes.ENDERSLIME_ISLANDS).add(END_HIGHLANDS, END_MIDLANDS, SMALL_END_ISLANDS, END_BARRENS);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Biome Tags";
  }
}
