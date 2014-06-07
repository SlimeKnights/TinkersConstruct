package tconstruct.mechworks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
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
import tconstruct.mechworks.inventory.ContainerLandmine;
import tconstruct.mechworks.logic.TileEntityLandmine;
import tconstruct.smeltery.SmelteryProxyCommon;
import tconstruct.smeltery.logic.AdaptiveSmelteryLogic;
import tconstruct.smeltery.logic.SmelteryLogic;
import tconstruct.tools.ToolProxyCommon;
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
    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == MechworksProxyCommon.landmineID)
            return new GuiLandmine(new ContainerLandmine(player, (TileEntityLandmine) world.getTileEntity(x, y, z)));
        return null;
    }
}
