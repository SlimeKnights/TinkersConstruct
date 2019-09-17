package slimeknights.tconstruct.common.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.world.TinkerWorld;

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

  @Override
  public void registerTags() {
    super.registerTags();

    this.filter = this.tagToBuilder.keySet().stream().map(Tag::getId).collect(Collectors.toSet());

    this.getBuilder(SLIMY_LOGS).add(TinkerCommons.congealed_green_slime, TinkerCommons.congealed_blue_slime, TinkerCommons.congealed_purple_slime, TinkerCommons.congealed_blood_slime, TinkerCommons.congealed_magma_slime);

    if (TConstruct.pulseManager.isPulseLoaded(TinkerPulseIds.TINKER_WORLD_PULSE_ID)) {
      this.getBuilder(SLIMY_LEAVES).add(TinkerWorld.blue_slime_leaves, TinkerWorld.purple_slime_leaves, TinkerWorld.orange_slime_leaves);
    }
  }

  @Override
  protected Path makePath(ResourceLocation id) {
    return this.filter != null && this.filter.contains(id) ? null : super.makePath(id); //We don't want to save vanilla tags.
  }

  @Override
  public String getName() {
    return "Tconstruct Block Tags";
  }

}
