package mods.tinker.tconstruct.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import mods.tinker.tconstruct.blocks.logic.ToolForgeLogic;
import mods.tinker.tconstruct.inventory.ActiveContainer;
import mods.tinker.tconstruct.inventory.ToolForgeContainer;
import mods.tinker.tconstruct.library.client.TConstructClientRegistry;
import mods.tinker.tconstruct.library.client.ToolGuiElement;
import mods.tinker.tconstruct.library.tools.ToolCore;
import mods.tinker.tconstruct.library.tools.Weapon;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
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
public class ToolForgeGui extends NewContainerGui
{
    ToolForgeLogic logic;
    ToolForgeContainer toolSlots;
    GuiTextField text;
    String toolName;
    int selectedButton;
    int[] slotX, slotY, iconX, iconY;
    boolean active;
    String title, body = "";

    public ToolForgeGui(InventoryPlayer inventoryplayer, ToolForgeLogic stationlogic, World world, int x, int y, int z)
    {
        super((ActiveContainer) stationlogic.getGuiContainer(inventoryplayer, world, x, y, z));
        this.logic = stationlogic;
        toolSlots = (ToolForgeContainer) container;
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

    protected void mouseClicked (int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0)
        {
            int gLeft = this.guiLeft + 68;
            int gTop = this.guiTop + 6;
            int gWidth = 102;
            int gHeight = 12;
            active = mouseX > gLeft && mouseX < gLeft + gWidth && mouseY > gTop && mouseY < gTop + gHeight;
        }
    }

    void resetGui ()
    {
        this.text.setText("");
        selectedButton = 0;
        setSlotType(0);
        iconX = new int[] { 0, 1, 2, 13 };
        iconY = new int[] { 13, 13, 13, 13 };
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
        GuiButtonTool repairButton = new GuiButtonTool(0, cornerX - 110, cornerY, repair.buttonIconX, repair.buttonIconY, repair.texture, repair); // Repair
        repairButton.enabled = false;
        this.buttonList.add(repairButton);
        int offset = TConstructClientRegistry.tierTwoButtons.size();

        for (int iter = 0; iter < TConstructClientRegistry.tierTwoButtons.size(); iter++)
        {
            ToolGuiElement element = TConstructClientRegistry.tierTwoButtons.get(iter);
            GuiButtonTool button = new GuiButtonTool(iter+1, cornerX - 110 + 22 * ((iter + 1) % 5), cornerY + 22 * ((iter + 1) / 5), element.buttonIconX, element.buttonIconY, element.texture, element);
            this.buttonList.add(button);
        }

        for (int iter = 1; iter < TConstructClientRegistry.toolButtons.size(); iter++)
        {
            ToolGuiElement element = TConstructClientRegistry.toolButtons.get(iter);
            GuiButtonTool button = new GuiButtonTool(iter+offset, cornerX - 110 + 22 * ((iter + offset) % 5), cornerY + 22 * ((iter + offset) / 5), element.buttonIconX, element.buttonIconY, element.texture,
                    element);
            this.buttonList.add(button);
        }
    }

    protected void actionPerformed (GuiButton button)
    {
        GuiButtonTool b = (GuiButtonTool) button;
        ((GuiButton) this.buttonList.get(selectedButton)).enabled = true;
        selectedButton = button.id;
        button.enabled = false;

        setSlotType(b.element.slotType);
        iconX = b.element.iconsX;
        iconY = b.element.iconsY;
        title = "\u00A7n" + b.element.title;
        body = b.element.body;
    }

    void setSlotType (int type)
    {
        switch (type)
        {
        case 0:
            slotX = new int[] { 56, 38, 38, 14 }; // Repair
            slotY = new int[] { 37, 28, 46, 37 };
            break;
        case 1:
            slotX = new int[] { 56, 56, 56, 14 }; // Three parts
            slotY = new int[] { 19, 55, 37, 37 };
            break;
        case 2:
            slotX = new int[] { 56, 56, 14, 14 }; // Two parts
            slotY = new int[] { 28, 46, 28, 46 };
            break;
        case 3:
            slotX = new int[] { 38, 47, 56, 14 }; // Double head
            slotY = new int[] { 28, 46, 28, 37 };
            break;
        case 4:
            slotX = new int[] { 47, 38, 56, 47 }; // Four parts
            slotY = new int[] { 19, 37, 37, 55 };
            break;
        case 5:
            slotX = new int[] { 38, 47, 56, 47 }; // Four parts, double head
            slotY = new int[] { 19, 55, 19, 37 };
            break;
        case 6:
            slotX = new int[] { 38, 38, 20, 56 }; // Double head
            slotY = new int[] { 28, 46, 28, 28 };
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
        this.fontRenderer.drawString(StatCollector.translateToLocal("crafters.ToolForge"), 6, 8, 0x000000);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x000000);
        this.fontRenderer.drawString(toolName + "_", this.xSize / 2 - 18, 8, 0xffffff);

        if (logic.isStackInSlot(0))
            drawToolStats();
        else
            drawToolInformation();

        //this.fontRenderer.drawString("Namebox active: "+active, this.xSize / 2 - 18, -10, 0xffffff);
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
        int attack = (int) (tags.getInteger("Attack") * tool.getDamageModifier());

        int durability = tags.getInteger("Damage");
        float stonebound = tags.getFloat("Shoddy");
        float stoneboundDamage = -stonebound * durability / 65f;
        if (stonebound > 0)
            stoneboundDamage = -stonebound * durability / 100f;
        attack += stoneboundDamage;
        if (attack < 1)
            attack = 1;

        String heart = attack == 2 ? " Heart" : " Hearts";
        if (attack % 2 == 0)
            this.fontRenderer.drawString("Attack: " + attack / 2 + heart, xSize + 8, 35, 0xffffff);
        else
            this.fontRenderer.drawString("Attack: " + attack / 2f + heart, xSize + 8, 35, 0xffffff);
        //fontRenderer.drawString("Attack: " + damage, xSize + 8, 35, 0xffffff);
        if (stoneboundDamage != 0)
        {
            heart = stoneboundDamage == 2 ? " Heart" : " Hearts";
            String bloss = stoneboundDamage > 0 ? "Bonus: " : "Loss: ";
            this.fontRenderer.drawString(bloss + (int) stoneboundDamage / 2 + heart, xSize + 8, 46, 0xffffff);
        }

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

        int attack = tags.getInteger("Attack");
        String heart = attack == 2 ? " Heart" : " Hearts";
        if (attack % 2 == 0)
            this.fontRenderer.drawString("Attack: " + attack / 2 + heart, xSize + 8, 35, 0xffffff);
        else
            this.fontRenderer.drawString("Attack: " + attack / 2f + heart, xSize + 8, 35, 0xffffff);
        /*int damage = tags.getInteger("Attack");
        fontRenderer.drawString("Damage: " + damage, xSize + 8, 35, 0xffffff);*/
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
            return "Cobalt";
        case 5:
            return "Manyullyn";
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
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/toolstation.png");
        int cornerX = (this.width - this.xSize) / 2;
        int cornerY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);

        if (active)
        {
            this.drawTexturedModalRect(cornerX + 62, cornerY, 0, this.ySize, 112, 22);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/icons.png");
        // Draw the slots

        for (int i = 0; i < slotX.length; i++)
        {
            this.drawTexturedModalRect(cornerX + slotX[i] - 4, cornerY + slotY[i] - 4, 140, 212, 28, 28);
            if (!logic.isStackInSlot(i + 1))
            {
                this.drawTexturedModalRect(cornerX + slotX[i], cornerY + slotY[i], 18 * iconX[i], 18 * iconY[i], 18, 18);
            }
        }

        // Draw description
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/description.png");
        cornerX = (this.width + this.xSize) / 2;
        cornerY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(cornerX, cornerY, 0, 0, 126, this.ySize + 30);

    }

    protected void keyTyped (char par1, int keyCode)
    {
        if (keyCode == 1 || (!active && keyCode == this.mc.gameSettings.keyBindInventory.keyCode))
        {
            logic.setToolname("");
            updateServer("");
            Keyboard.enableRepeatEvents(false);
            this.mc.thePlayer.closeScreen();
        }
        else if (active)
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
