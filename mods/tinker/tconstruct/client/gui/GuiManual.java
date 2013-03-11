package mods.tinker.tconstruct.client.gui;

import mods.tinker.tconstruct.client.SmallFontRenderer;
import mods.tinker.tconstruct.client.TProxyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiParticle;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
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
    int bookImageWidth = 166;
    int bookImageHeight = 200;
    int bookTotalPages = 1;
    int currentPage;
    int maxPages;

    private TurnPageButton buttonNextPage;
    private TurnPageButton buttonPreviousPage;
    String pageLeftType;
    String pageRightType;
    String textLeft;
    String textRight;
    
    public GuiManual(ItemStack stack, Document doc)
    {
        this.itemstackBook = stack;
        currentPage = 0; //Stack page
        manual = doc;
    }
    
    @Override
    public void setWorldAndResolution(Minecraft minecraft, int w, int h)
    {
        this.guiParticles = new GuiParticle(minecraft);
        this.mc = minecraft;
        this.fontRenderer = TProxyClient.smallFontRenderer;
        this.width = w;
        this.height = h;
        this.buttonList.clear();
        this.initGui();
        

    	int scale = 0;
    	
    	while (width / (scale + 1) >= 160)
        {
            scale++;
        }
    	
    	SmallFontRenderer.guiScale = scale;
    }
    
    public void initGui()
    {
    	maxPages = manual.getElementsByTagName("page").getLength();
    	updateText();
    	int xPos = (this.width) / 2;
        this.buttonList.add(this.buttonNextPage = new TurnPageButton(1, xPos+bookImageWidth-50, 180, true));
        this.buttonList.add(this.buttonPreviousPage = new TurnPageButton(2, xPos-bookImageWidth+24, 180, false));
    }
    
    protected void actionPerformed(GuiButton button)
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
    
    void updateText()
    {
        if (currentPage >= maxPages)
        	currentPage = maxPages-2;
        if (currentPage % 2 == 1)
        	currentPage--;
        if (currentPage < 0)
        	currentPage = 0;
        
    	NodeList nList = manual.getElementsByTagName("page");
    	
    	Node node = nList.item(currentPage);
		if (node.getNodeType() == Node.ELEMENT_NODE)
		{
			Element element = (Element) node;
			//System.out.println("TypeL: "+eElement.getAttribute("type"));
			pageLeftType = element.getAttribute("type");
			NodeList nodes = element.getElementsByTagName("text");
			if (nodes != null)
				textLeft = nodes.item(0).getTextContent();
			//textLeft = element.getElementsByTagName("text").item(0).getTextContent();
		}
		
		node = nList.item(currentPage+1);
		if (node != null && node.getNodeType() == Node.ELEMENT_NODE)
		{
			Element element = (Element) node;
			pageRightType = element.getAttribute("type");
			NodeList nodes = element.getElementsByTagName("text");
			if (nodes != null)
				textRight = nodes.item(0).getTextContent();
		}
		else
		{
			pageRightType = "blank";
			textRight = null;
		}
    }

    public void drawScreen(int par1, int par2, float par3)
    {
    	
        //int texID = this.mc.renderEngine.getTexture();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.func_98187_b("/tinkertextures/gui/bookright.png");
        int localWidth = (this.width) / 2;
        byte localHeight = 8;
        this.drawTexturedModalRect(localWidth, localHeight, 0, 0, this.bookImageWidth, this.bookImageHeight);
        
        //texID = this.mc.renderEngine.getTexture("/tinkertextures/gui/bookleft.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //this.mc.renderEngine.bindTexture(texID);
        this.mc.renderEngine.func_98187_b("/tinkertextures/gui/bookleft.png");
        localWidth = localWidth - this.bookImageWidth;
        this.drawTexturedModalRect(localWidth, localHeight, 256 - this.bookImageWidth, 0, this.bookImageWidth, this.bookImageHeight);

        if (textLeft != null)
        {
        	if (pageLeftType.equals("text"))
        		drawTextPage(textLeft, localWidth + 80, localHeight + 32);
        	else if (pageLeftType.equals("intro"))
        		drawTitlePage(textLeft, localWidth + 80, localHeight + 32); 
        }
        if (textRight != null)
        	drawTextPage(textRight, localWidth + 320, localHeight + 32);
        super.drawScreen(par1, par2, par3);
    }
    
    public void drawTextPage(String text, int localWidth, int localHeight)
    {
    	this.fontRenderer.drawSplitString(text, localWidth, localHeight, 200, 0);
    }
    
    public void drawTitlePage(String text, int localWidth, int localHeight)
    {
    	this.fontRenderer.drawSplitString(text, localWidth, localHeight, 200, 0);
    }
}
