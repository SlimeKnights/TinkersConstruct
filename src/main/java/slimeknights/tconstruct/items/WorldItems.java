package slimeknights.tconstruct.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registry.ItemRegistryAdapter;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.world.WorldEntities;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@SuppressWarnings("unused")
@ObjectHolder(TConstruct.modID)
public final class WorldItems {

  public static final SpawnEggItem blue_slime_spawn_egg = injected();

  static void registerItems(final RegistryEvent.Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry(), TinkerRegistry.tabWorld);

    registry.register(getBlueSlimeSpawnEgg(), "blue_slime_spawn_egg");
  }

  private static SpawnEggItem getBlueSlimeSpawnEgg() {
    return new SpawnEggItem(WorldEntities.blue_slime_entity, 0x47eff5, 0xacfff4, (new Item.Properties()).group(ItemGroup.MISC));
  }

  private WorldItems() {}
}
