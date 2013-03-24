package mods.tinker.tconstruct.client.gui;

import mods.tinker.tconstruct.client.TProxyClient;
import mods.tinker.tconstruct.library.TConstructClientRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiParticle;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
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

	String pageRightType;
	String textRight;
	ItemStack[] iconsRight;
	String[] multiTextRight;

	public GuiManual(ItemStack stack, Document doc)
	{
		this.mc = Minecraft.getMinecraft();
		this.itemstackBook = stack;
		currentPage = 0; //Stack page
		manual = doc;
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
		if (pageLeftType.equals("picture"))
		{
			drawPicture(multiTextLeft[0], localWidth + 16, localHeight + 12);
		}
		if (pageRightType.equals("picture"))
		{
			drawPicture(multiTextRight[0], localWidth + 220, localHeight + 12);
		}

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
			this.fontRenderer.drawString("\u00a7n"+info, localWidth + 50, localHeight + 4, 0);
		for (int i = 0; i < icons.length; i++)
		{
			renderitem.renderItemIntoGUI(fontRenderer, mc.renderEngine, icons[i], localWidth + 16, localHeight + 18 * i + 18);
			int yOffset = 18;
			if (multiText[i].length() > 40)
				yOffset = 13;
			this.fontRenderer.drawString(multiText[i], localWidth + 38, localHeight + 18 * i + yOffset, 0);
		}
	}
	
	public void drawSidebarPage (String info, ItemStack[] icons, String[] multiText, int localWidth, int localHeight)
	{
		this.fontRenderer.drawSplitString(info, localWidth, localHeight, 178, 0);
		for (int i = 0; i < icons.length; i++)
		{
			renderitem.renderItemIntoGUI(fontRenderer, mc.renderEngine, icons[i], localWidth + 8, localHeight + 18 * i + 36);
			int yOffset = 39;
			if (multiText[i].length() > 40)
				yOffset = 34;
			this.fontRenderer.drawSplitString(multiText[i], localWidth + 30, localHeight + 18 * i + yOffset, 140, 0);
		}
	}
	
	public void drawPicture(String picture, int localWidth, int localHeight)
	{
		this.mc.renderEngine.bindTexture(picture);
		this.drawTexturedModalRect(localWidth, localHeight+12, 0, 0, 170, 144);
	}
	
	public void drawPicturePage(String info, int localWidth, int localHeight)
	{
		this.fontRenderer.drawSplitString(info, localWidth+8, localHeight, 178, 0);
	}
}
