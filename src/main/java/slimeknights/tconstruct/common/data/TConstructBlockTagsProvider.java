package slimeknights.tconstruct.common.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.blocks.WorldBlocks;
import slimeknights.tconstruct.common.Tags;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static slimeknights.tconstruct.common.Tags.Blocks.SLIMY_LEAVES;
import static slimeknights.tconstruct.common.Tags.Blocks.SLIMY_LOGS;

public class TConstructBlockTagsProvider extends BlockTagsProvider {

  private Set<ResourceLocation> filter = null;

  public TConstructBlockTagsProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  private void addCommon() {
    this.getBuilder(net.minecraftforge.common.Tags.Blocks.STORAGE_BLOCKS).add(Tags.Blocks.STORAGE_BLOCKS_COBALT, Tags.Blocks.STORAGE_BLOCKS_ARDITE, Tags.Blocks.STORAGE_BLOCKS_MANYULLYN, Tags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME, Tags.Blocks.STORAGE_BLOCKS_PIGIRON, Tags.Blocks.STORAGE_BLOCKS_ALUBRASS);
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_COBALT).add(CommonBlocks.cobalt_block);
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_ARDITE).add(CommonBlocks.ardite_block);
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_MANYULLYN).add(CommonBlocks.manyullyn_block);
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME).add(CommonBlocks.knightslime_block);
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_PIGIRON).add(CommonBlocks.pigiron_block);
    this.getBuilder(Tags.Blocks.STORAGE_BLOCKS_ALUBRASS).add(CommonBlocks.alubrass_block);
  }

  private void addWorld() {
    this.getBuilder(SLIMY_LOGS).add(WorldBlocks.congealed_green_slime, WorldBlocks.congealed_blue_slime, WorldBlocks.congealed_purple_slime, WorldBlocks.congealed_blood_slime, WorldBlocks.congealed_magma_slime);
    this.getBuilder(SLIMY_LEAVES).add(WorldBlocks.blue_slime_leaves, WorldBlocks.purple_slime_leaves, WorldBlocks.orange_slime_leaves);
  }

  @Override
  public void registerTags() {
    super.registerTags();

    this.filter = this.tagToBuilder.keySet().stream().map(Tag::getId).collect(Collectors.toSet());

    this.addCommon();
    this.addWorld();
  }

  @Override
  protected Path makePath(ResourceLocation id) {
    return this.filter != null && this.filter.contains(id) ? null : super.makePath(id); //We don't want to save vanilla tags.
  }

  @Override
  public String getName() {
    return "Tinkers Construct Block Tags";
  }

}
