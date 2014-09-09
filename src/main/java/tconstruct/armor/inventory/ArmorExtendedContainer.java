package tconstruct.armor.inventory;

import net.minecraft.entity.player.*;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import tconstruct.armor.player.ArmorExtended;
import tconstruct.smeltery.inventory.ActiveContainer;

public class ArmorExtendedContainer extends ActiveContainer
{
    public InventoryPlayer invPlayer;
    public ArmorExtended armor;

    public ArmorExtendedContainer(InventoryPlayer inventoryplayer, ArmorExtended armor)
    {
        invPlayer = inventoryplayer;
        this.armor = armor;

        this.addSlotToContainer(new SlotAccessory(armor, 0, 80, 17));
        this.addSlotToContainer(new SlotAccessory(armor, 1, 80, 35));
        this.addSlotToContainer(new SlotAccessory(armor, 2, 116, 17));
        this.addSlotToContainer(new SlotAccessory(armor, 3, 116, 35));
        this.addSlotToContainer(new SlotAccessory(armor, 4, 152, 53));
        this.addSlotToContainer(new SlotAccessory(armor, 5, 152, 35));
        this.addSlotToContainer(new SlotAccessory(armor, 6, 152, 17));
        for (int playerArmor = 0; playerArmor < 4; ++playerArmor)
        {
            this.addSlotToContainer(new SlotArmorCopy(this, inventoryplayer, inventoryplayer.getSizeInventory() - 1 - playerArmor, 98, 8 + playerArmor * 18, playerArmor));
        }

        for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
                this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 8 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 8 + column * 18, 142));
        }

    }

    @Override
    public boolean canInteractWith (EntityPlayer var1)
    {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot (EntityPlayer player, int slotID)
    {
        return null;
    }
}
