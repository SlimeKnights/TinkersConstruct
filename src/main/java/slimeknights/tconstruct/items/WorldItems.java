package slimeknights.tconstruct.items;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.entity.WorldEntities;
import slimeknights.tconstruct.library.registration.ItemDeferredRegister;
import slimeknights.tconstruct.library.registration.object.ItemObject;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorldItems {

  private static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(TConstruct.modID);

  public static void init() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    ITEMS.register(modEventBus);
  }

  public static final ItemObject<SpawnEggItem> blue_slime_spawn_egg = ITEMS.register("blue_slime_spawn_egg", () -> new SpawnEggItem(WorldEntities.blue_slime_entity, 0x47eff5, 0xacfff4, (new Item.Properties()).group(ItemGroup.MISC)));
}
