package tconstruct.plugins.nei;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.recipe.DefaultOverlayHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.client.gui.inventory.GuiContainer;
import tconstruct.client.gui.CraftingStationGui;

import java.awt.*;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 3/11/13
 * Time: 10:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class NEI_TConstructCraftingTables_Config implements IConfigureNEI
{
    @Override
    public void loadConfig()
    {
        API.registerGuiOverlay(CraftingStationGui.class, "crafting");
        API.registerGuiOverlayHandler(CraftingStationGui.class, new DefaultOverlayHandler(), "crafting");
        TemplateRecipeHandler.RecipeTransferRectHandler.registerRectsToGuis(
                Arrays.<Class<? extends GuiContainer>>asList(CraftingStationGui.class),
                Arrays.asList(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(84, 23, 24, 18), "crafting")));
        FMLLog.getLogger().info("Tinkers Construct crafting tables NEI plugin loaded");
    }

    @Override
    public String getName()
    {
        return "Tinkers Construct crafting tables Plugin";
    }

    @Override
    public String getVersion()
    {
        return "0.1";
    }
}
