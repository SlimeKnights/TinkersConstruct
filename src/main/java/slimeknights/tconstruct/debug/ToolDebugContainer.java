package slimeknights.tconstruct.debug;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.mantle.inventory.OutSlot;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.items.ToolItems;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.tools.ToolBuildHandler;

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
    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }

    for (int k = 0; k < 9; ++k) {
      this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
    }

    ItemStack st = ToolItems.test_part.getItemstackWithMaterial(MaterialRegistry.getMaterial(new MaterialId("tconstruct:stone")));
    ItemStack st2 = ToolItems.test_part.getItemstackWithMaterial(MaterialRegistry.getMaterial(new MaterialId("tconstruct:wood")));
    ItemStack st3 = ToolItems.test_part.getItemstackWithMaterial(MaterialRegistry.getMaterial(new MaterialId("tconstruct:ardite")));
    ItemStack itemStack = ToolBuildHandler.buildItemFromStacks(NonNullList.from(ItemStack.EMPTY, st, st2, st3), ToolItems.test_tool);
    inventory.setInventorySlotContents(0, itemStack);
  }

  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return true;
  }
}
