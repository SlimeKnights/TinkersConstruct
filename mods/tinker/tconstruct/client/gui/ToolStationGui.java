package mods.tinker.tconstruct.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import mods.tinker.tconstruct.library.client.ToolGuiElement;
import mods.tinker.tconstruct.container.ToolStationContainer;
import mods.tinker.tconstruct.library.ToolCore;
import mods.tinker.tconstruct.library.Weapon;
import mods.tinker.tconstruct.library.client.TConstructClientRegistry;
import mods.tinker.tconstruct.logic.ToolStationLogic;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ToolStationGui extends GuiContainer
{
	ToolStationLogic logic;
	ToolStationContainer toolSlots;
	GuiTextField text;
	String toolName;
	int guiType;
	int[] slotX, slotY, iconX, iconY;
	String title, body = "";

	public ToolStationGui(InventoryPlayer inventoryplayer, ToolStationLogic stationlogic, World world, int x, int y, int z)
	{
		super(stationlogic.getGuiContainer(inventoryplayer, world, x, y, z));
		this.logic = stationlogic;
		toolSlots = (ToolStationContainer) inventorySlots;
		text = new GuiTextField(this.fontRenderer, this.xSize / 2 - 5, 8, 30, 12);
		this.text.setMaxStringLength(40);
		this.text.setEnableBackgroundDrawing(false);
		this.text.setVisible(true);
		this.text.setCanLoseFocus(false);
		this.text.setFocused(true);
		this.text.setTextColor(0xffffff);
		toolName = "";
		resetGui();
		Keyboard.enableRepeatEvents(true);
	}

	void resetGui ()
	{
		this.text.setText("");
		guiType = 0;
		setSlotType(0);
		iconX = new int[] { 0, 1, 2 };
		iconY = new int[] { 13, 13, 13 };
		title = "\u00A7nRepair and Modification";
		body = "The main way to repair or change your tools. Place a tool and a material on the left to get started.";
	}

	public void initGui ()
	{
		super.initGui();
		int cornerX = (this.width - this.xSize) / 2;
		int cornerY = (this.height - this.ySize) / 2;

		this.buttonList.clear();
		ToolGuiElement repair = TConstructClientRegistry.toolButtons.get(0);
		GuiButtonTool repairButton = new GuiButtonTool(0, cornerX - 110, cornerY, repair.buttonIconX, repair.buttonIconY, repair.texture); // Repair
		repairButton.enabled = false;
		this.buttonList.add(repairButton);

		for (int iter = 1; iter < TConstructClientRegistry.toolButtons.size(); iter++)
		{
			ToolGuiElement element = TConstructClientRegistry.toolButtons.get(iter);
			GuiButtonTool button = new GuiButtonTool(iter, cornerX - 110 + 22 * (iter % 5), cornerY + 22 * (iter / 5), element.buttonIconX, element.buttonIconY, element.texture); // Repair
			this.buttonList.add(button);
		}
	}

	protected void actionPerformed (GuiButton button)
	{
		((GuiButton) this.buttonList.get(guiType)).enabled = true;
		guiType = button.id;
		button.enabled = false;

		ToolGuiElement element = TConstructClientRegistry.toolButtons.get(guiType);
		setSlotType(element.slotType);
		iconX = element.iconsX;
		iconY = element.iconsY;
		title = "\u00A7n" + element.title;
		body = element.body;
	}

	void setSlotType (int type)
	{
		switch (type)
		{
		case 0:
			slotX = new int[] { 56, 38, 38 }; // Repair
			slotY = new int[] { 37, 28, 46 };
			break;
		case 1:
			slotX = new int[] { 56, 56, 56 }; // Three parts
			slotY = new int[] { 19, 55, 37 };
			break;
		case 2:
			slotX = new int[] { 56, 56, 14 }; // Two parts
			slotY = new int[] { 28, 46, 37 };
			break;
		case 3:
			slotX = new int[] { 38, 47, 56 }; // Double head
			slotY = new int[] { 28, 46, 28 };
			break;
		case 4:
			slotX = new int[] { 47, 47, 38, 56 }; // Four parts
			slotY = new int[] { 19, 55, 37, 37 };
			break;
		}
		toolSlots.resetSlots(slotX, slotY);
	}

	public void updateScreen ()
	{
		super.updateScreen();
		this.text.updateCursorCounter();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer (int par1, int par2)
	{
		this.fontRenderer.drawString(StatCollector.translateToLocal("crafters.ToolStation"), 6, 8, 0x000000);
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x000000);
		this.fontRenderer.drawString(toolName + "_", this.xSize / 2 - 18, 8, 0xffffff);

		if (logic.isStackInSlot(0))
			drawToolStats();
		else
			drawToolInformation();
	}

	void drawToolStats ()
	{
		ItemStack stack = logic.getStackInSlot(0);
		ToolCore tool = (ToolCore) stack.getItem();
		NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
		this.drawCenteredString(fontRenderer, "\u00A7n" + tool.getToolName(), xSize + 63, 8, 0xffffff);
		if (tool instanceof Weapon)
			drawWeaponStats(stack, tool, tags);
		else if (tool.getHeadType() == 3)
			drawDualStats(stack, tool, tags);
		else
			drawHarvestStats(stack, tool, tags);
	}

	void drawWeaponStats (ItemStack stack, ToolCore tool, NBTTagCompound tags)
	{
		int dur = tags.getInteger("Damage");
		int maxDur = tags.getInteger("TotalDurability");
		dur = maxDur - dur;
		fontRenderer.drawString("Durability: " + dur + "/" + maxDur, xSize + 8, 24, 0xffffff);
		int damage = tags.getInteger("Attack");
		fontRenderer.drawString("Damage: " + damage, xSize + 8, 35, 0xffffff);

		fontRenderer.drawString("Modifiers remaining: " + tags.getInteger("Modifiers"), xSize + 8, 57, 0xffffff);
		if (tags.hasKey("Tooltip1"))
			fontRenderer.drawString("Modifiers:", xSize + 8, 68, 0xffffff);

		boolean displayToolTips = true;
		int tipNum = 0;
		while (displayToolTips)
		{
			tipNum++;
			String tooltip = "ModifierTip" + tipNum;
			if (tags.hasKey(tooltip))
			{
				String tipName = tags.getString(tooltip);
				fontRenderer.drawString("- " + tipName, xSize + 8, 68 + tipNum * 11, 0xffffff);
			}
			else
				displayToolTips = false;
		}
	}

	void drawHarvestStats (ItemStack stack, ToolCore tool, NBTTagCompound tags)
	{
		int dur = tags.getInteger("Damage");
		int maxDur = tags.getInteger("TotalDurability");
		dur = maxDur - dur;
		fontRenderer.drawString("Durability: " + dur + "/" + maxDur, xSize + 8, 24, 0xffffff);
		int damage = tags.getInteger("Attack");
		fontRenderer.drawString("Damage: " + damage, xSize + 8, 35, 0xffffff);
		float mineSpeed = tags.getInteger("MiningSpeed") / 100f;
		fontRenderer.drawString("Mining Speed: " + mineSpeed, xSize + 8, 46, 0xffffff);
		fontRenderer.drawString("Mining Level: " + getHarvestLevelName(tags.getInteger("HarvestLevel")), xSize + 8, 57, 0xffffff);

		fontRenderer.drawString("Modifiers remaining: " + tags.getInteger("Modifiers"), xSize + 8, 79, 0xffffff);
		if (tags.hasKey("Tooltip1"))
			fontRenderer.drawString("Modifiers:", xSize + 8, 90, 0xffffff);

		boolean displayToolTips = true;
		int tipNum = 0;
		while (displayToolTips)
		{
			tipNum++;
			String tooltip = "ModifierTip" + tipNum;
			if (tags.hasKey(tooltip))
			{
				String tipName = tags.getString(tooltip);
				fontRenderer.drawString("- " + tipName, xSize + 8, 90 + tipNum * 11, 0xffffff);
			}
			else
				displayToolTips = false;
		}
	}

	void drawDualStats (ItemStack stack, ToolCore tool, NBTTagCompound tags)
	{
		int dur = tags.getInteger("Damage");
		int maxDur = tags.getInteger("TotalDurability");
		dur = maxDur - dur;
		fontRenderer.drawString("Durability: " + dur + "/" + maxDur, xSize + 8, 24, 0xffffff);
		float mineSpeed = tags.getInteger("MiningSpeed") / 100f;
		float mineSpeed2 = tags.getInteger("MiningSpeed2") / 100f;
		fontRenderer.drawString("Mining Speeds: ", xSize + 8, 35, 0xffffff);
		fontRenderer.drawString("- " + mineSpeed + ", " + mineSpeed2, xSize + 8, 46, 0xffffff);
		fontRenderer.drawString("Harvest Levels:", xSize + 8, 57, 0xffffff);
		fontRenderer.drawString("- " + getHarvestLevelName(tags.getInteger("HarvestLevel")), xSize + 8, 68, 0xffffff);
		fontRenderer.drawString("- " + getHarvestLevelName(tags.getInteger("HarvestLevel2")), xSize + 8, 79, 0xffffff);

		fontRenderer.drawString("Modifiers remaining: " + tags.getInteger("Modifiers"), xSize + 8, 90, 0xffffff);
		if (tags.hasKey("Tooltip1"))
			fontRenderer.drawString("Modifiers:", xSize + 8, 101, 0xffffff);

		boolean displayToolTips = true;
		int tipNum = 0;
		while (displayToolTips)
		{
			tipNum++;
			String tooltip = "ModifierTip" + tipNum;
			if (tags.hasKey(tooltip))
			{
				String tipName = tags.getString(tooltip);
				fontRenderer.drawString("- " + tipName, xSize + 8, 101 + tipNum * 11, 0xffffff);
			}
			else
				displayToolTips = false;
		}
	}

	void drawToolInformation ()
	{
		this.drawCenteredString(fontRenderer, title, xSize + 63, 8, 0xffffff);
		fontRenderer.drawSplitString(body, xSize + 8, 24, 115, 0xffffff);
	}

	String getHarvestLevelName (int num)
	{
		switch (num)
		{
		case 0:
			return "Stone";
		case 1:
			return "Iron";
		case 2:
			return "Redstone";
		case 3:
			return "Obsidian"; //Mithril
		case 4:
			return "Vulcanite";
		case 5:
			return "Adamantine";
		default:
			return String.valueOf(num);
		}
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	protected void drawGuiContainerBackgroundLayer (float par1, int par2, int par3)
	{
		// Draw the background
		//int texID = this.mc.renderEngine.getTexture("/mods/tinker/textures/gui/toolstation.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/toolstation.png");
		int cornerX = (this.width - this.xSize) / 2;
		int cornerY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);

		//texID = this.mc.renderEngine.getTexture("/mods/tinker/textures/gui/icons.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		//this.mc.renderEngine.bindTexture(texID);
		this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/icons.png");
		// Draw the slots

		for (int i = 0; i < slotX.length; i++)
		{
			this.drawTexturedModalRect(cornerX + slotX[i], cornerY + slotY[i], 0, 0, 18, 18);
			if (!logic.isStackInSlot(i + 1))
			{
				this.drawTexturedModalRect(cornerX + slotX[i], cornerY + slotY[i], 18 * iconX[i], 18 * iconY[i], 18, 18);
			}
		}

		// Draw description
		//texID = this.mc.renderEngine.getTexture("/mods/tinker/textures/gui/description.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		//this.mc.renderEngine.bindTexture(texID);
		this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/description.png");
		cornerX = (this.width + this.xSize) / 2;
		cornerY = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(cornerX, cornerY, 0, 0, 126, this.ySize + 30);
	}

	protected void keyTyped (char par1, int keyCode)
	{
		if (keyCode == 1)
		{
			logic.setToolname("");
			updateServer("");
			Keyboard.enableRepeatEvents(false);
			this.mc.thePlayer.closeScreen();
		}
		else
		{
			text.textboxKeyTyped(par1, keyCode);
			toolName = text.getText().trim();
			logic.setToolname(toolName);
			updateServer(toolName);
		}
	}

	void updateServer (String name)
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try
		{
			outputStream.writeByte(1);
			outputStream.writeInt(logic.worldObj.provider.dimensionId);
			outputStream.writeInt(logic.xCoord);
			outputStream.writeInt(logic.yCoord);
			outputStream.writeInt(logic.zCoord);
			outputStream.writeUTF(name);
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

	/*protected void mouseClicked(int par1, int par2, int par3)
	{
	    super.mouseClicked(par1, par2, par3);
	    text.mouseClicked(par1, par2, par3);
	}*/
}
