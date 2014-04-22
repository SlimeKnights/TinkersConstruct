package tconstruct.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import tconstruct.blocks.logic.ToolStationLogic;
import tconstruct.inventory.ActiveContainer;
import tconstruct.inventory.ToolStationContainer;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.client.ToolGuiElement;
import tconstruct.library.tools.HarvestTool;
import tconstruct.library.tools.ToolCore;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ToolStationGui extends NewContainerGui
{
    public ToolStationLogic logic;
    public ToolStationContainer toolSlots;
    public GuiTextField text;
    public String toolName;
    public int guiType;
    public int[] slotX, slotY, iconX, iconY;
    public boolean active;
    public String title, body = "";

    public ToolStationGui(InventoryPlayer inventoryplayer, ToolStationLogic stationlogic, World world, int x, int y, int z)
    {
        super((ActiveContainer) stationlogic.getGuiContainer(inventoryplayer, world, x, y, z));
        this.logic = stationlogic;
        toolSlots = (ToolStationContainer) container;
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
        guiType = 0;
        setSlotType(0);
        iconX = new int[] { 0, 1, 2 };
        iconY = new int[] { 13, 13, 13 };
        title = "\u00A7n" + (StatCollector.translateToLocal("gui.toolforge1"));
        body = (StatCollector.translateToLocal("gui.toolforge2"));
    }

    public void initGui ()
    {
        super.initGui();
        int cornerX = (this.width - this.xSize) / 2;
        int cornerY = (this.height - this.ySize) / 2;

        this.buttonList.clear();
        ToolGuiElement repair = TConstructClientRegistry.toolButtons.get(0);
        GuiButtonTool repairButton = new GuiButtonTool(0, cornerX - 110, cornerY, repair.buttonIconX, repair.buttonIconY, repair.domain, repair.texture, repair); // Repair
        repairButton.enabled = false;
        this.buttonList.add(repairButton);

        for (int iter = 1; iter < TConstructClientRegistry.toolButtons.size(); iter++)
        {
            ToolGuiElement element = TConstructClientRegistry.toolButtons.get(iter);
            GuiButtonTool button = new GuiButtonTool(iter, cornerX - 110 + 22 * (iter % 5), cornerY + 22 * (iter / 5), element.buttonIconX, element.buttonIconY, repair.domain, element.texture,
                    element);
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
        case 7:
            slotX = new int[] { 56, 56, 56 }; // Three parts reverse
            slotY = new int[] { 19, 37, 55 };
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
        this.fontRenderer.drawString(StatCollector.translateToLocal(logic.getInvName()), 6, 8, 0x000000);
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
        if (stack.getItem() instanceof ToolCore)
        {
            ToolCore tool = (ToolCore) stack.getItem();
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            this.drawCenteredString(fontRenderer, "\u00A7n" + tool.getToolName(), xSize + 63, 8, 0xffffff);

            drawModularToolStats(stack, tool, tags);
        }
    }

    void drawModularToolStats (ItemStack stack, ToolCore tool, NBTTagCompound tags)
    {
        List categories = Arrays.asList(tool.toolCategories());
        final int durability = tags.getInteger("Damage");
        final int maxDur = tags.getInteger("TotalDurability");
        int availableDurability = maxDur - durability;

        //Durability
        int base = 24;
        int offset = 0;
        if (maxDur > 0)
        {
            if (maxDur >= 10000)
            {
                fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation1"), xSize + 8, base + offset * 11, 0xffffff);
                offset++;
                fontRenderer.drawString("- " + availableDurability + "/" + maxDur, xSize + 8, base + offset * 10, 0xffffff);
                offset++;
            }
            else
            {
                fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation2") + availableDurability + "/" + maxDur, xSize + 8, base + offset * 10, 0xffffff);
                offset++;
            }
        }

        final float stonebound = tags.getFloat("Shoddy");
        //Attack
        if (categories.contains("weapon"))
        {
            int attack = (int) (tags.getInteger("Attack")) + 1;
            float stoneboundDamage = (float) Math.log(durability / 72f + 1) * -2 * stonebound;
            attack += stoneboundDamage;
            attack *= tool.getDamageModifier();
            if (attack < 1)
                attack = 1;

            String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
            if (attack % 2 == 0)
                this.fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation3") + attack / 2 + heart, xSize + 8, base + offset * 10, 0xffffff);
            else
                this.fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation3") + attack / 2f + heart, xSize + 8, base + offset * 10, 0xffffff);
            offset++;

            if (stoneboundDamage != 0)
            {
                DecimalFormat df = new DecimalFormat("##.##");
                heart = stoneboundDamage == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
                String bloss = stoneboundDamage > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                this.fontRenderer.drawString(bloss + df.format(stoneboundDamage / 2f) + heart, xSize + 8, base + offset * 10, 0xffffff);
                offset++;
            }
            offset++;
        }

        if (categories.contains("bow"))
        {
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            int drawSpeed = tags.getInteger("DrawSpeed");
            float flightSpeed = tags.getFloat("FlightSpeed");
            float trueDraw = drawSpeed / 20f * flightSpeed;
            this.fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation6") + df.format(trueDraw) + "s", xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            this.fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation7") + df.format(flightSpeed) + "x", xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            offset++;
        }

        if (categories.contains("ammo"))
        {
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            int attack = (int) (tags.getInteger("Attack"));
            float mass = tags.getFloat("Mass");
            float shatter = tags.getFloat("BreakChance");
            float accuracy = tags.getFloat("Accuracy");

            this.fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation10"), xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
            if (attack % 2 == 0)
                this.fontRenderer.drawString("- " + attack / 2 + heart, xSize + 8, base + offset * 10, 0xffffff);
            else
                this.fontRenderer.drawString("- " + attack / 2f + heart, xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            int minAttack = attack;
            int maxAttack = attack * 2;
            heart = StatCollector.translateToLocal("gui.partcrafter9");
            this.fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation11"), xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            this.fontRenderer.drawString(df.format(minAttack / 2f) + "-" + df.format(maxAttack / 2f) + heart, xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            offset++;

            this.fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation8") + df.format(mass), xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            this.fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation9") + df.format(accuracy - 4) + "%", xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            /*this.fontRenderer.drawString("Chance to break: " + df.format(shatter)+"%", xSize + 8, base + offset * 10, 0xffffff);
            offset++;*/
            offset++;
        }

        //Mining
        if (categories.contains("dualharvest"))
        {
            float mineSpeed = tags.getInteger("MiningSpeed") / 100f * ((HarvestTool) tool).breakSpeedModifier();
            float mineSpeed2 = tags.getInteger("MiningSpeed2") / 100f * ((HarvestTool) tool).breakSpeedModifier();
            float stoneboundSpeed = (float) Math.log(durability / ((HarvestTool) tool).stoneboundModifier() + 1) * 2 * stonebound;
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            float trueSpeed = mineSpeed + stoneboundSpeed;
            float trueSpeed2 = mineSpeed2 + stoneboundSpeed;

            fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation12"), xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            fontRenderer.drawString("- " + df.format(trueSpeed) + ", " + df.format(trueSpeed2), xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            if (stoneboundSpeed != 0)
            {
                String bloss = stoneboundSpeed > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                fontRenderer.drawString(bloss + df.format(stoneboundSpeed), xSize + 8, base + offset * 10, 0xffffff);
                offset++;
            }
            offset++;
            fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation13"), xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            fontRenderer
                    .drawString("- " + getHarvestLevelName(tags.getInteger("HarvestLevel")) + ", " + getHarvestLevelName(tags.getInteger("HarvestLevel2")), xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            offset++;
        }
        else if (categories.contains("harvest"))
        {
            float mineSpeed = tags.getInteger("MiningSpeed");
            int heads = 1;

            if (tags.hasKey("MiningSpeed2"))
            {
                mineSpeed += tags.getInteger("MiningSpeed2");
                heads++;
            }

            if (tags.hasKey("MiningSpeedHandle"))
            {
                mineSpeed += tags.getInteger("MiningSpeedHandle");
                heads++;
            }

            if (tags.hasKey("MiningSpeedExtra"))
            {
                mineSpeed += tags.getInteger("MiningSpeedExtra");
                heads++;
            }

            float trueSpeed = mineSpeed / (heads * 100f);
            if (tool instanceof HarvestTool)
                trueSpeed *= ((HarvestTool) tool).breakSpeedModifier();

            float localStonebound = 72f;
            if (tool instanceof HarvestTool)
                localStonebound = ((HarvestTool) tool).stoneboundModifier();
            float stoneboundSpeed = (float) Math.log(durability / localStonebound + 1) * 2 * stonebound;
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            trueSpeed += stoneboundSpeed;
            if (trueSpeed < 0)
                trueSpeed = 0;
            fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation14") + df.format(trueSpeed), xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            if (stoneboundSpeed != 0 && !Float.isNaN(stoneboundSpeed))
            {
                System.out.println("Stonebound: "+stoneboundSpeed);
                String bloss = stoneboundSpeed > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                fontRenderer.drawString(bloss + df.format(stoneboundSpeed), xSize + 8, base + offset * 10, 0xffffff);
                offset++;
            }
            fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation15") + getHarvestLevelName(tags.getInteger("HarvestLevel")), xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            offset++;
        }
        else if (categories.contains("utility"))
        {
            float mineSpeed = tags.getInteger("MiningSpeed");
            float trueSpeed = mineSpeed / (100f);
            fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation16") + trueSpeed, xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            offset++;
        }

        int modifiers = tags.getInteger("Modifiers");
        if (modifiers > 0)
        {
            fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), xSize + 8, base + offset * 10, 0xffffff);
            offset++;
        }
        if (tags.hasKey("Tooltip1"))
        {
            fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation17"), xSize + 8, base + offset * 10, 0xffffff);
        }

        boolean displayToolTips = true;
        int tipNum = 0;
        while (displayToolTips)
        {
            tipNum++;
            String tooltip = "ModifierTip" + tipNum;
            if (tags.hasKey(tooltip))
            {
                String tipName = tags.getString(tooltip);
                fontRenderer.drawString("- " + tipName, xSize + 8, base + (offset + tipNum) * 10, 0xffffff);
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

    protected String getHarvestLevelName (int num)
    {
        switch (num)
        {
        case 0:
            return (StatCollector.translateToLocal("gui.partcrafter.mining1"));
        case 1:
            return (StatCollector.translateToLocal("gui.partcrafter.mining2"));
        case 2:
            return (StatCollector.translateToLocal("gui.partcrafter.mining3"));
        case 3:
            return (StatCollector.translateToLocal("gui.partcrafter.mining4")); //Mithril
        case 4:
            return (StatCollector.translateToLocal("gui.partcrafter.mining5"));
        case 5:
            return (StatCollector.translateToLocal("gui.partcrafter.mining6"));
        default:
            return String.valueOf(num);
        }
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/toolstation.png");
    private static final ResourceLocation icons = new ResourceLocation("tinker", "textures/gui/icons.png");
    private static final ResourceLocation description = new ResourceLocation("tinker", "textures/gui/description.png");

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer (float par1, int par2, int par3)
    {
        // Draw the background
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int cornerX = (this.width - this.xSize) / 2;
        int cornerY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);

        if (active)
        {
            this.drawTexturedModalRect(cornerX + 62, cornerY, 0, this.ySize, 112, 22);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);
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
        this.mc.getTextureManager().bindTexture(description);
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
