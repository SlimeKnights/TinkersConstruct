package tinker.tconstruct.client.gui;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.liquids.LiquidStack;

import org.lwjgl.opengl.GL11;

import tinker.common.BlockSkinRenderHelper;
import tinker.tconstruct.logic.SmelteryLogic;

public class SmelteryGui extends GuiContainer
{
	public SmelteryLogic logic;

	public SmelteryGui(InventoryPlayer inventoryplayer, SmelteryLogic smeltery, World world, int x, int y, int z)
	{
		super(smeltery.getGuiContainer(inventoryplayer, world, x, y, z));
		logic = smeltery;
	}

	protected void drawGuiContainerForegroundLayer (int par1, int par2)
	{
		fontRenderer.drawString(StatCollector.translateToLocal("crafters.Smeltery"), 60, 5, 0x404040);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
		drawStats();
	}

	void drawStats ()
	{
		fontRenderer.drawString("Temp: " + logic.getInternalTemperature(), xSize + 6, 6, 0xffffff);
		fontRenderer.drawString("Liquid: " + logic.getCapacity(), xSize + 6, 16, 0xffffff);
		for (int iter = 0; iter < 9; iter++)
			fontRenderer.drawString("Slot "+iter+" temp: " + logic.getTempForSlot(iter), xSize + 6, 26+iter*10, 0xffffff);
		/*for (int iter = 0; iter < 9; iter++)
			fontRenderer.drawString("Slot "+iter+" mTemp: " + logic.meltingTemps[iter], xSize + 6, 100+iter*9, 0xffffff);*/
	}

	protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
	{
		int texID = mc.renderEngine.getTexture("/tinkertextures/gui/smeltery.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(texID);
		int cornerX = (width - xSize) / 2;
		int cornerY = (height - ySize) / 2;
		drawTexturedModalRect(cornerX, cornerY, 0, 0, xSize, ySize);
		
		//Fuel - Lava
		if (logic.fuelGague > 0)
		{
			ForgeHooksClient.bindTexture(Block.lavaStill.getTextureFile(), 0);
			int renderIndex = Block.lavaStill.getBlockTextureFromSideAndMetadata(0, 0);
			int xTex = renderIndex % 16 * 16;
			int yTex = renderIndex / 16 * 16;
			int fuel = logic.getScaledFuelGague(52);
			int count = 0;
			while (fuel > 0)
			{
				int size = fuel >= 16 ? 16 : fuel;
				fuel -= size;
				drawTexturedModalRect(cornerX + 146, (cornerY + 67) - size - 16*count, xTex, yTex+16-size, 9, size);
				count++;
			}
		}
		
		//Liquids - molten metal
		int base = 0;
		for (LiquidStack liquid : logic.moltenMetal)
		{
			int renderIndex;
			if (liquid.itemID < 4096) //Block
			{
				Block liquidBlock = Block.blocksList[liquid.itemID];
				ForgeHooksClient.bindTexture(liquidBlock.getTextureFile(), 0);
				renderIndex = liquidBlock.getBlockTextureFromSideAndMetadata(0, liquid.itemMeta);
			}
			else //Item
			{
				Item liquidItem = Item.itemsList[liquid.itemID];
				ForgeHooksClient.bindTexture(liquidItem.getTextureFile(), 0);
				renderIndex = liquidItem.getIconFromDamage(liquid.itemMeta);
			}

			int xTex = renderIndex % 16 * 16;
			int yTex = renderIndex / 16 * 16;
			int liquidSize = liquid.amount * 52 / logic.getCapacity();
			while (liquidSize > 0)
			{
				int size = liquidSize >= 16 ? 16 : liquidSize;
				drawTexturedModalRect(cornerX + 13, (cornerY + 68) - size - base, xTex, yTex+16-size, 16, size);
				drawTexturedModalRect(cornerX + 29, (cornerY + 68) - size - base, xTex, yTex+16-size, 16, size);
				drawTexturedModalRect(cornerX + 45, (cornerY + 68) - size - base, xTex, yTex+16-size, 2, size);
				liquidSize -= size;
				base += size;
			}
			//base = liquid.amount / 10000 * 52;
		}

		// Draw description - don't use this 
		texID = this.mc.renderEngine.getTexture("/tinkertextures/gui/description.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(texID);
		cornerX = (this.width + this.xSize) / 2;
		cornerY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);
	}
}
