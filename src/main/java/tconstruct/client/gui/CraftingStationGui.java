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
import tconstruct.library.tools.HarvestTool;
import tconstruct.library.tools.ToolCore;

public class CraftingStationGui extends GuiContainer
{
    public boolean active;
    public String toolName;
    public GuiTextField text;
    public String title, body = "";
    CraftingStationLogic logic;

    public CraftingStationGui(InventoryPlayer inventory, CraftingStationLogic logic, World world, int x, int y, int z)
    {
        super(logic.getGuiContainer(inventory, world, x, y, z));
        this.logic = logic;
        //text = new GuiTextField(this.fontRenderer, this.xSize / 2 - 5, 8, 30, 12);
        //this.text.setText("");
        title = "\u00A7n" + StatCollector.translateToLocal("gui.toolforge1");
        body = StatCollector.translateToLocal("gui.toolforge2");
        toolName = "";
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        this.fontRenderer.drawString(StatCollector.translateToLocal(logic.tinkerTable ? "crafters.TinkerTable" : logic.getInvName()), 8, 6, 0x202020);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 0x202020);
        if (logic.chest != null)
            this.fontRenderer.drawString(StatCollector.translateToLocal(logic.chest.get().getInvName()), -108, this.ySize - 160, 0x202020);
        //this.fontRenderer.drawString(toolName + "_", this.xSize / 2 - 18, 8, 0xffffff);

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
        ItemStack stack = logic.getStackInSlot(0);
        if (stack.getItem() instanceof ToolCore)
        {
            ToolCore tool = (ToolCore) stack.getItem();
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            this.drawCenteredString(fontRenderer, "\u00A7n" + tool.getToolName(), xSize + 63, 8, 0xffffff);

            drawModularToolStats(stack, tool, tags);
        }
        else if (stack.getItem() instanceof ArmorCore)
        {
            ArmorCore armor = (ArmorCore) stack.getItem();
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("TinkerArmor");
            this.drawCenteredString(fontRenderer, "\u00A7n" + StatCollector.translateToLocal("armortype." + armor.getArmorName()), xSize + 63, 8, 0xffffff);

            drawModularArmorStats(stack, armor, tags);
        }
    }

    void drawModularArmorStats (ItemStack stack, ArmorCore tool, NBTTagCompound tags)
    {
        int modifiers = tags.getInteger("Modifiers");
        int base = 24;
        int offset = 0;
        if (modifiers > 0)
        {
            fontRenderer.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), xSize + 8, base + offset * 10, 0xffffff);
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
                fontRenderer.drawString("- " + tipName, xSize + 8, base + (offset + tipNum) * 10, 0xffffff);
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
                System.out.println("Stonebound: " + stoneboundSpeed);
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
        this.drawCenteredString(fontRenderer, title, xSize + 63, 8, 0xffffff);
        fontRenderer.drawSplitString(body, xSize + 8, 24, 115, 0xffffff);
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
        int cornerX = (this.width - this.xSize) / 2;
        int cornerY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);

        if (active)
        {
            this.drawTexturedModalRect(cornerX + 62, cornerY, 0, this.ySize, 112, 22);
        }

        this.mc.getTextureManager().bindTexture(icons);
        // Draw the slots

        if (logic.tinkerTable && !logic.isStackInSlot(5))
            this.drawTexturedModalRect(cornerX + 47, cornerY + 33, 0, 233, 18, 18);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(icons);

        //Draw chest side
        if (logic.chest != null)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(chest);
            if (logic.doubleChest == null)
                this.drawTexturedModalRect(cornerX - 116, cornerY, 0, 0, 121, this.ySize);
            else
                this.drawTexturedModalRect(cornerX - 116, cornerY, 125, 0, 122, this.ySize + 21);
        }

        // Draw description
        if (logic.tinkerTable)
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(description);
            cornerX = (this.width + this.xSize) / 2;
            cornerY = (this.height - this.ySize) / 2;
            this.drawTexturedModalRect(cornerX, cornerY, 0, 0, 126, this.ySize + 30);
        }

    }

}
