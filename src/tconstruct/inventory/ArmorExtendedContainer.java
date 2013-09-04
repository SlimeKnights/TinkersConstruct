package tconstruct.inventory;

import tconstruct.util.player.ArmorExtended;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ArmorExtendedContainer extends ActiveContainer
{
    public InventoryPlayer invPlayer;
    public ArmorExtended armor;

    public ArmorExtendedContainer(InventoryPlayer inventoryplayer, ArmorExtended armor)
    {
        invPlayer = inventoryplayer;
        this.armor = armor;

        this.addSlotToContainer(new Slot(armor, 0, 80, 17));
        this.addSlotToContainer(new Slot(armor, 1, 80, 35));
        this.addSlotToContainer(new Slot(armor, 2, 116, 17));
        this.addSlotToContainer(new Slot(armor, 3, 116, 35));
        this.addSlotToContainer(new Slot(armor, 4, 152, 17));
        this.addSlotToContainer(new Slot(armor, 5, 152, 35));
        this.addSlotToContainer(new Slot(armor, 6, 152, 53));

        /* Player inventory */
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
