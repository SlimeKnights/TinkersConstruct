package mods.tinker.tconstruct.client.gui;

import java.util.List;

import mods.tinker.tconstruct.client.TProxyClient;
import mods.tinker.tconstruct.crafting.PatternBuilder;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.ToolMaterial;
import mods.tinker.tconstruct.library.client.TConstructClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiParticle;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiManual extends GuiScreen
{
    ItemStack itemstackBook;
    Document manual;
    private RenderItem renderitem = new RenderItem();
    int bookImageWidth = 206;
    int bookImageHeight = 200;
    int bookTotalPages = 1;
    int currentPage;
    int maxPages;

    private TurnPageButton buttonNextPage;
    private TurnPageButton buttonPreviousPage;
    String pageLeftType;
    String textLeft;
    ItemStack[] iconsLeft;
    String[] multiTextLeft;
    ToolMaterial materialLeft;

    String pageRightType;
    String textRight;
    ItemStack[] iconsRight;
    String[] multiTextRight;
    ToolMaterial materialRight;

    public GuiManual(ItemStack stack, Document doc)
    {
        this.mc = Minecraft.getMinecraft();
        this.itemstackBook = stack;
        currentPage = 0; //Stack page
        manual = doc;
        renderitem.renderInFrame = true;
    }

    @Override
    public void setWorldAndResolution (Minecraft minecraft, int w, int h)
    {
        this.guiParticles = new GuiParticle(minecraft);
        this.mc = minecraft;
        this.fontRenderer = TProxyClient.smallFontRenderer;
        this.width = w;
        this.height = h;
        this.buttonList.clear();
        this.initGui();
    }

    public void initGui ()
    {
        multiTextLeft = new String[] { "" };
        multiTextRight = new String[] { "" };
        iconsLeft = new ItemStack[1];
        iconsRight = new ItemStack[1];
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

    void updateText () //TODO: OOP this
    {
        if (currentPage >= maxPages)
            currentPage = maxPages - 2;
        if (currentPage % 2 == 1)
            currentPage--;
        if (currentPage < 0)
            currentPage = 0;

        NodeList nList = manual.getElementsByTagName("page");

        Node node = nList.item(currentPage);
        if (node.getNodeType() == Node.ELEMENT_NODE)
        {
            Element element = (Element) node;
            pageLeftType = element.getAttribute("type");

            if (pageLeftType.equals("text") || pageLeftType.equals("intro"))
            {
                NodeList nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    textLeft = nodes.item(0).getTextContent();
            }

            else if (pageLeftType.equals("contents"))
            {
                NodeList nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    textLeft = nodes.item(0).getTextContent();

                nodes = element.getElementsByTagName("link");
                multiTextLeft = new String[nodes.getLength()];
                iconsLeft = new ItemStack[nodes.getLength()];
                for (int i = 0; i < nodes.getLength(); i++)
                {
                    NodeList children = nodes.item(i).getChildNodes();
                    multiTextLeft[i] = children.item(1).getTextContent();
                    iconsLeft[i] = TConstructClientRegistry.getManualIcon(children.item(3).getTextContent());
                }
            }

            else if (pageLeftType.equals("sidebar"))
            {
                NodeList nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    textLeft = nodes.item(0).getTextContent();

                nodes = element.getElementsByTagName("item");
                multiTextLeft = new String[nodes.getLength()];
                iconsLeft = new ItemStack[nodes.getLength()];
                for (int i = 0; i < nodes.getLength(); i++)
                {
                    NodeList children = nodes.item(i).getChildNodes();
                    multiTextLeft[i] = children.item(1).getTextContent();
                    iconsLeft[i] = TConstructClientRegistry.getManualIcon(children.item(3).getTextContent());
                }
            }

            else if (pageLeftType.equals("picture"))
            {
                NodeList nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    textLeft = nodes.item(0).getTextContent();

                nodes = element.getElementsByTagName("picture");
                if (nodes != null)
                    multiTextLeft[0] = nodes.item(0).getTextContent();
            }

            else if (pageLeftType.equals("crafting"))
            {
                NodeList nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    textLeft = nodes.item(0).getTextContent();

                nodes = element.getElementsByTagName("name");
                if (nodes != null)
                    iconsLeft = TConstructClientRegistry.getRecipeIcons(nodes.item(0).getTextContent());

                nodes = element.getElementsByTagName("size");
                if (nodes != null)
                    multiTextLeft[0] = nodes.item(0).getTextContent();
            }

            else if (pageLeftType.equals("smelting"))
            {
                NodeList nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    textLeft = nodes.item(0).getTextContent();

                nodes = element.getElementsByTagName("recipe");
                if (nodes != null)
                    iconsLeft = TConstructClientRegistry.getRecipeIcons(nodes.item(0).getTextContent());
            }
            
            else if (pageLeftType.equals("materialstats"))
            {
                NodeList nodes = element.getElementsByTagName("title");
                if (nodes != null)
                    textLeft = nodes.item(0).getTextContent();
                
                iconsLeft = new ItemStack[4];

                nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    multiTextLeft[0] = nodes.item(0).getTextContent();
                
                nodes = element.getElementsByTagName("icon");
                if (nodes != null)
                    iconsLeft[0] = TConstructClientRegistry.getManualIcon(nodes.item(0).getTextContent());                

                nodes = element.getElementsByTagName("toolmaterial");
                if (nodes != null && nodes.getLength() > 0)
                    materialLeft = TConstructRegistry.getMaterial(nodes.item(0).getTextContent());
                else
                    materialLeft = TConstructRegistry.getMaterial(textLeft);
                
                nodes = element.getElementsByTagName("material").item(0).getChildNodes();

                iconsLeft[1] = TConstructClientRegistry.getManualIcon(nodes.item(1).getTextContent());
                iconsLeft[2] = PatternBuilder.instance.getShardFromSet(materialLeft.name());
                iconsLeft[3] = PatternBuilder.instance.getRodFromSet(materialLeft.name());
            }
            
            else if (pageLeftType.equals("toolpage"))
            {
                NodeList nodes = element.getElementsByTagName("title");
                if (nodes != null)
                    textLeft = nodes.item(0).getTextContent();
                
                nodes = element.getElementsByTagName("item");
                multiTextLeft = new String[nodes.getLength() + 2];
                iconsLeft = new ItemStack[nodes.getLength() + 1];
                
                for (int i = 0; i < nodes.getLength(); i++)
                {
                    NodeList children = nodes.item(i).getChildNodes();
                    multiTextLeft[i+2] = children.item(1).getTextContent();
                    iconsLeft[i+1] = TConstructClientRegistry.getManualIcon(children.item(3).getTextContent());
                }

                nodes = element.getElementsByTagName("text");
                if (nodes != null)
                {
                    multiTextLeft[0] = nodes.item(0).getTextContent();
                    multiTextLeft[1] = nodes.item(1).getTextContent();
                }
                
                nodes = element.getElementsByTagName("icon");
                if (nodes != null)
                    iconsLeft[0] = TConstructClientRegistry.getManualIcon(nodes.item(0).getTextContent());
            }
        }

        node = nList.item(currentPage + 1);
        if (node != null && node.getNodeType() == Node.ELEMENT_NODE)
        {
            Element element = (Element) node;
            pageRightType = element.getAttribute("type");

            if (pageRightType.equals("text") || pageRightType.equals("intro"))
            {
                NodeList nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    textRight = nodes.item(0).getTextContent();
            }

            else if (pageRightType.equals("contents"))
            {
                NodeList nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    textRight = nodes.item(0).getTextContent();

                nodes = element.getElementsByTagName("link");
                multiTextRight = new String[nodes.getLength()];
                iconsRight = new ItemStack[nodes.getLength()];
                for (int i = 0; i < nodes.getLength(); i++)
                {
                    NodeList children = nodes.item(i).getChildNodes();
                    multiTextRight[i] = children.item(1).getTextContent();
                    iconsRight[i] = TConstructClientRegistry.getManualIcon(children.item(3).getTextContent());
                }
            }

            else if (pageRightType.equals("sidebar"))
            {
                NodeList nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    textRight = nodes.item(0).getTextContent();

                nodes = element.getElementsByTagName("item");
                multiTextRight = new String[nodes.getLength()];
                iconsRight = new ItemStack[nodes.getLength()];
                for (int i = 0; i < nodes.getLength(); i++)
                {
                    NodeList children = nodes.item(i).getChildNodes();
                    multiTextRight[i] = children.item(1).getTextContent();
                    iconsRight[i] = TConstructClientRegistry.getManualIcon(children.item(3).getTextContent());
                }
            }

            else if (pageRightType.equals("picture"))
            {
                NodeList nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    textRight = nodes.item(0).getTextContent();

                nodes = element.getElementsByTagName("picture");
                if (nodes != null)
                    multiTextRight[0] = nodes.item(0).getTextContent();
            }

            else if (pageRightType.equals("crafting"))
            {
                NodeList nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    textRight = nodes.item(0).getTextContent();

                nodes = element.getElementsByTagName("name");
                if (nodes != null)
                    iconsRight = TConstructClientRegistry.getRecipeIcons(nodes.item(0).getTextContent());

                nodes = element.getElementsByTagName("size");
                if (nodes != null)
                    multiTextRight[0] = nodes.item(0).getTextContent();
            }

            else if (pageRightType.equals("smelting"))
            {
                NodeList nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    textRight = nodes.item(0).getTextContent();

                nodes = element.getElementsByTagName("recipe");
                if (nodes != null)
                    iconsRight = TConstructClientRegistry.getRecipeIcons(nodes.item(0).getTextContent());
            }
            
            else if (pageRightType.equals("materialstats"))
            {
                NodeList nodes = element.getElementsByTagName("title");
                if (nodes != null)
                    textRight = nodes.item(0).getTextContent();
                
                iconsRight = new ItemStack[4];

                nodes = element.getElementsByTagName("text");
                if (nodes != null)
                    multiTextRight[0] = nodes.item(0).getTextContent();
                
                nodes = element.getElementsByTagName("icon");
                if (nodes != null)
                    iconsRight[0] = TConstructClientRegistry.getManualIcon(nodes.item(0).getTextContent());

                nodes = element.getElementsByTagName("toolmaterial");
                if (nodes != null && nodes.getLength() > 0)
                    materialRight = TConstructRegistry.getMaterial(nodes.item(0).getTextContent());
                else
                    materialRight = TConstructRegistry.getMaterial(textRight);
                
                nodes = element.getElementsByTagName("material").item(0).getChildNodes();

                iconsRight[1] = TConstructClientRegistry.getManualIcon(nodes.item(1).getTextContent());
                iconsRight[2] = PatternBuilder.instance.getShardFromSet(materialRight.name());
                iconsRight[3] = PatternBuilder.instance.getRodFromSet(materialRight.name());   
            }
            
            else if (pageRightType.equals("toolpage"))
            {
                NodeList nodes = element.getElementsByTagName("title");
                if (nodes != null)
                    textRight = nodes.item(0).getTextContent();
                
                nodes = element.getElementsByTagName("item");
                multiTextRight = new String[nodes.getLength() + 2];
                iconsRight = new ItemStack[nodes.getLength() + 1];
                
                for (int i = 0; i < nodes.getLength(); i++)
                {
                    NodeList children = nodes.item(i).getChildNodes();
                    multiTextRight[i+2] = children.item(1).getTextContent();
                    iconsRight[i+1] = TConstructClientRegistry.getManualIcon(children.item(3).getTextContent());
                }

                nodes = element.getElementsByTagName("text");
                if (nodes != null)
                {
                    multiTextRight[0] = nodes.item(0).getTextContent();
                    multiTextRight[1] = nodes.item(1).getTextContent();
                }
                
                nodes = element.getElementsByTagName("icon");
                if (nodes != null)
                    iconsRight[0] = TConstructClientRegistry.getManualIcon(nodes.item(0).getTextContent());
            }
        }
        else
        {
            pageRightType = "blank";
            textRight = null;
        }
    }

    public void drawScreen (int par1, int par2, float par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/bookright.png");
        int localWidth = (this.width) / 2;
        byte localHeight = 8;
        this.drawTexturedModalRect(localWidth, localHeight, 0, 0, this.bookImageWidth, this.bookImageHeight);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/bookleft.png");
        localWidth = localWidth - this.bookImageWidth;
        this.drawTexturedModalRect(localWidth, localHeight, 256 - this.bookImageWidth, 0, this.bookImageWidth, this.bookImageHeight);

        super.drawScreen(par1, par2, par3);

        //Workaround
        if (pageLeftType.equals("picture")) //TODO: OOP this
        {
            drawPicture(multiTextLeft[0], localWidth + 16, localHeight + 12);
        }
        if (pageRightType.equals("picture"))
        {
            drawPicture(multiTextRight[0], localWidth + 220, localHeight + 12);
        }

        if (pageLeftType.equals("crafting"))
        {
            if (multiTextLeft[0].equals("two"))
                drawCrafting(2, localWidth + 16, localHeight + 12);
            
            if (multiTextLeft[0].equals("three"))
                drawCrafting(3, localWidth + 22, localHeight + 12);
        }
        if (pageRightType.equals("crafting"))
        {
            int size = 2;
            if (multiTextRight[0].equals("three"))
                size = 3;
            drawCrafting(size, localWidth + 220, localHeight + 12);
        }
        if (pageLeftType.equals("smelting"))
        {
            drawSmelting(localWidth + 16, localHeight + 12);
        }
        if (pageRightType.equals("smelting"))
        {
            drawSmelting(localWidth + 220, localHeight + 12);
        }

        //Text
        if (pageLeftType.equals("text"))
        {
            if (textLeft != null)
                drawTextPage(textLeft, localWidth + 16, localHeight + 12);
        }
        else if (pageLeftType.equals("intro"))
        {
            if (textLeft != null)
                drawTitlePage(textLeft, localWidth + 16, localHeight + 12);
        }
        else if (pageLeftType.equals("contents"))
        {
            drawContentTablePage(textLeft, iconsLeft, multiTextLeft, localWidth + 16, localHeight + 12);
        }
        else if (pageLeftType.equals("sidebar"))
        {
            drawSidebarPage(textLeft, iconsLeft, multiTextLeft, localWidth + 16, localHeight + 12);
        }
        else if (pageLeftType.equals("picture"))
        {
            drawPicturePage(textLeft, localWidth + 16, localHeight + 12);
        }
        else if (pageLeftType.equals("crafting"))
        {
            if (multiTextLeft[0].equals("two"))
                drawCraftingPage(textLeft, iconsLeft, 2, localWidth + 16, localHeight + 12);
            if (multiTextLeft[0].equals("three"))
                drawCraftingPage(textLeft, iconsLeft, 3, localWidth + 22, localHeight + 12);
        }
        else if (pageLeftType.equals("smelting"))
        {
            drawSmeltingPage(textLeft, iconsLeft, localWidth + 16, localHeight + 12);
        }
        else if (pageLeftType.equals("materialstats"))
        {
            drawMaterialPage(textLeft, iconsLeft, multiTextLeft, materialLeft, localWidth + 16, localHeight + 12);
        }
        else if (pageLeftType.equals("toolpage"))
        {
            drawToolPage(textLeft, iconsLeft, multiTextLeft, localWidth + 16, localHeight + 12);
        }

        //Right
        if (pageRightType.equals("text"))
        {
            if (textRight != null)
                drawTextPage(textRight, localWidth + 220, localHeight + 12);
        }
        else if (pageRightType.equals("intro"))
        {
            if (textRight != null)
                drawTitlePage(textRight, localWidth + 220, localHeight + 12);
        }
        else if (pageRightType.equals("contents"))
        {
            drawContentTablePage(textRight, iconsRight, multiTextRight, localWidth + 220, localHeight + 12);
        }
        else if (pageRightType.equals("sidebar"))
        {
            drawSidebarPage(textRight, iconsRight, multiTextRight, localWidth + 220, localHeight + 12);
        }
        else if (pageRightType.equals("picture"))
        {
            drawPicturePage(textRight, localWidth + 220, localHeight + 12);
        }
        else if (pageRightType.equals("crafting"))
        {
            int size = 2;
            if (multiTextRight[0].equals("three"))
                size = 3;
            drawCraftingPage(textRight, iconsRight, size, localWidth + 220, localHeight + 12);
        }
        else if (pageRightType.equals("smelting"))
        {
            drawSmeltingPage(textRight, iconsRight, localWidth + 220, localHeight + 12);
        }
        else if (pageRightType.equals("materialstats"))
        {
            drawMaterialPage(textRight, iconsRight, multiTextRight, materialRight, localWidth + 220, localHeight + 12);
        }

        else if (pageRightType.equals("toolpage"))
        {
            drawToolPage(textRight, iconsRight, multiTextRight, localWidth + 220, localHeight + 12);
        }
    }

    /* Page types */
    public void drawTextPage (String text, int localWidth, int localHeight)
    {
        this.fontRenderer.drawSplitString(text, localWidth, localHeight, 178, 0);
    }

    public void drawTitlePage (String text, int localWidth, int localHeight)
    {
        this.fontRenderer.drawSplitString(text, localWidth, localHeight, 178, 0);
    }

    public void drawContentTablePage (String info, ItemStack[] icons, String[] multiText, int localWidth, int localHeight)
    {
        if (info != null)
            this.fontRenderer.drawString("\u00a7n" + info, localWidth + 50, localHeight + 4, 0);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        for (int i = 0; i < icons.length; i++)
        {
            renderitem.renderItemIntoGUI(fontRenderer, mc.renderEngine, icons[i], localWidth + 16, localHeight + 18 * i + 18);
            int yOffset = 18;
            if (multiText[i].length() > 40)
                yOffset = 13;
            this.fontRenderer.drawString(multiText[i], localWidth + 38, localHeight + 18 * i + yOffset, 0);
        }
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    public void drawSidebarPage (String info, ItemStack[] icons, String[] multiText, int localWidth, int localHeight)
    {
        this.fontRenderer.drawSplitString(info, localWidth, localHeight, 178, 0);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        for (int i = 0; i < icons.length; i++)
        {
            renderitem.renderItemIntoGUI(fontRenderer, mc.renderEngine, icons[i], localWidth + 8, localHeight + 18 * i + 36);
            int yOffset = 39;
            if (multiText[i].length() > 40)
                yOffset = 34;
            this.fontRenderer.drawSplitString(multiText[i], localWidth + 30, localHeight + 18 * i + yOffset, 140, 0);
        }
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    public void drawPicture (String picture, int localWidth, int localHeight)
    {
        this.mc.renderEngine.bindTexture(picture);
        this.drawTexturedModalRect(localWidth, localHeight + 12, 0, 0, 170, 144);
    }

    public void drawPicturePage (String info, int localWidth, int localHeight)
    {
        this.fontRenderer.drawSplitString(info, localWidth + 8, localHeight, 178, 0);
    }

    public void drawCrafting (int size, int localWidth, int localHeight)
    {
        this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/bookcrafting.png");
        if (size == 2)
            this.drawTexturedModalRect(localWidth + 8, localHeight + 46, 0, 116, 154, 78);
        if (size == 3)
            this.drawTexturedModalRect(localWidth - 8, localHeight + 28, 0, 0, 183, 114);
    }

    public void drawCraftingPage (String info, ItemStack[] icons, int recipeSize, int localWidth, int localHeight)
    {
        if (info != null)
            this.fontRenderer.drawString("\u00a7n" + info, localWidth + 50, localHeight + 4, 0);

        GL11.glScalef(2f, 2f, 2f);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        if (recipeSize == 2)
        {
            renderitem.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, icons[0], (localWidth + 126) / 2, (localHeight + 68) / 2);
            if (icons[0].stackSize > 1)
                renderitem.renderItemStack(fontRenderer, mc.renderEngine, icons[0], (localWidth + 126) / 2, (localHeight + 68) / 2, String.valueOf(icons[0].stackSize));
            for (int i = 0; i < icons.length - 1; i++)
            {
                if (icons[i + 1] != null)
                    renderitem.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, icons[i + 1], (localWidth + 14 + 36 * (i % 2)) / 2, (localHeight + 36 * (i / 2) + 52) / 2);
            }
        }
        
        if (recipeSize == 3)
        {
            renderitem.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, icons[0], (localWidth + 138) / 2, (localHeight + 70) / 2);
            if (icons[0].stackSize > 1)
                renderitem.renderItemStack(fontRenderer, mc.renderEngine, icons[0], (localWidth + 126) / 2, (localHeight + 68) / 2, String.valueOf(icons[0].stackSize));
            for (int i = 0; i < icons.length - 1; i++)
            {
                if (icons[i + 1] != null)
                    renderitem.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, icons[i + 1], (localWidth - 2 + 36 * (i % 3)) / 2, (localHeight + 36 * (i / 3) + 34) / 2);
            }
        }

        GL11.glScalef(0.5F, 0.5F, 0.5F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    public void drawSmelting (int localWidth, int localHeight)
    {
        this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/bookfurnace.png");
        this.drawTexturedModalRect(localWidth + 32, localHeight + 32, 0, 0, 111, 114);
    }

    public void drawSmeltingPage (String info, ItemStack[] icons, int localWidth, int localHeight)
    {
        if (info != null)
            this.fontRenderer.drawString("\u00a7n" + info, localWidth + 50, localHeight + 4, 0);

        GL11.glScalef(2f, 2f, 2f);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        renderitem.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, TConstructClientRegistry.getManualIcon("coal"), (localWidth + 38) / 2, (localHeight + 110) / 2);
        renderitem.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, icons[0], (localWidth + 106) / 2, (localHeight + 74) / 2);
        renderitem.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, icons[1], (localWidth + 38) / 2, (localHeight + 38) / 2);

        if (icons[0].stackSize > 1)
            renderitem.renderItemStack(fontRenderer, mc.renderEngine, icons[0], (localWidth + 106) / 2, (localHeight + 74) / 2, String.valueOf(icons[0].stackSize));

        GL11.glScalef(0.5F, 0.5F, 0.5F);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }
    
    public void drawMaterialPage (String title, ItemStack[] icons, String[] multiText, ToolMaterial material, int localWidth, int localHeight)
    {
        this.fontRenderer.drawString("\u00a7n" + title, localWidth + 70, localHeight + 4, 0);
        this.fontRenderer.drawSplitString(multiText[0], localWidth, localHeight + 16, 178, 0);
        
        this.fontRenderer.drawString("Material: ", localWidth+108, localHeight + 40, 0);
        this.fontRenderer.drawString("Shard: ", localWidth+108, localHeight + 72, 0);
        this.fontRenderer.drawString("Rod: ", localWidth+108, localHeight + 104, 0);
        
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        //renderitem.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, icons[0], localWidth + 50, localHeight + 0);
        renderitem.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, icons[1], localWidth + 108, localHeight + 50);
        renderitem.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, icons[2], localWidth + 108, localHeight + 82);
        renderitem.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, icons[3], localWidth + 108, localHeight + 114);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        this.fontRenderer.drawSplitString(icons[1].getTooltip(this.mc.thePlayer, false).get((0)).toString(), localWidth + 128, localHeight + 53, 52, 0);
        this.fontRenderer.drawSplitString(icons[2].getTooltip(this.mc.thePlayer, false).get((0)).toString(), localWidth + 128, localHeight + 85, 52, 0);
        this.fontRenderer.drawSplitString(icons[3].getTooltip(this.mc.thePlayer, false).get((0)).toString(), localWidth + 128, localHeight + 117, 52, 0);

        this.fontRenderer.drawString("Durability: " + material.durability(), localWidth, localHeight + 40, 0);
        this.fontRenderer.drawString("Handle Modifier: " + material.handleDurability()+"x", localWidth, localHeight + 50, 0);
        this.fontRenderer.drawString("Full Tool Durability: " + (int)(material.durability()*material.handleDurability()), localWidth, localHeight + 60, 0);
        
        this.fontRenderer.drawString("Mining Speed: " + material.toolSpeed()/100f, localWidth, localHeight + 80, 0);
        this.fontRenderer.drawString("Harvest Level: " + material.harvestLevel()+" ("+PartCrafterGui.getHarvestLevelName(material.harvestLevel())+")", localWidth, localHeight + 90, 0);
        int attack = material.attack();
        String heart = attack == 2 ? " Heart" : " Hearts";
        if (attack % 2 == 0)
            this.fontRenderer.drawString("Base Attack: " + material.attack()/2 + heart, localWidth, localHeight + 100, 0);
        else
            this.fontRenderer.drawString("Base Attack: " + material.attack()/2f + heart, localWidth, localHeight + 100, 0);
        
        int offset = 0;
        String ability = material.ability();
        if (!ability.equals(""))
        {
            this.fontRenderer.drawString("Material ability: " + material.ability(), localWidth, localHeight + 120 + 10*offset, 0);
            offset++;
            if (ability.equals("Writable"))
                this.fontRenderer.drawString("+1 Modifiers", localWidth, localHeight + 120 + 10*offset, 0);
        }
        
        if (material.reinforced() > 0)
        {
            this.fontRenderer.drawString("Material ability: Reinforced", localWidth, localHeight + 120 + 10*offset, 0);
            offset++;
            this.fontRenderer.drawString("Reinforced level: " + material.reinforced(), localWidth, localHeight + 120 + 10*offset, 0);
            offset++;
        }       
        
        if (material.shoddy() > 0)
        {
            this.fontRenderer.drawString("Shoddy level: " + material.shoddy(), localWidth, localHeight + 120 + 10*offset, 0);
            offset++;
        }
        else if (material.shoddy() < 0)
        {
            this.fontRenderer.drawString("Spiny level: " + -material.shoddy(), localWidth, localHeight + 120 + 10*offset, 0);
            offset++;
        } 
    }
    
    public void drawToolPage (String title, ItemStack[] icons, String[] multiText, int localWidth, int localHeight)
    {
        this.fontRenderer.drawString("\u00a7n" + title, localWidth + 70, localHeight + 4, 0);
        this.fontRenderer.drawSplitString(multiText[0], localWidth, localHeight + 16, 178, 0);
        int size = multiText[0].length()/50;
        this.fontRenderer.drawSplitString(multiText[1], localWidth, localHeight + 28 + 10*size, 118, 0);
        
        this.fontRenderer.drawString("Crafting Parts: ", localWidth + 124, localHeight + 28 + 10*size, 0);
        
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        renderitem.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, icons[0], localWidth + 50, localHeight + 0);
        for (int i = 1; i < icons.length; i++)
        {
            renderitem.renderItemAndEffectIntoGUI(fontRenderer, mc.renderEngine, icons[i], localWidth + 120, localHeight + 20 + 10*size + 18*i);
            this.fontRenderer.drawSplitString(multiText[i+1], localWidth + 140, localHeight + 24 + 10*size + 18*i, 42, 0);
        }
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }
}
