package tinker.tconstruct.client.gui;

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

import tinker.tconstruct.client.SmallFontRenderer;
import tinker.tconstruct.client.TProxyClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiManual extends GuiScreen
{
    ItemStack itemstackBook;
    int bookImageWidth = 166;
    int bookImageHeight = 200;
    int bookTotalPages = 1;
    int currentPage;
    int maxPages;

    private TurnPageButton buttonNextPage;
    private TurnPageButton buttonPreviousPage;
    String textLeft;
    String textRight;
    
    public GuiManual(ItemStack stack)
    {
        this.itemstackBook = stack;
        currentPage = 0; //Stack page
    }
    
    @Override
    public void setWorldAndResolution(Minecraft minecraft, int w, int h)
    {
        this.guiParticles = new GuiParticle(minecraft);
        this.mc = minecraft;
        this.fontRenderer = TProxyClient.smallFontRenderer;
        this.width = w;
        this.height = h;
        this.controlList.clear();
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
    	maxPages = TProxyClient.volume1.getElementsByTagName("page").getLength();
    	updateText();
    	int xPos = (this.width) / 2;
        this.controlList.add(this.buttonNextPage = new TurnPageButton(1, xPos+bookImageWidth-50, 180, true));
        this.controlList.add(this.buttonPreviousPage = new TurnPageButton(2, xPos-bookImageWidth+24, 180, false));
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
        if (currentPage < 0)
        	currentPage = 0;
        if (currentPage >= maxPages)
        	currentPage = maxPages-2;
        if (currentPage % 2 == 1)
        	currentPage--;
        
    	//Document doc = TProxyClient.volume1;
    	NodeList nList = TProxyClient.volume1.getElementsByTagName("page");
    	
    	Node nNode = nList.item(currentPage);
		if (nNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element eElement = (Element) nNode;
			textLeft = eElement.getElementsByTagName("text").item(0).getTextContent();
		}
		
		nNode = nList.item(currentPage+1);
		if (nNode != null && nNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element eElement = (Element) nNode;
			textRight = eElement.getElementsByTagName("text").item(0).getTextContent();
		}
		else
			textRight = null;
    }

    public void drawScreen(int par1, int par2, float par3)
    {
    	
        int texID = this.mc.renderEngine.getTexture("/tinkertextures/gui/bookright.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texID);
        int var5 = (this.width) / 2;
        byte var6 = 8;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.bookImageWidth, this.bookImageHeight);
        
        texID = this.mc.renderEngine.getTexture("/tinkertextures/gui/bookleft.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texID);
        var5 = var5 - this.bookImageWidth;
        this.drawTexturedModalRect(var5, var6, 256 - this.bookImageWidth, 0, this.bookImageWidth, this.bookImageHeight);

        if (textLeft != null)
        	this.fontRenderer.drawSplitString(textLeft, (var5 + 80), var6 + 16 + 16, 200, 0);
        if (textRight != null)
        	this.fontRenderer.drawSplitString(textRight, (var5 + 320), var6 + 16 + 16, 200, 0);
        super.drawScreen(par1, par2, par3);
    }
}
