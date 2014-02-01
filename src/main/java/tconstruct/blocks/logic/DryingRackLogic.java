package tconstruct.blocks.logic;

import cpw.mods.fml.relauncher.*;
import mantle.blocks.abstracts.InventoryLogic;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import tconstruct.library.crafting.DryingRackRecipes;

public class DryingRackLogic extends InventoryLogic
{
    int currentTime;
    int maxTime;

    public DryingRackLogic()
    {
        super(1, 1);
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public String getDefaultName ()
    {
        return "";
    }

    @Override
    public void func_145845_h ()
    {
        if (!field_145850_b.isRemote && maxTime > 0 && currentTime < maxTime)
        {
            currentTime++;
            if (currentTime >= maxTime)
            {
                inventory[0] = DryingRackRecipes.getDryingResult(inventory[0]);
                updateDryingTime();
            }
        }
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
        super.setInventorySlotContents(slot, itemstack);
        updateDryingTime();
    }

    @Override
    public ItemStack decrStackSize (int slot, int quantity)
    {
        ItemStack stack = super.decrStackSize(slot, quantity);
        maxTime = 0;
        currentTime = 0;
        return stack;
    }

    public void updateDryingTime ()
    {
        currentTime = 0;
        if (inventory[0] != null)
            maxTime = DryingRackRecipes.getDryingTime(inventory[0]);
        else
            maxTime = 0;
        field_145850_b.func_147471_g(field_145851_c, field_145848_d, field_145849_e);
    }

    @Override
    public void func_145839_a (NBTTagCompound tags)
    {
        currentTime = tags.getInteger("Time");
        maxTime = tags.getInteger("MaxTime");
        readCustomNBT(tags);
    }

    @Override
    public void func_145841_b (NBTTagCompound tags)
    {
        tags.setInteger("Time", currentTime);
        tags.setInteger("MaxTime", maxTime);
        writeCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        super.func_145839_a(tags);
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        super.func_145841_b(tags);
    }

    /* Packets */
    @Override
    public Packet func_145844_m ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new S35PacketUpdateTileEntity(field_145851_c, field_145848_d, field_145849_e, 1, tag);
    }

    @Override
    public void onDataPacket (NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        readCustomNBT(packet.data);
        field_145850_b.func_147479_m(field_145851_c, field_145848_d, field_145849_e);
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox ()
    {
        AxisAlignedBB cbb = AxisAlignedBB.getAABBPool().getAABB(field_145851_c, field_145848_d - 1, field_145849_e, field_145851_c + 1, field_145848_d + 1, field_145849_e + 1);
        return cbb;
    }
}
