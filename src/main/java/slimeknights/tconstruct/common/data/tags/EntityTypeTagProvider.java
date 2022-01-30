package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.TinkerWorld;

public class EntityTypeTagProvider extends EntityTypeTagsProvider {

  public EntityTypeTagProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
    super(generatorIn, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  public void addTags() {
    this.tag(TinkerTags.EntityTypes.SLIMES)
        .add(EntityType.SLIME, TinkerWorld.earthSlimeEntity.get(), TinkerWorld.skySlimeEntity.get(), TinkerWorld.enderSlimeEntity.get(), TinkerWorld.terracubeEntity.get());
    this.tag(TinkerTags.EntityTypes.BACON_PRODUCER).add(EntityType.PIG, EntityType.PIGLIN, EntityType.HOGLIN);

    this.tag(TinkerTags.EntityTypes.MELTING_SHOW).add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER, EntityType.PLAYER);
    this.tag(TinkerTags.EntityTypes.MELTING_HIDE).add(EntityType.GIANT);
    this.tag(TinkerTags.EntityTypes.PIGGYBACKPACK_BLACKLIST);

    this.tag(TinkerTags.EntityTypes.CREEPERS).add(EntityType.CREEPER);
    this.tag(TinkerTags.EntityTypes.RARE_MOBS).add(EntityType.WITHER, EntityType.ENDER_DRAGON, EntityType.ELDER_GUARDIAN, EntityType.EVOKER, EntityType.PLAYER);
  }

  @Override
  public String getName() {
    return "Tinkers Construct Entity Type TinkerTags";
  }

}
