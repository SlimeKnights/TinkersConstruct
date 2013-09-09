package tconstruct.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tconstruct.client.RenderItemCopy;
import tconstruct.client.TProxyClient;
import tconstruct.client.block.SmallFontRenderer;
import tconstruct.client.pages.BookPage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiManual extends GuiScreen
{
    ItemStack itemstackBook;
    Document manual;
    public RenderItemCopy renderitem = new RenderItemCopy();
    int bookImageWidth = 206;
    int bookImageHeight = 200;
    int bookTotalPages = 1;
    int currentPage;
    int maxPages;

    private TurnPageButton buttonNextPage;
    private TurnPageButton buttonPreviousPage;

    BookPage pageLeft;
    BookPage pageRight;

    public SmallFontRenderer fonts = TProxyClient.smallFontRenderer;

    public GuiManual(ItemStack stack, Document doc)
    {
        this.mc = Minecraft.getMinecraft();
        this.itemstackBook = stack;
        currentPage = 0; //Stack page
        manual = doc;
        //renderitem.renderInFrame = true;
    }

    /*@Override
    public void setWorldAndResolution (Minecraft minecraft, int w, int h)
    {
        this.guiParticles = new GuiParticle(minecraft);
        this.mc = minecraft;
        this.width = w;
        this.height = h;
        this.buttonList.clear();
        this.initGui();
    }*/

    public void initGui ()
    {
        maxPages = manual.getElementsByTagName("page").getLength();
        updateText();
        int xPos = (this.width) / 2;
        this.buttonList.add(this.buttonNextPage = new TurnPageButton(1, xPos + bookImageWidth - 50, 180, true));
        this.buttonList.add(this.buttonPreviousPage = new TurnPageButton(2, xPos - bookImageWidth + 24, 180, false));
    }

    protected void actionPerformed (GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == 1)
                currentPage += 2;
            if (button.id == 2)
                currentPage -= 2;

            updateText();
        }
    }

    void updateText ()
    {
        if (maxPages % 2 == 1)
        {
            if (currentPage > maxPages)
                currentPage = maxPages;
        }
        else
        {
            if (currentPage >= maxPages)
                currentPage = maxPages - 2;
        }
        if (currentPage % 2 == 1)
            currentPage--;
        if (currentPage < 0)
            currentPage = 0;

        NodeList nList = manual.getElementsByTagName("page");

        Node node = nList.item(currentPage);
        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
            Element element = (Element) node;
            Class clazz = TProxyClient.getPageClass(element.getAttribute("type"));
            if (clazz != null)
            {
                try
                {
                    pageLeft = (BookPage) clazz.newInstance();
                    pageLeft.init(this, 0);
                    pageLeft.readPageFromXML(element);
                }
                catch (Exception e)
                {
                }
            }
            else
            {
                pageLeft = null;
            }
        }

        node = nList.item(currentPage + 1);
        if (node != null && node.getNodeType() == Node.ELEMENT_NODE)
        {
            Element element = (Element) node;
            Class clazz = TProxyClient.getPageClass(element.getAttribute("type"));
            if (clazz != null)
            {
                try
                {
                    pageRight = (BookPage) clazz.newInstance();
                    pageRight.init(this, 1);
                    pageRight.readPageFromXML(element);
                }
                catch (Exception e)
                {
                }
            }
            else
            {
                pageLeft = null;
            }
        }
        else
        {
            pageRight = null;
        }
    }

    private static final ResourceLocation bookRight = new ResourceLocation("tinker", "textures/gui/bookright.png");
    private static final ResourceLocation bookLeft = new ResourceLocation("tinker", "textures/gui/bookleft.png");

    public void drawScreen (int par1, int par2, float par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(bookRight);
        int localWidth = (this.width) / 2;
        byte localHeight = 8;
        this.drawTexturedModalRect(localWidth, localHeight, 0, 0, this.bookImageWidth, this.bookImageHeight);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(bookLeft);
        localWidth = localWidth - this.bookImageWidth;
        this.drawTexturedModalRect(localWidth, localHeight, 256 - this.bookImageWidth, 0, this.bookImageWidth, this.bookImageHeight);

        super.drawScreen(par1, par2, par3); //16, 12, 220, 12

        if (pageLeft != null)
            pageLeft.renderBackgroundLayer(localWidth + 16, localHeight + 12);
        if (pageRight != null)
            pageRight.renderBackgroundLayer(localWidth + 220, localHeight + 12);

        if (pageLeft != null)
            pageLeft.renderContentLayer(localWidth + 16, localHeight + 12);
        if (pageRight != null)
            pageRight.renderContentLayer(localWidth + 220, localHeight + 12);
    }

    public Minecraft getMC ()
    {
        return mc;
    }

    public boolean doesGuiPauseGame ()
    {
        return false;
    }
}
