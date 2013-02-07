package tinker.tconstruct.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

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
		for (int iter = 0; iter < 9; iter++)
			fontRenderer.drawString("Slot "+iter+" temp: " + logic.getTempForSlot(iter), xSize + 6, 15+iter*9, 0xffffff);
		for (int iter = 0; iter < 9; iter++)
			fontRenderer.drawString("Slot "+iter+" mTemp: " + logic.meltingTemps[iter], xSize + 6, 100+iter*9, 0xffffff);
	}

	protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
	{
		int texID = mc.renderEngine.getTexture("/tinkertextures/gui/smeltery.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(texID);
		int cornerX = (width - xSize) / 2;
		int cornerY = (height - ySize) / 2;
		drawTexturedModalRect(cornerX, cornerY, 0, 0, xSize, ySize);
		if (logic.fuelGague > 0)
		{
			int fuel = logic.getScaledFuelGague(52);
			drawTexturedModalRect(cornerX + 146, (cornerY + 67) - fuel, 176, 52 - fuel, 14, fuel + 2);
		}

		// Draw description
		texID = this.mc.renderEngine.getTexture("/tinkertextures/gui/description.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(texID);
		cornerX = (this.width + this.xSize) / 2;
		cornerY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);
	}
}
