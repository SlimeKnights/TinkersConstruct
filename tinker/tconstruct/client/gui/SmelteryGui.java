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
		fontRenderer.drawString(StatCollector.translateToLocal("crafters.Smeltery"), 50, 5, 0x404040);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 50, (ySize - 96) + 2, 0x404040);
	}
	
	protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
	{
		int texID = mc.renderEngine.getTexture("/tinkertextures/gui/smeltery.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(texID);
		int cornerX = (width - xSize) / 2;
		int cornerY = (height - ySize) / 2;
		drawTexturedModalRect(cornerX+46, cornerY, 0, 0, xSize, ySize);

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
				drawTexturedModalRect(cornerX + 117, (cornerY + 68) - size - 16 * count, xTex, yTex + 16 - size, 12, size);
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
			else
			//Item
			{
				Item liquidItem = Item.itemsList[liquid.itemID];
				ForgeHooksClient.bindTexture(liquidItem.getTextureFile(), 0);
				renderIndex = liquidItem.getIconFromDamage(liquid.itemMeta);
			}

			int xTex = renderIndex % 16 * 16;
			int yTex = renderIndex / 16 * 16;
			if (logic.getCapacity() > 0)
			{
				int liquidSize = liquid.amount * 52 / logic.getCapacity();
				while (liquidSize > 0)
				{
					int size = liquidSize >= 16 ? 16 : liquidSize;
					int basePos = 54;
					drawTexturedModalRect(cornerX + basePos,    (cornerY + 68) - size - base, xTex, yTex + 16 - size, 16, size);
					drawTexturedModalRect(cornerX + basePos+16, (cornerY + 68) - size - base, xTex, yTex + 16 - size, 16, size);
					drawTexturedModalRect(cornerX + basePos+32, (cornerY + 68) - size - base, xTex, yTex + 16 - size, 16, size);
					drawTexturedModalRect(cornerX + basePos+48, (cornerY + 68) - size - base, xTex, yTex + 16 - size, 4, size);
					liquidSize -= size;
					base += size;
				}
			}
			//base = liquid.amount / 10000 * 52;
		}
		
		//Liquid gague
		texID = this.mc.renderEngine.getTexture("/tinkertextures/gui/smeltery.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(texID);
		drawTexturedModalRect(cornerX+54, cornerY+16, xSize, 76, 52, 52);
		//drawTexturedModalRect(cornerX+111, cornerY+16, xSize, 128, 52, 52);
		
		//Side inventory
		texID = this.mc.renderEngine.getTexture("/tinkertextures/gui/smelteryside.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(texID);
		drawTexturedModalRect(cornerX-46, cornerY, 0, 0, 98, ySize-8);
		drawTexturedModalRect(cornerX+32, cornerY+8, 98, 0, 12, 15);
		
		//drawStats();
		int slotSize = logic.layers * 9;
		if (slotSize > 24)
			slotSize = 24;
		for (int iter = 0; iter < slotSize; iter++)
		{
			int slotTemp = logic.getTempForSlot(iter) - 20;
			int maxTemp = logic.getMeltingPointForSlot(iter) - 20;
			if (slotTemp > 0 && maxTemp > 0)
			{				
				int size = 16 * slotTemp / maxTemp + 1;
				drawTexturedModalRect(cornerX-38+(iter%3*22), cornerY+8+(iter/3*18) + 16 - size, 98, 15 + 16 - size, 5, size);
			}
		}
	}
}
