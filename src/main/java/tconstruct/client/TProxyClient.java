package tconstruct.client;

import java.io.InputStream;
import javax.xml.parsers.*;
import mantle.client.SmallFontRenderer;
import mantle.lib.client.MantleClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import org.w3c.dom.Document;
import tconstruct.TConstruct;
import tconstruct.armor.ArmorProxyClient;
import tconstruct.armor.player.TPlayerStats;
import tconstruct.common.TProxyCommon;
import tconstruct.tools.items.ManualInfo;

public class TProxyClient extends TProxyCommon
{
    /* TODO: Split this class up into its respective parts */
    public static SmallFontRenderer smallFontRenderer;
    public static IIcon metalBall;
    public static Minecraft mc;
    public static RenderItem itemRenderer = new RenderItem();

    public void initialize ()
    {
        registerRenderer();
        readManuals();
    }

    /* Registers any rendering code. */
    public void registerRenderer ()
    {
        Minecraft mc = Minecraft.getMinecraft();
        smallFontRenderer = new SmallFontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, false);
    }

    public static Document diary;
    public static Document volume1;
    public static Document volume2;
    public static Document smelter;
    public static ManualInfo manualData;

    public void readManuals ()
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	String CurrentLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
		
	Document diary_cl = readManual("/assets/tinker/manuals/" + CurrentLanguage + "/diary.xml", dbFactory);
	Document volume1_cl = readManual("/assets/tinker/manuals/" + CurrentLanguage + "/firstday.xml", dbFactory);
	Document volume2_cl = readManual("/assets/tinker/manuals/" + CurrentLanguage + "/materials.xml", dbFactory);
	Document smelter_cl = readManual("/assets/tinker/manuals/" + CurrentLanguage + "/smeltery.xml", dbFactory);
		
	diary = readManual("/assets/tinker/manuals/en_US/diary.xml", dbFactory);
	volume1 = readManual("/assets/tinker/manuals/en_US/firstday.xml", dbFactory);
	volume2 = readManual("/assets/tinker/manuals/en_US/materials.xml", dbFactory);
	smelter = readManual("/assets/tinker/manuals/en_US/smeltery.xml", dbFactory);
		
	if(diary_cl != null)
	{
		diary = diary_cl;
	}
		
	if(volume1_cl != null)
	{
		volume1 = volume1_cl;
	}
		
	if(volume2_cl != null)
	{
		volume2 = volume2_cl;
	}
		
	if(smelter_cl != null)
	{
		smelter = smelter_cl;
	}
			
	initManualIcons();
        initManualRecipes();
        initManualPages();
        manualData = new ManualInfo();
    }

    Document readManual (String location, DocumentBuilderFactory dbFactory)
    {
        try
        {
            InputStream stream = TConstruct.class.getResourceAsStream(location);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void initManualIcons ()
    {
        MantleClientRegistry.registerManualIcon("torch", new ItemStack(Blocks.torch));
        MantleClientRegistry.registerManualIcon("sapling", new ItemStack(Blocks.sapling));
        MantleClientRegistry.registerManualIcon("workbench", new ItemStack(Blocks.crafting_table));
        MantleClientRegistry.registerManualIcon("coal", new ItemStack(Items.coal));

        MantleClientRegistry.registerManualIcon("woodplanks", new ItemStack(Blocks.planks));
        MantleClientRegistry.registerManualIcon("stoneblock", new ItemStack(Blocks.stone));
        MantleClientRegistry.registerManualIcon("ironingot", new ItemStack(Items.iron_ingot));
        MantleClientRegistry.registerManualIcon("flint", new ItemStack(Items.flint));
        MantleClientRegistry.registerManualIcon("cactus", new ItemStack(Blocks.cactus));
        MantleClientRegistry.registerManualIcon("bone", new ItemStack(Items.bone));
        MantleClientRegistry.registerManualIcon("obsidian", new ItemStack(Blocks.obsidian));
        MantleClientRegistry.registerManualIcon("netherrack", new ItemStack(Blocks.netherrack));
    }

    public void initManualRecipes ()
    {
    }

    void initManualPages ()
    {

    }

    public static Document getManualFromStack (ItemStack stack)
    {
        switch (stack.getItemDamage())
        {
        case 0:
            return volume1;
        case 1:
            return volume2;
        case 2:
            return smelter;
        case 3:
            return diary;
        }

        return null;
    }

    public void recalculateHealth ()
    {
        ArmorProxyClient.armorExtended.recalculateHealth(mc.thePlayer, TPlayerStats.get(mc.thePlayer));
    }

}
