package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.EntityTypeTagsProvider;
import net.minecraft.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.TinkerWorld;

public class EntityTypeTagProvider extends EntityTypeTagsProvider {

  public EntityTypeTagProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
    super(generatorIn, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  public void registerTags() {
    this.getOrCreateBuilder(TinkerTags.EntityTypes.BOUNCY).add(TinkerWorld.skySlimeEntity.get());
    this.getOrCreateBuilder(TinkerTags.EntityTypes.SLIMES)
        .add(EntityType.SLIME, TinkerWorld.earthSlimeEntity.get(), TinkerWorld.skySlimeEntity.get(), TinkerWorld.enderSlimeEntity.get());
    this.getOrCreateBuilder(TinkerTags.EntityTypes.BACON_PRODUCER).add(EntityType.PIG, EntityType.PIGLIN, EntityType.HOGLIN);

    this.getOrCreateBuilder(TinkerTags.EntityTypes.MELTING_SHOW).add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER, EntityType.PLAYER);
    this.getOrCreateBuilder(TinkerTags.EntityTypes.MELTING_HIDE).add(EntityType.GIANT);
    this.getOrCreateBuilder(TinkerTags.EntityTypes.PIGGYBACKPACK_BLACKLIST);

    this.getOrCreateBuilder(TinkerTags.EntityTypes.CREEPERS).add(EntityType.CREEPER);
    this.getOrCreateBuilder(TinkerTags.EntityTypes.RARE_MOBS).add(EntityType.WITHER_SKELETON, EntityType.ENDER_DRAGON, EntityType.ELDER_GUARDIAN, EntityType.EVOKER);
  }

  @Override
  public String getName() {
    return "Tinkers Construct Entity Type TinkerTags";
  }

}
