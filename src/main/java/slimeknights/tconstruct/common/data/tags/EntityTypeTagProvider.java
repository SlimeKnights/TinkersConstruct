package slimeknights.tconstruct.common.data.tags;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.concurrent.CompletableFuture;

import static slimeknights.mantle.Mantle.commonResource;
import static slimeknights.tconstruct.common.TinkerTags.EntityTypes.COLLECTABLES;
import static slimeknights.tconstruct.common.TinkerTags.EntityTypes.DISCARDABLE_COLLECTABLES;
import static slimeknights.tconstruct.common.TinkerTags.EntityTypes.NECROTIC_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.EntityTypes.REFLECTING_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.EntityTypes.REFLECTING_PRESERVE_OWNER;
import static slimeknights.tconstruct.common.TinkerTags.EntityTypes.TRIDENTS;

@SuppressWarnings("unchecked")
public class EntityTypeTagProvider extends EntityTypeTagsProvider {

  public EntityTypeTagProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
    super(packOutput, lookupProvider, TConstruct.MOD_ID, existingFileHelper);
  }

  @SuppressWarnings("removal")
  @Override
  protected void addTags(Provider provider) {
    // mob classes
    this.tag(TinkerTags.EntityTypes.SLIMES).add(
      EntityType.SLIME, EntityType.MAGMA_CUBE,
      TinkerWorld.skySlimeEntity.get(), TinkerWorld.enderSlimeEntity.get(), TinkerWorld.terracubeEntity.get()
    );
    // hostile
    this.tag(TinkerTags.EntityTypes.CREEPERS).add(EntityType.CREEPER);
    this.tag(TinkerTags.EntityTypes.SPIDERS).add(EntityType.SPIDER, EntityType.CAVE_SPIDER);
    this.tag(TinkerTags.EntityTypes.GUARDIANS).add(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN);
    this.tag(TinkerTags.EntityTypes.SILVERFISH).add(EntityType.SILVERFISH);
    this.tag(TinkerTags.EntityTypes.BLAZES).add(EntityType.BLAZE);
    this.tag(TinkerTags.EntityTypes.GHASTS).add(EntityType.GHAST);
    this.tag(TinkerTags.EntityTypes.PHANTOMS).add(EntityType.PHANTOM);
    this.tag(TinkerTags.EntityTypes.SHULKERS).add(EntityType.SHULKER);
    // passive
    this.tag(TinkerTags.EntityTypes.AXOLOTLS).add(EntityType.AXOLOTL);
    this.tag(TinkerTags.EntityTypes.BEES).add(EntityType.BEE);
    this.tag(TinkerTags.EntityTypes.FROGS).add(EntityType.FROG);
    this.tag(TinkerTags.EntityTypes.SQUIDS).add(EntityType.SQUID);
    this.tag(TinkerTags.EntityTypes.STRIDERS).add(EntityType.STRIDER);
    this.tag(TinkerTags.EntityTypes.TURTLES).add(EntityType.TURTLE);
    // villager
    this.tag(TinkerTags.EntityTypes.VILLAGERS).add(EntityType.VILLAGER, EntityType.WANDERING_TRADER, EntityType.ZOMBIE_VILLAGER);
    this.tag(TinkerTags.EntityTypes.ILLAGERS).add(EntityType.EVOKER, EntityType.ILLUSIONER, EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.WITCH);
    this.tag(TinkerTags.EntityTypes.PIGLINS).add(EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.ZOMBIFIED_PIGLIN);

    // melting
    this.tag(TinkerTags.EntityTypes.MELTING_SHOW).add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER, EntityType.PLAYER);
    this.tag(TinkerTags.EntityTypes.MELTING_HIDE).add(EntityType.GIANT);
    // meltable
    this.tag(TinkerTags.EntityTypes.MELTABLE_FARM_ANIMALS).add(
      EntityType.CHICKEN, EntityType.RABBIT,
      EntityType.COW, EntityType.MOOSHROOM,
      EntityType.PIG, EntityType.HOGLIN,
      EntityType.SHEEP, EntityType.GOAT,
      EntityType.COD, EntityType.SALMON, EntityType.TROPICAL_FISH);
    this.tag(TinkerTags.EntityTypes.MELTABLE_ZOMBIE).add(EntityType.ZOMBIE, EntityType.HUSK, EntityType.ZOMBIE_HORSE);
    this.tag(TinkerTags.EntityTypes.MELTABLE_DROWNED).add(EntityType.DROWNED);
    this.tag(TinkerTags.EntityTypes.MELTABLE_SKELETON).addTag(EntityTypeTags.SKELETONS).add(EntityType.SKELETON_HORSE);
    this.tag(TinkerTags.EntityTypes.MELTABLE_ENDER).add(EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON);
    this.tag(TinkerTags.EntityTypes.MELTABLE_SLIME).add(EntityType.SLIME);
    this.tag(TinkerTags.EntityTypes.MELTABLE_MAGMA).add(EntityType.MAGMA_CUBE);

    // behavior
    this.tag(EntityTypeTags.FROG_FOOD).add(TinkerWorld.skySlimeEntity.get(), TinkerWorld.enderSlimeEntity.get(), TinkerWorld.terracubeEntity.get());
    this.tag(EntityTypeTags.ARROWS).add(TinkerTools.materialArrow.get());

    // compatability
    this.tag(TinkerTags.EntityTypes.BOBBERS).add(TinkerTools.fishingHook.get());

    // tool logic
    // players use tool daamge util
    this.tag(TinkerTags.EntityTypes.DAMAGE_MODIFIER_BLACKLIST).add(EntityType.PLAYER);
    this.tag(TinkerTags.EntityTypes.PIGGYBACKPACK_BLACKLIST);
    this.tag(TinkerTags.EntityTypes.SMALL_ARMOR).addTag(TinkerTags.EntityTypes.SLIMES);

    // projectile logic
    this.tag(TRIDENTS).add(EntityType.TRIDENT, TinkerTools.thrownTool.get());
    this.tag(TinkerTags.EntityTypes.REUSABLE_AMMO).addTag(TRIDENTS);

    // modifiers
    this.tag(TinkerTags.EntityTypes.KILLAGERS).addTags(TinkerTags.EntityTypes.VILLAGERS, TinkerTags.EntityTypes.ILLAGERS).add(EntityType.IRON_GOLEM, EntityType.RAVAGER);
    this.tag(TinkerTags.EntityTypes.BACON_PRODUCER).add(EntityType.PIG, EntityType.PIGLIN, EntityType.HOGLIN);
    // in theory this could just be reusable ammo, but it seems better to keep separate
    this.tag(TinkerTags.EntityTypes.ENDERFERENCE_ARROW_BLACKLIST).addTag(TRIDENTS);
    // prevent dummy from healing you with necrotic
    this.tag(NECROTIC_BLACKLIST)
      .addOptional(new ResourceLocation("dummmmmmy", "target_dummy"))
      .addOptionalTag(commonResource(NECROTIC_BLACKLIST.location().getPath()));

    // collecting - TODO 1.21: remove legacy tags
    this.tag(COLLECTABLES).add(
        EntityType.ITEM, TinkerTools.indestructibleItem.get(),
        EntityType.EXPERIENCE_ORB
      ).addTags(TRIDENTS, DISCARDABLE_COLLECTABLES)
      .addOptionalTag(commonResource(COLLECTABLES.location().getPath()));
    this.tag(DISCARDABLE_COLLECTABLES).add(EntityType.ARROW, EntityType.SPECTRAL_ARROW, TinkerTools.materialArrow.get())
      .addOptionalTag(commonResource(DISCARDABLE_COLLECTABLES.location().getPath()));

    // reflecting - TODO 1.21: remove legacy tags
    this.tag(REFLECTING_BLACKLIST).addOptionalTag(commonResource(REFLECTING_BLACKLIST.location().getPath()));
    this.tag(REFLECTING_PRESERVE_OWNER).add(EntityType.FISHING_BOBBER, TinkerTools.fishingHook.get())
      .addOptionalTag(commonResource(REFLECTING_PRESERVE_OWNER.location().getPath()));
  }

  @Override
  public String getName() {
    return "Tinkers Construct Entity Type TinkerTags";
  }
}
