package mods.tinker.tconstruct.client;

import mods.tinker.tconstruct.TConstruct;
import mods.tinker.tconstruct.common.TContent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.liquids.LiquidStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TClientEvents
{
    /* Sounds */
    @ForgeSubscribe
    @SideOnly(Side.CLIENT)
    public void onSound (SoundLoadEvent event)
    {
        try
        {
            event.manager.soundPoolSounds.addSound("mods/tinker/resources/sounds/frypan_hit.ogg", TConstruct.class.getResource("/mods/tinker/resources/sounds/frypan_hit.ogg"));
            event.manager.soundPoolSounds.addSound("mods/tinker/resources/sounds/little_saw.ogg", TConstruct.class.getResource("/mods/tinker/resources/sounds/little_saw.ogg"));
            System.out.println("[TConstruct] Successfully loaded sounds.");
        }
        catch (Exception e)
        {
            System.err.println("[TConstruct] Failed to register one or more sounds");
        }
    }

    /* Liquids */
    @ForgeSubscribe
    @SideOnly(Side.CLIENT)
    public void postStitch (TextureStitchEvent.Post event)
    {
        for (int i = 0; i < TContent.liquidIcons.length; i++)
        {
            TContent.liquidIcons[i].setRenderingIcon(TContent.liquidMetalStill.getIcon(0, i));
            LiquidStack canon = TContent.liquidIcons[i].canonical();
            if (canon != null)
                canon.setRenderingIcon(TContent.liquidMetalStill.getIcon(0, i));
        }
    }
}
