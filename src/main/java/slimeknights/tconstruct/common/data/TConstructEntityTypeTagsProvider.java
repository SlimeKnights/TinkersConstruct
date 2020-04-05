package slimeknights.tconstruct.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.EntityTypeTagsProvider;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.common.Tags;
import slimeknights.tconstruct.entity.WorldEntities;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class TConstructEntityTypeTagsProvider extends EntityTypeTagsProvider {

  private Set<ResourceLocation> filter = null;

  public TConstructEntityTypeTagsProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  @Override
  public void registerTags() {
    super.registerTags();

    this.filter = this.tagToBuilder.keySet().stream().map(Tag::getId).collect(Collectors.toSet());

    this.getBuilder(Tags.EntityTypes.SLIMES).add(WorldEntities.blue_slime_entity);
  }

  @Override
  protected Path makePath(ResourceLocation id) {
    return this.filter != null && this.filter.contains(id) ? null : super.makePath(id); //We don't want to save vanilla tags.
  }

  @Override
  public String getName() {
    return "Tinkers Construct Entity Type Tags";
  }

}
