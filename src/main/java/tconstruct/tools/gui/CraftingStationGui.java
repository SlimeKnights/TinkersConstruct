package tconstruct.tools.gui;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.tools.*;
import tconstruct.library.util.HarvestLevels;
import tconstruct.tools.logic.CraftingStationLogic;

public class CraftingStationGui extends GuiContainer
{
    public boolean active;
    public String toolName;
    public GuiTextField text;
    public String title, body = "";
    CraftingStationLogic logic;

    // Panel positions

    int craftingLeft = 0;
    int craftingTop = 0;

    int descLeft = 0;
    int descTop = 0;

    int chestLeft = 0;
    int chestTop = 0;

    public CraftingStationGui(InventoryPlayer inventory, CraftingStationLogic logic, World world, int x, int y, int z)
    {
        super(logic.getGuiContainer(inventory, world, x, y, z));
        this.logic = logic;
        // text = new GuiTextField(this.fontRendererObj, this.xSize / 2 - 5, 8,
        // 30, 12);
        // this.text.setText("");
        title = "\u00A7n" + StatCollector.translateToLocal("gui.toolforge1");
        body = StatCollector.translateToLocal("gui.toolforge2");
        toolName = "";
    }

    @Override
    public void initGui ()
    {
        super.initGui();

        this.xSize = 176;
        this.ySize = 166;

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.craftingLeft = this.guiLeft;
        this.craftingTop = this.guiTop;
        this.descTop = this.craftingTop;

        if (logic.tinkerTable)
        {
            this.xSize += 126;
            this.descLeft = this.guiLeft + 176;
        }

        if (logic.chest != null)
        {
            this.xSize += 122;
            this.guiLeft -= 122;
            this.chestLeft = this.guiLeft + 6;
            this.chestTop = this.craftingTop;
            this.craftingLeft = this.guiLeft + 122;
            this.descLeft = this.guiLeft + 122 + 176;
            if (logic.doubleChest != null)
                this.ySize = 187;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        int offsetX = 0;

        if (logic.chest != null)
        {
            this.fontRendererObj.drawString(StatCollector.translateToLocal(logic.chest.get().getInventoryName()), 14, 6, 0x202020);
            offsetX = 122;
        }

        this.fontRendererObj.drawString(StatCollector.translateToLocal(logic.tinkerTable ? "crafters.TinkerTable" : logic.getInvName()), offsetX + 8, 6, 0x202020);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), offsetX + 8, 72, 0x202020);

        // this.fontRendererObj.drawString(toolName + "_", this.xSize / 2 - 18,
        // 8, 0xffffff);

        if (logic.tinkerTable)
        {
            if (logic.isStackInSlot(0))
                drawToolStats();
            else
                drawToolInformation();
        }
    }

    void drawToolStats ()
    {
        int offsetX = 239;
        if (logic.chest != null)
        {
            offsetX += 122;
        }

        ItemStack stack = logic.getStackInSlot(0);
        if (stack.getItem() instanceof ToolCore)
        {
            ToolCore tool = (ToolCore) stack.getItem();
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            this.drawCenteredString(fontRendererObj, "\u00A7n" + tool.getLocalizedToolName(), offsetX, 8, 0xffffff);

            drawModularToolStats(stack, tool, tags);
        }
        else if (stack.getItem() instanceof ArmorCore)
        {
            ArmorCore armor = (ArmorCore) stack.getItem();
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("TinkerArmor");
            this.drawCenteredString(fontRendererObj, "\u00A7n" + armor.getClass().getSimpleName(), offsetX, 8, 0xffffff); // todo: localize this

            drawModularArmorStats(stack, armor, tags);
        }
    }

    void drawModularArmorStats (ItemStack stack, ArmorCore tool, NBTTagCompound tags)
    {
        int offsetX = 178;
        if (logic.chest != null)
        {
            offsetX += 122;
        }

        int modifiers = tags.getInteger("Modifiers");
        int base = 24;
        int offset = 0;
        if (modifiers > 0)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), offsetX + 8, base + offset * 10, 0xffffff);
            offset++;
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
                fontRendererObj.drawString("- " + tipName, offsetX + 8, base + (offset + tipNum) * 10, 0xffffff);
            }
            else
                displayToolTips = false;
        }
    }

    void drawModularToolStats (ItemStack stack, ToolCore tool, NBTTagCompound tags)
    {
        List categories = Arrays.asList(tool.getTraits());
        final int durability = tags.getInteger("Damage");
        final int maxDur = tags.getInteger("TotalDurability");
        int availableDurability = maxDur - durability;

        int offsetX = 178;
        if (logic.chest != null)
        {
            offsetX += 122;
        }

        // Durability
        int base = 24;
        int offset = 0;
        if (maxDur > 0)
        {
            if (maxDur >= 10000)
            {
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation1"), offsetX + 8, base + offset * 11, 0xffffff);
                offset++;
                fontRendererObj.drawString("- " + availableDurability + "/" + maxDur, offsetX + 8, base + offset * 10, 0xffffff);
                offset++;
            }
            else
            {
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation2") + availableDurability + "/" + maxDur, offsetX + 8, base + offset * 10, 0xffffff);
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
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation3") + attack / 2 + heart, offsetX + 8, base + offset * 10, 0xffffff);
            else
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation3") + attack / 2f + heart, offsetX + 8, base + offset * 10, 0xffffff);
            offset++;

            if (stoneboundDamage != 0)
            {
                DecimalFormat df = new DecimalFormat("##.##");
                heart = stoneboundDamage == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
                String bloss = stoneboundDamage > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                this.fontRendererObj.drawString(bloss + df.format(stoneboundDamage / 2f) + heart, offsetX + 8, base + offset * 10, 0xffffff);
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
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation6") + df.format(trueDraw) + "s", offsetX + 8, base + offset * 10, 0xffffff);
            offset++;
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation7") + df.format(flightSpeed) + "x", offsetX + 8, base + offset * 10, 0xffffff);
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

            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation10"), offsetX + 8, base + offset * 10, 0xffffff);
            offset++;
            String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
            if (attack % 2 == 0)
                this.fontRendererObj.drawString("- " + attack / 2 + heart, offsetX + 8, base + offset * 10, 0xffffff);
            else
                this.fontRendererObj.drawString("- " + attack / 2f + heart, offsetX + 8, base + offset * 10, 0xffffff);
            offset++;
            int minAttack = attack;
            int maxAttack = attack * 2;
            heart = StatCollector.translateToLocal("gui.partcrafter9");
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation11"), offsetX + 8, base + offset * 10, 0xffffff);
            offset++;
            this.fontRendererObj.drawString(df.format(minAttack / 2f) + "-" + df.format(maxAttack / 2f) + heart, xSize + 8, base + offset * 10, 0xffffff);
            offset++;
            offset++;

            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation8") + df.format(mass), offsetX + 8, base + offset * 10, 0xffffff);
            offset++;
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation9") + df.format(accuracy - 4) + "%", offsetX + 8, base + offset * 10, 0xffffff);
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

            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation12"), offsetX + 8, base + offset * 10, 0xffffff);
            offset++;
            fontRendererObj.drawString("- " + df.format(trueSpeed) + ", " + df.format(trueSpeed2), offsetX + 8, base + offset * 10, 0xffffff);
            offset++;
            if (stoneboundSpeed != 0)
            {
                String bloss = stoneboundSpeed > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                fontRendererObj.drawString(bloss + df.format(stoneboundSpeed), offsetX + 8, base + offset * 10, 0xffffff);
                offset++;
            }
            offset++;
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation13"), offsetX + 8, base + offset * 10, 0xffffff);
            offset++;
            fontRendererObj.drawString("- " + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel")) + ", " + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel2")), offsetX + 8, base + offset * 10, 0xffffff);
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
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation14") + df.format(trueSpeed), offsetX + 8, base + offset * 10, 0xffffff);
            offset++;
            if (stoneboundSpeed != 0 && !Float.isNaN(stoneboundSpeed))
            {
                String bloss = stoneboundSpeed > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                fontRendererObj.drawString(bloss + df.format(stoneboundSpeed), xSize + 8, base + offset * 10, 0xffffff);
                offset++;
            }
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation15") + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel")), offsetX + 8, base + offset * 10, 0xffffff);
            offset++;
            offset++;
        }
        else if (categories.contains("utility"))
        {
            float mineSpeed = tags.getInteger("MiningSpeed");
            float trueSpeed = mineSpeed / (100f);
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation16") + trueSpeed, offsetX + 8, base + offset * 10, 0xffffff);
            offset++;
            offset++;
        }

        int modifiers = tags.getInteger("Modifiers");
        if (modifiers > 0)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), offsetX + 8, base + offset * 10, 0xffffff);
            offset++;
        }
        if (tags.hasKey("Tooltip1"))
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation17"), offsetX + 8, base + offset * 10, 0xffffff);
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
                fontRendererObj.drawString("- " + tipName, offsetX + 8, base + (offset + tipNum) * 10, 0xffffff);
            }
            else
                displayToolTips = false;
        }
    }

    void drawToolInformation ()
    {
        int offsetX = 239;
        if (logic.chest != null)
        {
            offsetX += 122;
        }
        this.drawCenteredString(fontRendererObj, title, offsetX, 8, 0xffffff);
        fontRendererObj.drawSplitString(body, offsetX - 56, 24, 115, 0xffffff);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/tinkertable.png");
    private static final ResourceLocation description = new ResourceLocation("tinker", "textures/gui/description.png");
    private static final ResourceLocation icons = new ResourceLocation("tinker", "textures/gui/icons.png");
    private static final ResourceLocation chest = new ResourceLocation("tinker", "textures/gui/chestside.png");

    @Override
    protected void drawGuiContainerBackgroundLayer (float par1, int par2, int par3)
    {
        // Draw the background
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        this.drawTexturedModalRect(this.craftingLeft, this.craftingTop, 0, 0, 176, 166);

        if (active)
        {
            this.drawTexturedModalRect(this.craftingLeft + 62, this.craftingTop, 0, 166, 112, 22);
        }

        this.mc.getTextureManager().bindTexture(icons);
        // Draw the slots

        if (logic.tinkerTable && !logic.isStackInSlot(5))
            this.drawTexturedModalRect(this.craftingLeft + 47, this.craftingTop + 33, 0, 233, 18, 18);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);

        // Draw chest side
        if (logic.chest != null)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(chest);

            if (logic.doubleChest == null)
                this.drawTexturedModalRect(this.chestLeft, this.chestTop, 0, 0, 122, 114);
            else
                this.drawTexturedModalRect(this.chestLeft, this.chestTop, 125, 0, 122, 187);
        }

        // Draw description
        if (logic.tinkerTable)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(description);
            this.drawTexturedModalRect(this.descLeft, this.descTop, 0, 0, 126, 172);
        }

    }

}
