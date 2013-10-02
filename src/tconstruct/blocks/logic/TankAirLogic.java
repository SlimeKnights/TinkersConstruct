package tconstruct.blocks.logic;

import java.util.ArrayList;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import tconstruct.TConstruct;
import tconstruct.blocks.component.TankAirComponent;
import tconstruct.library.blocks.InventoryLogic;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IMasterLogic;
import tconstruct.library.util.IServantLogic;

public class TankAirLogic extends InventoryLogic implements IServantLogic
{
    TankAirComponent multitank = new TankAirComponent(TConstruct.ingotLiquidValue * 18);
    CoordTuple master;

    public TankAirLogic()
    {
        super(1);
    }
    
    public void overrideFluids(ArrayList<FluidStack> fluids)
    {
        multitank.overrideFluids(fluids);
    }

    @Override
    public Container getGuiContainer (InventoryPlayer inventoryplayer, World world, int x, int y, int z)
    {
        return null; //Not a gui block
    }

    @Override
    protected String getDefaultName ()
    {
        return null; //Not a gui block
    }

    @Override
    public CoordTuple getMasterPosition ()
    {
        return master;
    }

    @Override
    public void notifyMasterOfChange ()
    {
        //Probably not useful here
    }

    @Override
    public boolean setPotentialMaster (IMasterLogic master, World world, int xMaster, int yMaster, int zMaster)
    {
        return false; //Master should be verified right after placement
    }

    @Override
    public boolean verifyMaster (IMasterLogic logic, World world, int xMaster, int yMaster, int zMaster)
    {
        if (master != null)
            return false;

        master = new CoordTuple(xMaster, yMaster, zMaster);
        return true;
    }

    @Override
    public void invalidateMaster (IMasterLogic master, World world, int xMaster, int yMaster, int zMaster)
    {
        world.setBlockToAir(xCoord, yCoord, zCoord);
    }
}
