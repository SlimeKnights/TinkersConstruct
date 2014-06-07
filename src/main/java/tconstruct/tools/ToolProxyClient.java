package tconstruct.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import tconstruct.TConstruct;
import tconstruct.armor.ArmorProxyCommon;
import tconstruct.client.TProxyClient;
import tconstruct.client.gui.AdaptiveSmelteryGui;
import tconstruct.client.gui.ArmorExtendedGui;
import tconstruct.client.gui.CraftingStationGui;
import tconstruct.client.gui.FrypanGui;
import tconstruct.client.gui.FurnaceGui;
import tconstruct.client.gui.GuiLandmine;
import tconstruct.client.gui.KnapsackGui;
import tconstruct.client.gui.PartCrafterGui;
import tconstruct.client.gui.PatternChestGui;
import tconstruct.client.gui.SmelteryGui;
import tconstruct.client.gui.StencilTableGui;
import tconstruct.client.gui.ToolForgeGui;
import tconstruct.client.gui.ToolStationGui;
import tconstruct.client.tabs.TabRegistry;
import tconstruct.mechworks.MechworksProxyCommon;
import tconstruct.mechworks.inventory.ContainerLandmine;
import tconstruct.mechworks.logic.TileEntityLandmine;
import tconstruct.smeltery.SmelteryProxyCommon;
import tconstruct.smeltery.logic.AdaptiveSmelteryLogic;
import tconstruct.smeltery.logic.SmelteryLogic;
import tconstruct.tools.logic.CraftingStationLogic;
import tconstruct.tools.logic.FrypanLogic;
import tconstruct.tools.logic.FurnaceLogic;
import tconstruct.tools.logic.PartBuilderLogic;
import tconstruct.tools.logic.PatternChestLogic;
import tconstruct.tools.logic.StencilTableLogic;
import tconstruct.tools.logic.ToolForgeLogic;
import tconstruct.tools.logic.ToolStationLogic;
import tconstruct.util.config.PHConstruct;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ToolProxyClient extends ToolProxyCommon
{
    public ToolProxyClient()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == ToolProxyCommon.toolStationID)
            return new ToolStationGui(player.inventory, (ToolStationLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == ToolProxyCommon.partBuilderID)
            return new PartCrafterGui(player.inventory, (PartBuilderLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == ToolProxyCommon.patternChestID)
            return new PatternChestGui(player.inventory, (PatternChestLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == ToolProxyCommon.frypanGuiID)
            return new FrypanGui(player.inventory, (FrypanLogic) world.getTileEntity(x, y, z), world, x, y, z);

        if (ID == ToolProxyCommon.stencilTableID)
            return new StencilTableGui(player.inventory, (StencilTableLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == ToolProxyCommon.toolForgeID)
            return new ToolForgeGui(player.inventory, (ToolForgeLogic) world.getTileEntity(x, y, z), world, x, y, z);
        if (ID == ToolProxyCommon.craftingStationID)
            return new CraftingStationGui(player.inventory, (CraftingStationLogic) world.getTileEntity(x, y, z), world, x, y, z);

        if (ID == ToolProxyCommon.furnaceID)
            return new FurnaceGui(player.inventory, (FurnaceLogic) world.getTileEntity(x, y, z));

        return null;
    }
    
    @SubscribeEvent
    public void onSound (SoundLoadEvent event)
    {
        try
        {
            /*
             * SoundManager soundmanager = event.manager;
             * soundmanager.addSound("tinker:frypan_hit.ogg");
             * soundmanager.addSound("tinker:little_saw.ogg");
             * soundmanager.addSound("tinker:launcher_clank.ogg");
             * TConstruct.logger.info("Successfully loaded sounds.");
             */
        }
        catch (Exception e)
        {
            TConstruct.logger.error("Failed to register one or more sounds");
        }

        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
