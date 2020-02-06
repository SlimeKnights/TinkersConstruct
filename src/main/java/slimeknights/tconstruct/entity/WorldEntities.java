package slimeknights.tconstruct.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.world.entity.BlueSlimeEntity;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class WorldEntities {

  public static final EntityType<BlueSlimeEntity> blue_slime_entity = EntityType.Builder
    .create(BlueSlimeEntity::new, EntityClassification.MONSTER)
    .setShouldReceiveVelocityUpdates(true)
    .setUpdateInterval(5)
    .setTrackingRange(64)
    .size(2.04F, 2.04F)
    .setCustomClientFactory((spawnEntity, world) -> WorldEntities.blue_slime_entity.create(world))
    .build(Util.prefix("blue_slime_entity"));

  @SubscribeEvent
  public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    BaseRegistryAdapter<EntityType<?>> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registry.register(blue_slime_entity, "blue_slime_entity");
  }

  private WorldEntities() {}
}
