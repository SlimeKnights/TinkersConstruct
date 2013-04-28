package mods.tinker.tconstruct.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import mods.tinker.tconstruct.blocks.logic.PatternShaperLogic;
import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.inventory.PatternShaperContainer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;

public class PatternShaperGui extends GuiContainer
{
	PatternShaperLogic logic;
	int patternIndex;

	public PatternShaperGui(InventoryPlayer inventoryplayer, PatternShaperLogic shaper, World world, int x, int y, int z)
	{
		super(new PatternShaperContainer(inventoryplayer, shaper));
		logic = shaper;
		patternIndex = 0;
	}

	public void onGuiClosed ()
	{
		super.onGuiClosed();
	}

	@Override
	protected void drawGuiContainerForegroundLayer (int par1, int par2)
	{
		fontRenderer.drawString(StatCollector.translateToLocal("crafters.PatternShaper"), 50, 6, 0x404040);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
	}

	protected void drawGuiContainerBackgroundLayer (float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/patternshaper.png");
		int cornerX = (this.width - this.xSize) / 2;
		int cornerY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);
		if (!logic.isStackInSlot(0))
		{
			this.drawTexturedModalRect(cornerX + 47, cornerY + 34, 176, 0, 18, 18);
		}
	}

	public void initGui ()
	{
		super.initGui();
		int cornerX = (this.width - this.xSize) / 2;
		int cornerY = (this.height - this.ySize) / 2;

		this.buttonList.clear();
		/*ToolGuiElement repair = TConstruct.toolButtons.get(0);
		GuiButtonTool repairButton = new GuiButtonTool(0, cornerX - 110, cornerY, repair.buttonIconX, repair.buttonIconY, repair.texture); // Repair
		repairButton.enabled = false;
		this.buttonList.add(repairButton);*/
		this.buttonList.add(new GuiButton(0, cornerX - 120, cornerY, 120, 20, "Next Pattern"));
		this.buttonList.add(new GuiButton(1, cornerX - 120, cornerY + 20, 120, 20, "Previous Pattern"));

		//for (int iter = 0; iter < TConstructContent.patternOutputs.length; iter++)
		//{

		/*ToolGuiElement element = TConstruct.toolButtons.get(iter);
		GuiButtonTool button = new GuiButtonTool(iter, cornerX - 110 + 22 * (iter % 5), cornerY + 22 * (iter / 5), element.buttonIconX, element.buttonIconY, element.texture); // Repair
		this.buttonList.add(button);*/
		//}
	}

	protected void actionPerformed (GuiButton button)
	{
		ItemStack pattern = logic.getStackInSlot(0);
		if (pattern != null && pattern.getItem() == TContent.blankPattern)
		{
			int meta = pattern.getItemDamage();
			if (meta == 0)
			{
				if (button.id == 0)
				{
					patternIndex++;
					if (patternIndex > TContent.patternOutputs.length-1)
						patternIndex = 0;
				}
				else if (button.id == 1)
				{
					patternIndex--;
					if (patternIndex < 0)
						patternIndex = TContent.patternOutputs.length-1;
				}
				ItemStack stack = new ItemStack(TContent.woodPattern, 1, patternIndex + 1);
				logic.setInventorySlotContents(1, stack);
				updateServer(stack);
			}
			else if (meta == 1)
			{
				if (button.id == 0)
				{
					patternIndex++;
					if (patternIndex > TContent.patternOutputs.length)
						patternIndex = 0;
				}
				else if (button.id == 1)
				{
					patternIndex--;
					if (patternIndex < 0)
						patternIndex = TContent.patternOutputs.length;
				}
				ItemStack stack = new ItemStack(TContent.metalPattern, 1, patternIndex);
				logic.setInventorySlotContents(1, stack);
				updateServer(stack);
			}
		}
	}

	void updateServer (ItemStack stack)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try
		{
			outputStream.writeByte(2);
			outputStream.writeInt(logic.worldObj.provider.dimensionId);
			outputStream.writeInt(logic.xCoord);
			outputStream.writeInt(logic.yCoord);
			outputStream.writeInt(logic.zCoord);
			outputStream.writeShort(stack.itemID);
			outputStream.writeShort(stack.getItemDamage());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "TConstruct";
		packet.data = bos.toByteArray();
		packet.length = bos.size();

		PacketDispatcher.sendPacketToServer(packet);
	}
}
