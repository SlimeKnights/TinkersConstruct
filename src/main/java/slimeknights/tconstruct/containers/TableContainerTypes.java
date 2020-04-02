package slimeknights.tconstruct.containers;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.tables.inventory.chest.PartChestContainer;
import slimeknights.tconstruct.tables.inventory.chest.PatternChestContainer;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@SuppressWarnings("unused")
@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TableContainerTypes {

  public static final ContainerType<PatternChestContainer> pattern_chest = injected();
  public static final ContainerType<PartChestContainer> part_chest = injected();

  @SubscribeEvent
  static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
    BaseRegistryAdapter<ContainerType<?>> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registry.register(IForgeContainerType.create(PatternChestContainer::new), "pattern_chest");
    registry.register(IForgeContainerType.create(PartChestContainer::new), "part_chest");
  }
}
