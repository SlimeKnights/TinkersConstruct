package slimeknights.tconstruct.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tinkering.IndestructibleEntityItem;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ToolEntities {

  public static final EntityType<IndestructibleEntityItem> indestructible_item = EntityType.Builder
    .<IndestructibleEntityItem>create(IndestructibleEntityItem::new, EntityClassification.MISC)
    .size(0.25F, 0.25F)
    .immuneToFire()
    .build(Util.prefix("indestructible_item"));

  @SubscribeEvent
  static void registerEntityTypes(final RegistryEvent.Register<EntityType<?>> event) {
    BaseRegistryAdapter<EntityType<?>> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registry.register(indestructible_item, "indestructible_item");
  }

  private ToolEntities() {}
}
