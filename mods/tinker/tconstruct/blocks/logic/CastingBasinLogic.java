package mods.tinker.tconstruct.blocks.logic;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.library.blocks.InventoryLogic;
import mods.tinker.tconstruct.library.crafting.CastingRecipe;
import mods.tinker.tconstruct.library.util.IPattern;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

public class CastingBasinLogic extends InventoryLogic// implements ILiquidTank, ITankContainer, ISidedInventory
{

    public CastingBasinLogic(int invSize)
    {
        super(invSize);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getDefaultName ()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
