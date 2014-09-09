package tconstruct.mechworks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import tconstruct.client.entity.item.ExplosiveRender;
import tconstruct.common.TProxyCommon;
import tconstruct.mechworks.entity.item.*;
import tconstruct.mechworks.gui.GuiLandmine;
import tconstruct.mechworks.inventory.ContainerLandmine;
import tconstruct.mechworks.logic.TileEntityLandmine;

public class MechworksProxyClient extends MechworksProxyCommon
{
    public void initialize ()
    {
        registerRenderer();
        registerGuiHandler();
    }

    void registerRenderer ()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityLandmineFirework.class, new RenderSnowball(Items.fireworks));
        RenderingRegistry.registerEntityRenderingHandler(ExplosivePrimed.class, new ExplosiveRender());
    }

    protected void registerGuiHandler ()
    {
        super.registerGuiHandler();
        TProxyCommon.registerClientGuiHandler(landmineID, this);
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == MechworksProxyCommon.landmineID)
            return new GuiLandmine(new ContainerLandmine(player, (TileEntityLandmine) world.getTileEntity(x, y, z)));
        return null;
    }
}
