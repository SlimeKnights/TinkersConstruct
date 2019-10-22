package slimeknights.tconstruct.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Items;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.common.Tags;
import slimeknights.tconstruct.items.CommonItems;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class TConstructItemTagsProvider extends ItemTagsProvider {

  private Set<ResourceLocation> filter = null;

  public TConstructItemTagsProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  private void addCommon() {
    this.getBuilder(net.minecraftforge.common.Tags.Items.SLIMEBALLS).add(Tags.Items.BLUE_SLIMEBALL, Tags.Items.PURPLE_SLIMEBALL, Tags.Items.BLOOD_SLIMEBALL, Tags.Items.MAGMA_SLIMEBALL).add(CommonItems.pink_slime_ball);
    this.getBuilder(Tags.Items.GREEN_SLIMEBALL).add(Items.SLIME_BALL);
    this.getBuilder(Tags.Items.BLUE_SLIMEBALL).add(CommonItems.blue_slime_ball);
    this.getBuilder(Tags.Items.PURPLE_SLIMEBALL).add(CommonItems.purple_slime_ball);
    this.getBuilder(Tags.Items.BLOOD_SLIMEBALL).add(CommonItems.blood_slime_ball);
    this.getBuilder(Tags.Items.MAGMA_SLIMEBALL).add(CommonItems.magma_slime_ball);

    this.getBuilder(net.minecraftforge.common.Tags.Items.INGOTS).add(Tags.Items.INGOTS_COBALT, Tags.Items.INGOTS_ARDITE, Tags.Items.INGOTS_MANYULLYN, Tags.Items.INGOTS_KNIGHTSLIME, Tags.Items.INGOTS_PIGIRON, Tags.Items.INGOTS_ALUBRASS);
    this.getBuilder(Tags.Items.INGOTS_COBALT).add(CommonItems.cobalt_ingot);
    this.getBuilder(Tags.Items.INGOTS_ARDITE).add(CommonItems.ardite_ingot);
    this.getBuilder(Tags.Items.INGOTS_MANYULLYN).add(CommonItems.manyullyn_ingot);
    this.getBuilder(Tags.Items.INGOTS_KNIGHTSLIME).add(CommonItems.knightslime_ingot);
    this.getBuilder(Tags.Items.INGOTS_PIGIRON).add(CommonItems.pigiron_ingot);
    this.getBuilder(Tags.Items.INGOTS_ALUBRASS).add(CommonItems.alubrass_ingot);

    this.getBuilder(net.minecraftforge.common.Tags.Items.NUGGETS).add(Tags.Items.NUGGETS_COBALT, Tags.Items.NUGGETS_ARDITE, Tags.Items.NUGGETS_MANYULLYN, Tags.Items.NUGGETS_KNIGHTSLIME, Tags.Items.NUGGETS_PIGIRON, Tags.Items.NUGGETS_ALUBRASS);
    this.getBuilder(Tags.Items.NUGGETS_COBALT).add(CommonItems.cobalt_nugget);
    this.getBuilder(Tags.Items.NUGGETS_ARDITE).add(CommonItems.ardite_nugget);
    this.getBuilder(Tags.Items.NUGGETS_MANYULLYN).add(CommonItems.manyullyn_nugget);
    this.getBuilder(Tags.Items.NUGGETS_KNIGHTSLIME).add(CommonItems.knightslime_nugget);
    this.getBuilder(Tags.Items.NUGGETS_PIGIRON).add(CommonItems.pigiron_nugget);
    this.getBuilder(Tags.Items.NUGGETS_ALUBRASS).add(CommonItems.alubrass_nugget);

    this.copy(net.minecraftforge.common.Tags.Blocks.STORAGE_BLOCKS, net.minecraftforge.common.Tags.Items.STORAGE_BLOCKS);
    this.copy(Tags.Blocks.STORAGE_BLOCKS_COBALT, Tags.Items.STORAGE_BLOCKS_COBALT);
    this.copy(Tags.Blocks.STORAGE_BLOCKS_ARDITE, Tags.Items.STORAGE_BLOCKS_ARDITE);
    this.copy(Tags.Blocks.STORAGE_BLOCKS_MANYULLYN, Tags.Items.STORAGE_BLOCKS_MANYULLYN);
    this.copy(Tags.Blocks.STORAGE_BLOCKS_KNIGHTSLIME, Tags.Items.STORAGE_BLOCKS_KNIGHTSLIME);
    this.copy(Tags.Blocks.STORAGE_BLOCKS_PIGIRON, Tags.Items.STORAGE_BLOCKS_PIGIRON);
    this.copy(Tags.Blocks.STORAGE_BLOCKS_ALUBRASS, Tags.Items.STORAGE_BLOCKS_ALUBRASS);
  }

  private void addWorld() {
    this.copy(Tags.Blocks.SLIMY_LOGS, Tags.Items.SLIMY_LOGS);
    this.copy(Tags.Blocks.SLIMY_LEAVES, Tags.Items.SLIMY_LEAVES);
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
    return "Tconstruct Item Tags";
  }

}
