package slimeknights.tconstruct.world;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;

import slimeknights.tconstruct.world.entity.BlueSlimeEntity;

public final class WorldEntities {

  private WorldEntities() {}

  public static EntityType<BlueSlimeEntity> blue_slime_entity = EntityType.Builder
      .create(BlueSlimeEntity::new, EntityClassification.MONSTER)
      .setShouldReceiveVelocityUpdates(true)
      .setUpdateInterval(5)
      .setTrackingRange(64)
      .size(2.04F, 2.04F)
      .setCustomClientFactory((spawnEntity, world) -> TinkerWorld.blue_slime_entity.create(world)).build("tconstruct:blue_slime_entity");

}
