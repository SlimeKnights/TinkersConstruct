package tconstruct.landmine.logic;

import java.util.ArrayList;
import java.util.Iterator;

import tconstruct.landmine.logic.behavior.stackCombo.SpecialStackHandler;
import tconstruct.landmine.tileentity.TileEntityLandmine;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class LandmineSpecialStackLogic
{

    private final World worldObj;
    private final Entity triggerer;
    private final TileEntityLandmine tileEntity;
    private final int x, y, z;
    private final boolean isOffensive;
    private final ArrayList<ItemStack> stackEffects;

    public LandmineSpecialStackLogic(World par1World, int par2, int par3, int par4, Entity entity, boolean mayHurtPlayer, ArrayList<ItemStack> items)
    {
        worldObj = par1World;
        this.tileEntity = (TileEntityLandmine) par1World.getBlockTileEntity(par2, par3, par4);
        this.x = par2;
        this.y = par3;
        this.z = par4;
        this.triggerer = entity;
        this.isOffensive = mayHurtPlayer;
        this.stackEffects = items;
    }

    public void handleSpecialStacks ()
    {
        Iterator<SpecialStackHandler> i1 = SpecialStackHandler.handlers.iterator();

        while (i1.hasNext())
        {
            SpecialStackHandler h = i1.next();
            if (isOffensive || !h.isOffensive(stackEffects))
            {
                h.checkStack(worldObj, x, y, z, triggerer, stackEffects);
            }
        }
    }

}
