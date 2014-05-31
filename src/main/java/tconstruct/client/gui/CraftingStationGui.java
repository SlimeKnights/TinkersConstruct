package tconstruct.client.gui;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import tconstruct.blocks.logic.CraftingStationLogic;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.tools.ToolCore;

public class CraftingStationGui extends GuiContainer
{
    public boolean active;
    public String toolName;
    public GuiTextField text;
    public String title, body = "";
    CraftingStationLogic logic;
    private int guiLOffset;
    private int guiLPos;
    private int guiROffset;
    private int guiRPos;

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
        xSize = 175;
        ySize = 166;
        guiLOffset = (this.logic.chest == null ? 0 : 117);
        guiROffset = guiLOffset + 175;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        if(this.logic.chest != null)
        {
            guiLeft -= 121;
            xSize += 121;
            if(logic.doubleChest != null)
                ySize = 187;
        }
        guiLPos = this.guiLeft + this.guiLOffset;
        guiRPos = this.guiLeft + this.guiROffset;
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        this.fontRendererObj.drawString(StatCollector.translateToLocal(logic.tinkerTable ? "crafters.TinkerTable" : logic.getInvName()), this.guiLOffset + 8, 6, 0x202020);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), this.guiLOffset + 8, 72, 0x202020);
        if (logic.chest != null)
            this.fontRendererObj.drawString(StatCollector.translateToLocal(logic.chest.get().getInventoryName()), 13, 6, 0x202020);
        // this.fontRendererObj.drawString(toolName + "_", this.xSize / 2 - 18,
        // 8, 0xffffff);

        if (logic.tinkerTable)
        {
            ItemStack stack = logic.getStackInSlot(5);
            if (stack != null && (stack.getItem() instanceof ToolCore || stack.getItem() instanceof ArmorCore))
                drawToolStats();
            else
                drawToolInformation();
        }
    }

    void drawToolStats ()
    {
        ItemStack stack = logic.isStackInSlot(0) ? logic.getStackInSlot(0) : logic.getStackInSlot(5);
        final int x = this.guiROffset + 63;
        if (stack.getItem() instanceof ToolCore)
        {
            ToolCore tool = (ToolCore) stack.getItem();
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            this.drawCenteredString(fontRendererObj, "\u00A7n" + tool.getToolName(), x, 8, 0xffffff);

            drawModularToolStats(stack, tool, tags);
        }
        else if (stack.getItem() instanceof ArmorCore)
        {
            ArmorCore armor = (ArmorCore) stack.getItem();
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("TinkerArmor");
            this.drawCenteredString(fontRendererObj, "\u00A7n" + armor.getClass().getSimpleName(), x, 8, 0xffffff);

            drawModularArmorStats(stack, armor, tags);
        }
    }

    void drawModularArmorStats (ItemStack stack, ArmorCore tool, NBTTagCompound tags)
    {
        final int x = this.guiROffset + 8;
        int modifiers = tags.getInteger("Modifiers");
        int base = 24;
        int offset = 0;
        if (modifiers > 0)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), x, base + offset * 10, 0xffffff);
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
                fontRendererObj.drawString("- " + tipName, x, base + (offset + tipNum) * 10, 0xffffff);
            }
            else
                displayToolTips = false;
        }
    }

    void drawModularToolStats (ItemStack stack, ToolCore tool, NBTTagCompound tags)
    {
        List categories = Arrays.asList(tool.toolCategories());
        final int durability = tags.getInteger("Damage");
        final int maxDur = tags.getInteger("TotalDurability");
        int availableDurability = maxDur - durability;
        final int x = this.guiROffset + 8;

        // Durability
        int base = 24;
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
            int attack = (int) (tags.getInteger("Attack"));
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
                heart = stoneboundDamage == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
                String bloss = stoneboundDamage > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                this.fontRendererObj.drawString(bloss + (int) stoneboundDamage / 2 + heart, x, base + offset * 10, 0xffffff);
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
            float mineSpeed = tags.getInteger("MiningSpeed") / 100f;
            float mineSpeed2 = tags.getInteger("MiningSpeed2") / 100f;
            float stoneboundSpeed = (float) Math.log(durability / 90f + 1) * 2 * stonebound;
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            float trueSpeed = mineSpeed + stoneboundSpeed;
            float trueSpeed2 = mineSpeed + stoneboundSpeed;

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
            fontRendererObj.drawString("- " + getHarvestLevelName(tags.getInteger("HarvestLevel")) + ", " + getHarvestLevelName(tags.getInteger("HarvestLevel2")), x, base + offset * 10,
                    0xffffff);
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

            float stoneboundSpeed = (float) Math.log(durability / 90f + 1) * 2 * stonebound;
            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);
            trueSpeed += stoneboundSpeed;
            if (trueSpeed < 0)
                trueSpeed = 0;
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation14") + df.format(trueSpeed), x, base + offset * 10, 0xffffff);
            offset++;
            if (stoneboundSpeed != 0)
            {
                String bloss = stoneboundSpeed > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                fontRendererObj.drawString(bloss + df.format(stoneboundSpeed), x, base + offset * 10, 0xffffff);
                offset++;
            }
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation15") + getHarvestLevelName(tags.getInteger("HarvestLevel")), x, base + offset * 10, 0xffffff);
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

    protected String getHarvestLevelName (int num)
    {
        switch (num)
        {
        case 0:
            return StatCollector.translateToLocal("gui.partcrafter.mining1");
        case 1:
            return StatCollector.translateToLocal("gui.partcrafter.mining2");
        case 2:
            return StatCollector.translateToLocal("gui.partcrafter.mining3");
        case 3:
            return StatCollector.translateToLocal("gui.partcrafter.mining4");
        case 4:
            return StatCollector.translateToLocal("gui.partcrafter.mining5");
        case 5:
            return StatCollector.translateToLocal("gui.partcrafter.mining6");
        default:
            return String.valueOf(num);
        }
    }

    void drawToolInformation ()
    {
        this.drawCenteredString(fontRendererObj, title, this.guiROffset + 63, 8, 0xffffff);
        fontRendererObj.drawSplitString(body, this.guiROffset + 8, 24, 115, 0xffffff);
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
        this.drawTexturedModalRect(this.guiLPos, this.guiTop, 0, 0, 175, 166);

        this.mc.getTextureManager().bindTexture(icons);
        // Draw the slots

        if (logic.tinkerTable && !logic.isStackInSlot(5))
            this.drawTexturedModalRect(this.guiLPos + 47, this.guiTop + 33, 0, 233, 18, 18);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);

        // Draw chest side
        if (logic.chest != null)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(chest);
            if (logic.doubleChest == null)
                this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 121, 115);
            else
                this.drawTexturedModalRect(this.guiLeft, this.guiTop, 125, 0, 121, 187);
        }

        // Draw description
        if (logic.tinkerTable)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(description);
            this.drawTexturedModalRect(this.guiRPos, this.guiTop, 0, 0, 126, 171);
        }

    }
}
