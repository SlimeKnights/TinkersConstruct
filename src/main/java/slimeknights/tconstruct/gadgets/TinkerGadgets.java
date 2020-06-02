package slimeknights.tconstruct.gadgets;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.gadgets.entity.EflnBallEntity;
import slimeknights.tconstruct.gadgets.entity.FancyItemFrameEntity;
import slimeknights.tconstruct.gadgets.entity.GlowballEntity;
import slimeknights.tconstruct.gadgets.item.PiggyBackPackItem;
import slimeknights.tconstruct.items.GadgetItems;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.capability.piggyback.CapabilityTinkerPiggyback;
import slimeknights.tconstruct.shared.block.SlimeBlock;

@Pulse(id = TinkerPulseIds.TINKER_GADGETS_PULSE_ID, description = "All the fun toys")
@ObjectHolder(TConstruct.modID)
public class TinkerGadgets extends TinkerPulse {

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_GADGETS_PULSE_ID);

  public static EntityType<FancyItemFrameEntity> fancy_item_frame;
  public static EntityType<GlowballEntity> throwable_glow_ball;
  public static EntityType<EflnBallEntity> throwable_efln_ball;

  @SubscribeEvent
  public void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    IForgeRegistry<EntityType<?>> registry = event.getRegistry();

    fancy_item_frame = EntityType.Builder.<FancyItemFrameEntity>create(
      FancyItemFrameEntity::new, EntityClassification.MISC)
      .size(0.5F, 0.5F)
      .setTrackingRange(160)
      .setUpdateInterval(Integer.MAX_VALUE)
      .setCustomClientFactory((spawnEntity, world) -> new FancyItemFrameEntity(fancy_item_frame, world))
      .setShouldReceiveVelocityUpdates(false)
      .build("fancy_item_frame");

    registry.register(fancy_item_frame.setRegistryName("tconstruct:fancy_item_frame"));

    throwable_glow_ball = EntityType.Builder.<GlowballEntity>create(
      GlowballEntity::new, EntityClassification.MISC)
      .size(0.25F, 0.25F)
      .setTrackingRange(64)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new GlowballEntity(throwable_glow_ball, world))
      .setShouldReceiveVelocityUpdates(true)
      .build("glow_ball");

    registry.register(throwable_glow_ball.setRegistryName("tconstruct:glow_ball"));

    throwable_efln_ball = EntityType.Builder.<EflnBallEntity>create(
      EflnBallEntity::new, EntityClassification.MISC)
      .size(0.25F, 0.25F)
      .setTrackingRange(64)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new EflnBallEntity(throwable_efln_ball, world))
      .setShouldReceiveVelocityUpdates(true)
      .build("efln_ball");

    registry.register(throwable_efln_ball.setRegistryName("tconstruct:efln_ball"));
  }

  @SubscribeEvent
  public void registerPotions(RegistryEvent.Register<Effect> event) {
    IForgeRegistry<Effect> registry = event.getRegistry();

    registry.register(PiggyBackPackItem.CarryPotionEffect.INSTANCE);
  }

  @SubscribeEvent
  public void commonSetup(final FMLCommonSetupEvent event) {
    CapabilityTinkerPiggyback.register();
    MinecraftForge.EVENT_BUS.register(new GadgetEvents());
    TinkerRegistry.tabGadgets.setDisplayIcon(new ItemStack(GadgetItems.slime_sling.get(SlimeBlock.SlimeType.GREEN)));
  }
}
