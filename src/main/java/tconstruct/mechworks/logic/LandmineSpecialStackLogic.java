package tconstruct.mechworks.logic;

import java.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.mechworks.landmine.behavior.stackCombo.SpecialStackHandler;

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
        this.tileEntity = (TileEntityLandmine) par1World.getTileEntity(par2, par3, par4);
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
