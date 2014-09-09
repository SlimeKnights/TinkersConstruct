package tconstruct.tools.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.library.event.ToolCraftedEvent;
import tconstruct.library.modifier.IModifyable;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.logic.*;

public class ToolForgeContainer extends ToolStationContainer
{

    public ToolForgeContainer(InventoryPlayer inventoryplayer, ToolForgeLogic logic)
    {
        super(inventoryplayer, logic);
    }

    @Override
    public void initializeContainer (InventoryPlayer inventoryplayer, ToolStationLogic builderlogic)
    {
        invPlayer = inventoryplayer;
        this.logic = builderlogic;

        toolSlot = new SlotToolForge(inventoryplayer.player, logic, 0, 225, 38);
        this.addSlotToContainer(toolSlot);
        slots = new Slot[] { new Slot(logic, 1, 167, 29), new Slot(logic, 2, 169, 29), new Slot(logic, 3, 167, 47), new Slot(logic, 4, 149, 47) };

        for (int iter = 0; iter < 4; iter++)
            this.addSlotToContainer(slots[iter]);

        /* Player inventory */
        for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
                this.addSlotToContainer(new Slot(inventoryplayer, row + column * 9 + 9, 118 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
            this.addSlotToContainer(new Slot(inventoryplayer, column, 118 + column * 18, 142));
        }
    }

    // posX and posY must be the same length
    @Override
    public void resetSlots (int[] posX, int[] posY)
    {
        inventorySlots.clear();
        inventoryItemStacks.clear();
        this.addSlotToContainer(toolSlot);
        for (int iter = 0; iter < 4; iter++)
        {
            slots[iter].xDisplayPosition = posX[iter] + 111;
            slots[iter].yDisplayPosition = posY[iter] + 1;
            addSlotToContainer(slots[iter]);
        }

        for (int column = 0; column < 3; column++)
        {
            for (int row = 0; row < 9; row++)
            {
                this.addSlotToContainer(new Slot(invPlayer, row + column * 9 + 9, 118 + row * 18, 84 + column * 18));
            }
        }

        for (int column = 0; column < 9; column++)
        {
            this.addSlotToContainer(new Slot(invPlayer, column, 118 + column * 18, 142));
        }
    }

    @Override
    protected void craftTool (ItemStack stack)
    {
        if (stack.getItem() instanceof IModifyable)
        {
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag(((IModifyable) stack.getItem()).getBaseTagName());
            Boolean full = (logic.getStackInSlot(2) != null || logic.getStackInSlot(3) != null || logic.getStackInSlot(4) != null);
            for (int i = 2; i <= 4; i++)
                logic.decrStackSize(i, 1);
            ItemStack compare = logic.getStackInSlot(1);
            int amount = compare.getItem() instanceof IModifyable ? compare.stackSize : 1;
            logic.decrStackSize(1, amount);
            EntityPlayer player = invPlayer.player;
            if (!player.worldObj.isRemote && full)
                player.worldObj.playAuxSFX(1021, (int) player.posX, (int) player.posY, (int) player.posZ, 0);
            MinecraftForge.EVENT_BUS.post(new ToolCraftedEvent(this.logic, player, stack));
        }
        else
        //Simply naming items
        {
            int amount = logic.getStackInSlot(1).stackSize;
            logic.decrStackSize(1, amount);
        }
    }

    @Override
    public boolean canInteractWith (EntityPlayer entityplayer)
    {
        Block block = logic.getWorldObj().getBlock(logic.xCoord, logic.yCoord, logic.zCoord);
        if (block != TinkerTools.toolForge && block != TinkerTools.craftingSlabWood)
            return false;
        return logic.isUseableByPlayer(entityplayer);
    }
}
