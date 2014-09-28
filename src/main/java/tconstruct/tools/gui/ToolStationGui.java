package tconstruct.tools.gui;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import tconstruct.TConstruct;
import tconstruct.library.accessory.AccessoryCore;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.client.*;
import tconstruct.library.tools.*;
import tconstruct.library.util.HarvestLevels;
import tconstruct.smeltery.inventory.ActiveContainer;
import tconstruct.tools.inventory.ToolStationContainer;
import tconstruct.tools.logic.ToolStationLogic;
import tconstruct.util.network.ToolStationPacket;

@SideOnly(Side.CLIENT)
@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class ToolStationGui extends GuiContainer implements INEIGuiHandler
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
        toolSlots = (ToolStationContainer) inventorySlots;
        text = new GuiTextField(this.fontRendererObj, 83, 8, 30, 12);
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

    @Override
    protected void mouseClicked (int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0)
        {
            int gLeft = this.guiLeft + 68 + 110;
            int gTop = this.guiTop + 6;
            int gwidth = 102;
            int gheight = 12;
            active = mouseX > gLeft && mouseX < gLeft + gwidth && mouseY > gTop && mouseY < gTop + gheight;
        }
    }

    void resetGui ()
    {
        this.text.setText("");
        guiType = 0;
        setSlotType(0);
        iconX = new int[] { 0, 1, 2 };
        iconY = new int[] { 13, 13, 13 };
        title = "\u00A7n" + StatCollector.translateToLocal("gui.toolforge1");
        body = StatCollector.translateToLocal("gui.toolforge2");
    }

    @Override
    public void initGui ()
    {
        super.initGui();
        this.xSize = 176 + 110;
        this.guiLeft = (this.width - 176) / 2 - 110;

        this.buttonList.clear();
        ToolGuiElement repair = TConstructClientRegistry.toolButtons.get(0);
        GuiButtonTool repairButton = new GuiButtonTool(0, this.guiLeft, this.guiTop, repair.buttonIconX, repair.buttonIconY, repair.domain, repair.texture, repair); // Repair
        repairButton.enabled = false;
        this.buttonList.add(repairButton);

        for (int iter = 1; iter < TConstructClientRegistry.toolButtons.size(); iter++)
        {
            ToolGuiElement element = TConstructClientRegistry.toolButtons.get(iter);
            GuiButtonTool button = new GuiButtonTool(iter, this.guiLeft + 22 * (iter % 5), this.guiTop + 22 * (iter / 5), element.buttonIconX, element.buttonIconY, repair.domain, element.texture, element);
            this.buttonList.add(button);
        }
    }

    @Override
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

    @Override
    public void updateScreen ()
    {
        super.updateScreen();
        this.text.updateCursorCounter();
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        this.fontRendererObj.drawString(StatCollector.translateToLocal(logic.getInvName()), 116, 8, 0x000000);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 118, this.ySize - 96 + 2, 0x000000);
        this.fontRendererObj.drawString(toolName + "_", 180, 8, 0xffffff);

        if (logic.isStackInSlot(0))
            drawToolStats();
        else
            drawToolInformation();
    }

    protected void drawToolStats ()
    {
        ItemStack stack = logic.getStackInSlot(0);
        if (stack.getItem() instanceof ToolCore)
        {
            ToolCore tool = (ToolCore) stack.getItem();
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag(tool.getBaseTagName());
            this.drawCenteredString(fontRendererObj, "\u00A7n" + tool.getLocalizedToolName(), 349, 8, 0xffffff);

            drawModularToolStats(stack, tool, tags, 294, 24);
        }
        if(stack.getItem() instanceof ArmorCore)
        {
            ArmorCore armor = (ArmorCore) stack.getItem();
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag(armor.getBaseTagName());
            this.drawCenteredString(fontRendererObj, "\u00A7n" + stack.getDisplayName(), 349, 8, 0xffffff); // todo: localize

            drawModularArmorStats(stack, armor, tags, 294, 24);
        }
        if(stack.getItem() instanceof AccessoryCore)
        {
            AccessoryCore accessory = (AccessoryCore) stack.getItem();
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag(accessory.getBaseTagName());
            this.drawCenteredString(fontRendererObj, "\u00A7n" + stack.getDisplayName(), 349, 8, 0xffffff); // todo: localize

            drawModularAccessoryStats(stack, accessory, tags, 294, 24);
        }
    }

    protected void drawModularToolStats (ItemStack stack, ToolCore tool, NBTTagCompound tags, int x, int y)
    {
        List categories = Arrays.asList(tool.getTraits());
        final int durability = tags.getInteger("Damage");
        final int maxDur = tags.getInteger("TotalDurability");
        int availableDurability = maxDur - durability;

        // Durability
        int base = y;
        int offset = 0;
        if (maxDur > 0)
        {
            if (maxDur >= 10000)
            {
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation1"), x, base + offset * 11, 0xffffff);
                offset++;
                fontRendererObj.drawString("- " + availableDurability + "/" + maxDur, x, base + offset * 10, 0xffffff);
                offset++;
            }
            else
            {
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation2") + availableDurability + "/" + maxDur, x, base + offset * 10, 0xffffff);
                offset++;
            }
        }

        final float stonebound = tags.getFloat("Shoddy");
        // Attack
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
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation3") + attack / 2 + heart, x, base + offset * 10, 0xffffff);
            else
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation3") + attack / 2f + heart, x, base + offset * 10, 0xffffff);
            offset++;

            if (stoneboundDamage != 0)
            {
                DecimalFormat df = new DecimalFormat("##.##");
                heart = stoneboundDamage == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
                String bloss = stoneboundDamage > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                this.fontRendererObj.drawString(bloss + df.format(stoneboundDamage / 2f) + heart, x, base + offset * 10, 0xffffff);
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
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation6") + df.format(trueDraw) + "s", x, base + offset * 10, 0xffffff);
            offset++;
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation7") + df.format(flightSpeed) + "x", x, base + offset * 10, 0xffffff);
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

            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation10"), x, base + offset * 10, 0xffffff);
            offset++;
            String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
            if (attack % 2 == 0)
                this.fontRendererObj.drawString("- " + attack / 2 + heart, x, base + offset * 10, 0xffffff);
            else
                this.fontRendererObj.drawString("- " + attack / 2f + heart, x, base + offset * 10, 0xffffff);
            offset++;
            int minAttack = attack;
            int maxAttack = attack * 2;
            heart = StatCollector.translateToLocal("gui.partcrafter9");
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation11"), x, base + offset * 10, 0xffffff);
            offset++;
            this.fontRendererObj.drawString(df.format(minAttack / 2f) + "-" + df.format(maxAttack / 2f) + heart, x, base + offset * 10, 0xffffff);
            offset++;
            offset++;

            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation8") + df.format(mass), x, base + offset * 10, 0xffffff);
            offset++;
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation9") + df.format(accuracy - 4) + "%", x, base + offset * 10, 0xffffff);
            offset++;
            /*
             * this.fontRendererObj.drawString("Chance to break: " +
             * df.format(shatter)+"%", xSize + 8, base + offset * 10, 0xffffff);
             * offset++;
             */
            offset++;
        }

        // Mining
        if (categories.contains("dualharvest"))
        {
            float mineSpeed = tags.getInteger("MiningSpeed") / 100f * ((HarvestTool) tool).breakSpeedModifier();
            float mineSpeed2 = tags.getInteger("MiningSpeed2") / 100f * ((HarvestTool) tool).breakSpeedModifier();
            float stoneboundSpeed = (float) Math.log(durability / ((HarvestTool) tool).stoneboundModifier() + 1) * 2 * stonebound;
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            float trueSpeed = mineSpeed + stoneboundSpeed;
            float trueSpeed2 = mineSpeed2 + stoneboundSpeed;

            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation12"), x, base + offset * 10, 0xffffff);
            offset++;
            fontRendererObj.drawString("- " + df.format(trueSpeed) + ", " + df.format(trueSpeed2), x, base + offset * 10, 0xffffff);
            offset++;
            if (stoneboundSpeed != 0)
            {
                String bloss = stoneboundSpeed > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                fontRendererObj.drawString(bloss + df.format(stoneboundSpeed), x, base + offset * 10, 0xffffff);
                offset++;
            }
            offset++;
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation13"), x, base + offset * 10, 0xffffff);
            offset++;
            fontRendererObj.drawString("- " + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel")) + ", " + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel2")), x, base + offset * 10, 0xffffff);
            offset++;
            offset++;
        }
        else if (categories.contains("harvest"))
        {
            float trueSpeed = AbilityHelper.calcToolSpeed(tool, tags);
            float stoneboundSpeed = AbilityHelper.calcToolSpeed(tool, tags);

            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            trueSpeed += stoneboundSpeed;
            if (trueSpeed < 0)
                trueSpeed = 0;
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation14") + df.format(trueSpeed), x, base + offset * 10, 0xffffff);
            offset++;
            if (stoneboundSpeed != 0 && !Float.isNaN(stoneboundSpeed))
            {
                String bloss = stoneboundSpeed > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                fontRendererObj.drawString(bloss + df.format(stoneboundSpeed), x, base + offset * 10, 0xffffff);
                offset++;
            }
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation15") + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel")), x, base + offset * 10, 0xffffff);
            offset++;
            offset++;
        }
        else if (categories.contains("utility"))
        {
            float mineSpeed = tags.getInteger("MiningSpeed");
            float trueSpeed = mineSpeed / (100f);
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation16") + trueSpeed, x, base + offset * 10, 0xffffff);
            offset++;
            offset++;
        }

        int modifiers = tags.getInteger("Modifiers");
        if (modifiers > 0)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), x, base + offset * 10, 0xffffff);
            offset++;
        }
        if (tags.hasKey("Tooltip1"))
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation17"), x, base + offset * 10, 0xffffff);
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
                fontRendererObj.drawString("- " + tipName, x, base + (offset + tipNum) * 10, 0xffffff);
            }
            else
                displayToolTips = false;
        }
    }

    private static DecimalFormat df =  new DecimalFormat("##.#");

    // todo: do this properly, quick and dirty fix
    protected void drawModularArmorStats (ItemStack stack, ArmorCore armor, NBTTagCompound tags, int x, int y)
    {
        List categories = Arrays.asList(armor.getTraits());
        int base = y;
        int offset = 0;

        // durability
        final int durability = tags.getInteger("Damage");
        final int maxDur = tags.getInteger("TotalDurability");
        int availableDurability = maxDur - durability;

        // Durability
        if (maxDur > 0)
        {
            if (maxDur >= 10000)
            {
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation1"), x, base + offset * 11, 0xffffff);
                offset++;
                fontRendererObj.drawString("- " + availableDurability + "/" + maxDur, x, base + offset * 10, 0xffffff);
                offset++;
            }
            else
            {
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation2") + availableDurability + "/" + maxDur, x, base + offset * 10, 0xffffff);
                offset++;
            }
        }
        // Damage reduction
        double damageReduction = tags.getDouble("DamageReduction");
        if(damageReduction > 0.000001d)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation19") + df.format(damageReduction), x, base + offset * 10, 0xffffff);
            offset++;
        }

        // Protection
        double protection = armor.getProtection(stack);
        double maxProtection = tags.getDouble("MaxDefense");
        //if(maxProtection > protection)
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation20") + df.format(protection) + "/" + df.format(maxProtection), 294, base + offset * 10, 0xffffff);
        //else
          //  fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation20") + df.format(protection), x, base + offset * 10, 0xffffff);
        offset++;

        offset++;
        int modifiers = tags.getInteger("Modifiers");
        if (modifiers > 0)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), x, base + offset * 10, 0xffffff);
            offset++;
        }
        if (tags.hasKey("Tooltip1"))
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation17"), x, base + offset * 10, 0xffffff);
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
                fontRendererObj.drawString("- " + tipName, x, base + (offset + tipNum) * 10, 0xffffff);
            }
            else
                displayToolTips = false;
        }
    }

    // todo: also quick and dirty fix
    protected void drawModularAccessoryStats (ItemStack stack, AccessoryCore accessory, NBTTagCompound tags, int x, int y)
    {
        List categories = Arrays.asList(accessory.getTraits());
        int base = y;
        int offset = 0;

        if (categories.contains("utility"))
        {
            float mineSpeed = tags.getInteger("MiningSpeed");
            float trueSpeed = mineSpeed / (100f);
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation16") + trueSpeed, x, base + offset * 10, 0xffffff);
            offset++;
        }

        offset++;
        int modifiers = tags.getInteger("Modifiers");
        if (modifiers > 0)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), x, base + offset * 10, 0xffffff);
            offset++;
        }
        if (tags.hasKey("Tooltip1"))
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation17"), x, base + offset * 10, 0xffffff);
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
                fontRendererObj.drawString("- " + tipName, x, base + (offset + tipNum) * 10, 0xffffff);
            }
            else
                displayToolTips = false;
        }
    }

    protected void drawToolInformation ()
    {
        this.drawCenteredString(fontRendererObj, title, 349, 8, 0xffffff);
        fontRendererObj.drawSplitString(body, 294, 24, 115, 0xffffff);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/toolstation.png");
    private static final ResourceLocation icons = new ResourceLocation("tinker", "textures/gui/icons.png");
    private static final ResourceLocation description = new ResourceLocation("tinker", "textures/gui/description.png");

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer (float par1, int par2, int par3)
    {
        // Draw the background
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        final int cornerX = this.guiLeft + 110;
        this.drawTexturedModalRect(cornerX, this.guiTop, 0, 0, 176, this.ySize);

        if (active)
        {
            this.drawTexturedModalRect(cornerX + 62, this.guiTop, 0, this.ySize, 112, 22);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);
        // Draw the slots

        for (int i = 0; i < slotX.length; i++)
        {
            this.drawTexturedModalRect(cornerX + slotX[i], this.guiTop + slotY[i], 144, 216, 18, 18);
            if (!logic.isStackInSlot(i + 1))
            {
                this.drawTexturedModalRect(cornerX + slotX[i], this.guiTop + slotY[i], 18 * iconX[i], 18 * iconY[i], 18, 18);
            }
        }

        // Draw description
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(description);
        this.drawTexturedModalRect(cornerX + 176, this.guiTop, 0, 0, 126, this.ySize + 30);

    }

    @Override
    protected void keyTyped (char par1, int keyCode)
    {
        if (keyCode == 1 || (!active && keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()))
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
        /*
         * ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
         * DataOutputStream outputStream = new DataOutputStream(bos); try {
         * outputStream.writeByte(1);
         * outputStream.writeInt(logic.getWorld().provider.dimensionId);
         * outputStream.writeInt(logic.xCoord);
         * outputStream.writeInt(logic.yCoord);
         * outputStream.writeInt(logic.zCoord); outputStream.writeUTF(name); }
         * catch (Exception ex) { ex.printStackTrace(); }
         * 
         * Packet250CustomPayload packet = new Packet250CustomPayload();
         * packet.channel = "TConstruct"; packet.data = bos.toByteArray();
         * packet.length = bos.size();
         * 
         * PacketDispatcher.sendPacketToServer(packet);
         */

        TConstruct.packetPipeline.sendToServer(new ToolStationPacket(logic.xCoord, logic.yCoord, logic.zCoord, name));
    }

    /*
     * protected void mouseClicked(int par1, int par2, int par3) {
     * super.mouseClicked(par1, par2, par3); text.mouseClicked(par1, par2,
     * par3); }
     */

    @Override
    public VisiblityData modifyVisiblity (GuiContainer gui, VisiblityData currentVisibility)
    {
        if (width - xSize < 107)
        {
            currentVisibility.showWidgets = false;
        }
        else
        {
            currentVisibility.showWidgets = true;
        }

        if (guiLeft < 58)
        {
            currentVisibility.showStateButtons = false;
        }

        return currentVisibility;
    }

    @Override
    public Iterable<Integer> getItemSpawnSlots (GuiContainer gui, ItemStack item)
    {
        return null;
    }

    @Override
    public List<TaggedInventoryArea> getInventoryAreas (GuiContainer gui)
    {
        return Collections.emptyList();
    }

    @Override
    public boolean handleDragNDrop (GuiContainer gui, int mousex, int mousey, ItemStack draggedStack, int button)
    {
        return false;
    }

    @Override
    public boolean hideItemPanelSlot (GuiContainer gui, int x, int y, int w, int h)
    {
        if (y + h - 4 < guiTop || y + 4 > guiTop + ySize)
            return false;

        if (x + 4 > guiLeft + xSize + 126)
            return false;

        return true;
    }
}
