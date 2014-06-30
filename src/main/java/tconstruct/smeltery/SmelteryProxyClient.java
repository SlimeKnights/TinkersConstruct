package tconstruct.smeltery;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.client.gui.AdaptiveSmelteryGui;
import tconstruct.client.gui.SmelteryGui;
import tconstruct.common.TProxyCommon;
import tconstruct.smeltery.logic.AdaptiveSmelteryLogic;
import tconstruct.smeltery.logic.SmelteryLogic;
import tconstruct.util.config.PHConstruct;

public class SmelteryProxyClient extends SmelteryProxyCommon
{
    public SmelteryProxyClient()
    {
        MinecraftForge.EVENT_BUS.register(this);
        registerGuiHandler();
    }
    
    @Override
    protected void registerGuiHandler()
    {
        super.registerGuiHandler();
        TProxyCommon.registerClientGuiHandler(smelteryGuiID, this);
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == SmelteryProxyCommon.smelteryGuiID)
        {
            if (PHConstruct.newSmeltery)
                return new AdaptiveSmelteryGui(player.inventory, (AdaptiveSmelteryLogic) world.getTileEntity(x, y, z), world, x, y, z);
            else
                return new SmelteryGui(player.inventory, (SmelteryLogic) world.getTileEntity(x, y, z), world, x, y, z);
        }
        return null;
    }

    /* Liquids */

    IIcon[] stillIcons = new IIcon[1];
    IIcon[] flowIcons = new IIcon[1];

    @SubscribeEvent
    public void preStitch (TextureStitchEvent.Pre event)
    {
        TextureMap register = event.map;
        if (register.getTextureType() == 0)
        {
            stillIcons[0] = register.registerIcon("tinker:liquid_pigiron");
            flowIcons[0] = register.registerIcon("tinker:liquid_pigiron");
        }
    }

    @SubscribeEvent
    public void postStitch (TextureStitchEvent.Post event)
    {
        if (event.map.getTextureType() == 0)
        {
            for (int i = 0; i < TinkerSmeltery.fluidBlocks.length; i++)
            {
                TinkerSmeltery.fluids[i].setIcons(TinkerSmeltery.fluidBlocks[i].getIcon(0, 0), TinkerSmeltery.fluidBlocks[i].getIcon(2, 0));
            }
            TinkerSmeltery.pigIronFluid.setIcons(stillIcons[0], flowIcons[0]);
        }
    }
}
