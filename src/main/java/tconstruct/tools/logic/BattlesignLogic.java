package tconstruct.tools.logic;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/* Slots
 * 0: Battlesign item
 */

public class BattlesignLogic extends EquipLogic
{

    public BattlesignLogic()
    {
        super(1);
    }

    @Override
    public String getDefaultName ()
    {
        return "decoration.battlesign";
    }

    /* NBT */
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);
    }

    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return null; //Not a normal gui block
    }

    @Override
    public String getInventoryName ()
    {
        return null;
    }

    @Override
    public void openInventory ()
    {
    }

    @Override
    public void closeInventory ()
    {
    }
}