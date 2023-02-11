package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.TinkerWorld;

@SuppressWarnings("unchecked")
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
    this.tag(TinkerTags.EntityTypes.VILLAGERS).add(EntityType.VILLAGER, EntityType.WANDERING_TRADER, EntityType.ZOMBIE_VILLAGER);
    this.tag(TinkerTags.EntityTypes.ILLAGERS).add(EntityType.EVOKER, EntityType.ILLUSIONER, EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.WITCH);
    this.tag(TinkerTags.EntityTypes.KILLAGERS).addTags(TinkerTags.EntityTypes.VILLAGERS, TinkerTags.EntityTypes.ILLAGERS).add(EntityType.IRON_GOLEM, EntityType.RAVAGER);

    this.tag(TinkerTags.EntityTypes.SMALL_ARMOR).addTag(TinkerTags.EntityTypes.SLIMES);
    this.tag(TinkerTags.EntityTypes.REFLECTING_BLACKLIST);
    this.tag(TinkerTags.EntityTypes.REFLECTING_PRESERVE_OWNER).add(EntityType.FISHING_BOBBER);
  }

  @Override
  public String getName() {
    return "Tinkers Construct Entity Type TinkerTags";
  }

}
