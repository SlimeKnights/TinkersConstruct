package slimeknights.tconstruct.debug;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.mantle.inventory.OutSlot;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;

@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ToolDebugContainer extends Container {

  public static final ContainerType<ToolDebugContainer> TOOL_DEBUG_CONTAINER_TYPE = new ContainerType<>(ToolDebugContainer::new);

  @SubscribeEvent
  static void registerItems(final RegistryEvent.Register<ContainerType<?>> event) {
    TOOL_DEBUG_CONTAINER_TYPE.setRegistryName(Util.getResource("tool_debug"));
    event.getRegistry().register(TOOL_DEBUG_CONTAINER_TYPE);
  }

  Inventory inventory;

  public ToolDebugContainer(int id, PlayerInventory playerInventory) {
    super(TOOL_DEBUG_CONTAINER_TYPE, id);

    this.inventory = new Inventory(1);

    this.addSlot(new OutSlot(inventory, 0, 50, 50));
    for(int i = 0; i < 3; ++i) {
      for(int j = 0; j < 9; ++j) {
        this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }

    for(int k = 0; k < 9; ++k) {
      this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
    }
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return true;
  }
}
