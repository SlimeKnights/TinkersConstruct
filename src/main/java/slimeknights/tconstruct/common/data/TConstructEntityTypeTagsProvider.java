package slimeknights.tconstruct.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.EntityTypeTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.TinkerWorld;

public class TConstructEntityTypeTagsProvider extends EntityTypeTagsProvider {

  public TConstructEntityTypeTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
    super(generatorIn, TConstruct.modID, existingFileHelper);
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
