package tconstruct.client.tabs;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;

public class TabRegistry
{
    private static ArrayList<AbstractTab> tabList = new ArrayList<AbstractTab>();

    public static void registerTab (AbstractTab tab)
    {
        tabList.add(tab);
    }

    public static ArrayList<AbstractTab> getTabList ()
    {
        return tabList;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void guiPostInit (GuiScreenEvent.InitGuiEvent.Post event)
    {
        if ((event.gui instanceof GuiInventory))
        {
            int xSize = 176;
            int ySize = 166;
            int guiLeft = (event.gui.width - xSize) / 2;
            int guiTop = (event.gui.height - ySize) / 2;

            updateTabValues(guiLeft, guiTop, InventoryTabVanilla.class);
            addTabsToList(event.gui.buttonList);
        }
    }

    private static Minecraft mc = FMLClientHandler.instance().getClient();

    public static void openInventoryGui ()
    {
        mc.thePlayer.closeScreen();
        GuiInventory inventory = new GuiInventory(mc.thePlayer);
        mc.displayGuiScreen(inventory);
    }

    public static void updateTabValues (int cornerX, int cornerY, Class<?> selectedButton)
    {
        int count = 2;
        for (int i = 0; i < tabList.size(); i++)
        {
            AbstractTab t = tabList.get(i);

            if (t.shouldAddToList())
            {
                t.id = count;
                t.xPosition = cornerX + (count - 2) * 28;
                t.yPosition = cornerY - 28;
                t.enabled = !t.getClass().equals(selectedButton);
                count++;
            }
        }
    }

    public static void addTabsToList (List buttonList)
    {
        for (AbstractTab tab : tabList)
        {
            if (tab.shouldAddToList())
            {
                buttonList.add(tab);
            }
        }
    }
}
