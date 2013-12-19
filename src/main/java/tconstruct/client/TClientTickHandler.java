package tconstruct.client;

import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import tconstruct.common.TRepo;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TClientTickHandler implements ITickHandler
{
    Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void tickEnd (EnumSet<TickType> type, Object... tickData)
    {
        TRepo.oreBerry.setGraphicsLevel(Block.leaves.graphicsLevel);
        TRepo.oreBerrySecond.setGraphicsLevel(Block.leaves.graphicsLevel);
        TRepo.slimeLeaves.setGraphicsLevel(Block.leaves.graphicsLevel);
    }

    @Override
    public EnumSet<TickType> ticks ()
    {
        return EnumSet.of(TickType.RENDER);
    }

    @Override
    public void tickStart (EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public String getLabel ()
    {
        return null;
    }
}
