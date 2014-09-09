package tconstruct.tools.items;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import mantle.books.*;
import net.minecraft.util.*;
import org.w3c.dom.Document;
import tconstruct.client.TProxyClient;

public class ManualInfo
{
    //    static String[] name = new String[] { "beginner", "toolstation", "smeltery", "diary" };
    //    static String[] textureName = new String[] { "tinkerbook_diary", "tinkerbook_toolstation", "tinkerbook_smeltery", "tinkerbook_blue" };

    BookData beginner = new BookData();
    BookData toolStation = new BookData();
    BookData smeltery = new BookData();
    BookData diary = new BookData();

    /*        diary = readManual("/assets/tinker/manuals/diary.xml", dbFactory);
    volume1 = readManual("/assets/tinker/manuals/firstday.xml", dbFactory);
    volume2 = readManual("/assets/tinker/manuals/materials.xml", dbFactory);
    smelter = readManual("/assets/tinker/manuals/smeltery.xml", dbFactory);*/

    public ManualInfo()
    {
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        beginner = initManual(beginner, "tconstruct.manual.beginner", "\u00a7o" + StatCollector.translateToLocal("manual1.tooltip"), side == Side.CLIENT ? TProxyClient.volume1 : null, "tinker:tinkerbook_diary");
        toolStation = initManual(toolStation, "tconstruct.manual.toolstation", "\u00a7o" + StatCollector.translateToLocal("manual2.tooltip"), side == Side.CLIENT ? TProxyClient.volume2 : null, "tinker:tinkerbook_toolstation");
        smeltery = initManual(smeltery, "tconstruct.manual.smeltery", "\u00a7o" + StatCollector.translateToLocal("manual3.tooltip"), side == Side.CLIENT ? TProxyClient.smelter : null, "tinker:tinkerbook_smeltery");
        diary = initManual(diary, "tconstruct.manual.diary", "\u00a7o" + StatCollector.translateToLocal("manual4.tooltip"), side == Side.CLIENT ? TProxyClient.diary : null, "tinker:tinkerbook_blue");

    }

    public BookData initManual (BookData data, String unlocName, String toolTip, Document xmlDoc, String itemImage)
    {
        //proxy.readManuals();
        data.unlocalizedName = unlocName;
        data.toolTip = unlocName;
        data.modID = "TConstruct";
        data.itemImage = new ResourceLocation(data.modID, itemImage);
        data.doc = xmlDoc;
        BookDataStore.addBook(data);
        return data;
    }

}
