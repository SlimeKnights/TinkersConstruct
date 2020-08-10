package slimeknights.tconstruct.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.EntityTypeTagsProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Set;

public class TConstructEntityTypeTagsProvider extends EntityTypeTagsProvider {

  private Set<ResourceLocation> filter = null;

  public TConstructEntityTypeTagsProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  @Override
  public void registerTags() {
    this.getOrCreateBuilder(TinkerTags.EntityTypes.SLIMES).add(TinkerWorld.blueSlimeEntity.get());
  }

  @Override
  public String getName() {
    return "Tinkers Construct Entity Type TinkerTags";
  }

}
