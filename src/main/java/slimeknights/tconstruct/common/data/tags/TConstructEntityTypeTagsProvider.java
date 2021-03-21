package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.EntityTypeTagsProvider;
import net.minecraft.entity.EntityType;
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
    this.getOrCreateBuilder(TinkerTags.EntityTypes.SLIMES).add(TinkerWorld.skySlimeEntity.get());
    this.getOrCreateBuilder(TinkerTags.EntityTypes.MELTING_SHOW).add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER, EntityType.PLAYER);
    this.getOrCreateBuilder(TinkerTags.EntityTypes.MELTING_HIDE).add(EntityType.GIANT);
  }

  @Override
  public String getName() {
    return "Tinkers Construct Entity Type TinkerTags";
  }

}
