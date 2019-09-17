package slimeknights.tconstruct.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Items;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Tags;
import slimeknights.tconstruct.library.TinkerPulseIds;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class TConstructItemTagsProvider extends ItemTagsProvider {

  private Set<ResourceLocation> filter = null;

  public TConstructItemTagsProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  @Override
  public void registerTags() {
    super.registerTags();

    this.filter = this.tagToBuilder.keySet().stream().map(Tag::getId).collect(Collectors.toSet());

    getBuilder(net.minecraftforge.common.Tags.Items.DYES_BLACK).add(Items.BLACK_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_BLUE).add(Items.BLUE_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_BROWN).add(Items.BROWN_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_CYAN).add(Items.CYAN_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_GRAY).add(Items.GRAY_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_GREEN).add(Items.GREEN_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_LIGHT_BLUE).add(Items.LIGHT_BLUE_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_LIGHT_GRAY).add(Items.LIGHT_GRAY_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_LIME).add(Items.LIME_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_MAGENTA).add(Items.MAGENTA_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_ORANGE).add(Items.ORANGE_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_PINK).add(Items.PINK_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_PURPLE).add(Items.PURPLE_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_RED).add(Items.RED_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_WHITE).add(Items.WHITE_DYE);
    getBuilder(net.minecraftforge.common.Tags.Items.DYES_YELLOW).add(Items.YELLOW_DYE);

    this.copy(Tags.Blocks.SLIMY_LOGS, Tags.Items.SLIMY_LOGS);

    if (TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_WORLD_PULSE_ID)) {
      this.copy(Tags.Blocks.SLIMY_LEAVES, Tags.Items.SLIMY_LEAVES);
    }
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
