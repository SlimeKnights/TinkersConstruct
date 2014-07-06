package tconstruct.mechworks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import tconstruct.armor.ArmorProxyCommon;
import tconstruct.armor.gui.ArmorExtendedGui;
import tconstruct.armor.gui.KnapsackGui;
import tconstruct.client.TProxyClient;
import tconstruct.client.entity.item.ExplosiveRender;
import tconstruct.client.tabs.TabRegistry;
import tconstruct.common.TProxyCommon;
import tconstruct.mechworks.entity.item.EntityLandmineFirework;
import tconstruct.mechworks.entity.item.ExplosivePrimed;
import tconstruct.mechworks.gui.GuiLandmine;
import tconstruct.mechworks.inventory.ContainerLandmine;
import tconstruct.mechworks.logic.TileEntityLandmine;
import tconstruct.smeltery.SmelteryProxyCommon;
import tconstruct.smeltery.gui.AdaptiveSmelteryGui;
import tconstruct.smeltery.gui.SmelteryGui;
import tconstruct.smeltery.logic.AdaptiveSmelteryLogic;
import tconstruct.smeltery.logic.SmelteryLogic;
import tconstruct.tools.ToolProxyCommon;
import tconstruct.tools.gui.CraftingStationGui;
import tconstruct.tools.gui.FrypanGui;
import tconstruct.tools.gui.FurnaceGui;
import tconstruct.tools.gui.PartCrafterGui;
import tconstruct.tools.gui.PatternChestGui;
import tconstruct.tools.gui.StencilTableGui;
import tconstruct.tools.gui.ToolForgeGui;
import tconstruct.tools.gui.ToolStationGui;
import tconstruct.tools.logic.CraftingStationLogic;
import tconstruct.tools.logic.FrypanLogic;
import tconstruct.tools.logic.FurnaceLogic;
import tconstruct.tools.logic.PartBuilderLogic;
import tconstruct.tools.logic.PatternChestLogic;
import tconstruct.tools.logic.StencilTableLogic;
import tconstruct.tools.logic.ToolForgeLogic;
import tconstruct.tools.logic.ToolStationLogic;
import tconstruct.util.config.PHConstruct;

public class MechworksProxyClient extends MechworksProxyCommon
{    
    public void initialize()
    {
        registerRenderer();
        registerGuiHandler();        
    }

    void registerRenderer()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityLandmineFirework.class, new RenderSnowball(Items.fireworks));
        RenderingRegistry.registerEntityRenderingHandler(ExplosivePrimed.class, new ExplosiveRender());
    }
    
    protected void registerGuiHandler()
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
