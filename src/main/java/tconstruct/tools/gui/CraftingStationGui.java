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

import codechicken.nei.VisiblityData;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.TaggedInventoryArea;
import cpw.mods.fml.common.Optional;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.accessory.AccessoryCore;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.crafting.PatternBuilder;
import tconstruct.library.tools.*;
import tconstruct.library.util.HarvestLevels;
import tconstruct.tools.logic.CraftingStationLogic;

@Optional.Interface(iface = "codechicken.nei.api.INEIGuiHandler", modid = "NotEnoughItems")
public class CraftingStationGui extends GuiContainer implements INEIGuiHandler
{
    public boolean active;
    public String toolName;
    public GuiTextField text;
    public String title, body = "";
    CraftingStationLogic logic;

    boolean hasMaterial;
    boolean hasTool;
    boolean hasArmor;
    boolean hasAccessory;
    ItemStack centerStack;
    ToolMaterial materialEnum;
    String centerTitle;
    NBTTagCompound tags;
    
    public static final int CHEST_WIDTH = 116;

    // Panel positions

    private int craftingLeft = 0;
    private int craftingTop = 0;
    private int craftingTextLeft = 0;

    private int descLeft = 0;
    private int descTop = 0;
    private int descTextLeft = 0;

    private int chestLeft = 0;
    private int chestTop = 0;

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

        if (logic.tinkerTable)
        {
            this.descLeft = this.guiLeft + 176;
            this.descTop = this.craftingTop;
        }

        if (logic.chest != null)
        {
            this.xSize += CHEST_WIDTH;
            this.guiLeft -= CHEST_WIDTH;
            this.chestLeft = this.guiLeft;
            this.chestTop = this.craftingTop;
            if (logic.doubleChest != null)
                this.ySize = 187;
        }

        this.craftingTextLeft = this.craftingLeft - this.guiLeft;
        this.descTextLeft = this.descLeft - this.guiLeft;
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        if (logic.chest != null)
        {
            this.fontRendererObj.drawString(StatCollector.translateToLocal(logic.chest.get().getInventoryName()), 8, 6, 0x202020);
        }

        this.fontRendererObj.drawString(StatCollector.translateToLocal(logic.tinkerTable ? "crafters.TinkerTable" : logic.getInvName()), craftingTextLeft + 8, 6, 0x202020);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), craftingTextLeft + 8, 72, 0x202020);

        // this.fontRendererObj.drawString(toolName + "_", this.xSize / 2 - 18,
        // 8, 0xffffff);

        if (logic.tinkerTable)
        {
            if (logic.isStackInSlot(0)) // output slot = modified item
                drawToolStats(logic.getStackInSlot(0));
            else if (logic.isStackInSlot(5)) // center slot if no output item
            {
                // other slots empty?
                if(!logic.isStackInSlot(1) && !logic.isStackInSlot(2) && !logic.isStackInSlot(3) && !logic.isStackInSlot(4)
                && !logic.isStackInSlot(6) && !logic.isStackInSlot(7) && !logic.isStackInSlot(8) && !logic.isStackInSlot(9))
                    drawToolStats(logic.getStackInSlot(5));
                else
                    drawToolInformation();
            }
            else
                drawToolInformation();
        }
    }

    void drawToolStats (ItemStack stack)
    {
        int offsetX = descTextLeft + 63;
        int descX = descTextLeft + 10;

        if (centerStack != stack)
        {
            centerStack = stack;
            hasAccessory = hasArmor = hasMaterial = hasTool = false;

            int matID = PatternBuilder.instance.getPartID(stack);

            if (matID != Short.MAX_VALUE)
            {
                materialEnum = TConstructRegistry.getMaterial(matID);
                hasMaterial = true;
                centerTitle = "\u00A7n" + materialEnum.localizedName();
            }
            else if (stack.getItem() instanceof ToolCore)
            {
                ToolCore tool = (ToolCore) stack.getItem();
                tags = stack.getTagCompound().getCompoundTag(tool.getBaseTagName());
                hasTool = true;
                centerTitle = "\u00A7n" + tool.getLocalizedToolName();
            }
            else if (stack.getItem() instanceof ArmorCore)
            {
                ArmorCore armor = (ArmorCore) stack.getItem();
                tags = stack.getTagCompound().getCompoundTag(armor.getBaseTagName());
                hasArmor = true;
                centerTitle = "\u00A7n" + stack.getDisplayName(); // todo: localize
            }
            else if (stack.getItem() instanceof AccessoryCore)
            {
                AccessoryCore accessory = (AccessoryCore) stack.getItem();
                tags = stack.getTagCompound().getCompoundTag(accessory.getBaseTagName());
                hasAccessory = true;
                centerTitle = "\u00A7n" + stack.getDisplayName(); // todo: localize
            }
        }

        if (hasAccessory || hasArmor || hasMaterial || hasTool)
        {
            this.drawCenteredString(fontRendererObj, centerTitle, offsetX, 8, 0xffffff);

            if (hasTool)
            {
                drawModularToolStats();
            }
            else if (hasArmor)
            {
                drawModularArmorStats();
            }
            else if (hasAccessory)
            {
                drawModularAccessoryStats();
            }
            else if (hasMaterial)
            {
                drawMaterialStats();
            }
        }
        else
        {
            drawToolInformation();
        }
    }

    protected void drawModularToolStats ()
    {
        ToolCore tool = (ToolCore)centerStack.getItem();

        List categories = Arrays.asList(tool.getTraits());
        final int durability = tags.getInteger("Damage");
        final int maxDur = tags.getInteger("TotalDurability");
        int availableDurability = maxDur - durability;

        // Durability
        final int baseX = descTextLeft + 10;
        final int baseY = 24;
        int offset = 0;
        if (maxDur > 0)
        {
            if (maxDur >= 10000)
            {
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation1"), baseX, baseY + offset * 11, 0xffffff);
                offset++;
                fontRendererObj.drawString("- " + availableDurability + "/" + maxDur, baseX, baseY + offset * 10, 0xffffff);
                offset++;
            }
            else
            {
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation2") + availableDurability + "/" + maxDur, baseX, baseY + offset * 10, 0xffffff);
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
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation3") + attack / 2 + heart, baseX, baseY + offset * 10, 0xffffff);
            else
                this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation3") + attack / 2f + heart, baseX, baseY + offset * 10, 0xffffff);
            offset++;

            if (stoneboundDamage != 0)
            {
                DecimalFormat df = new DecimalFormat("##.##");
                heart = stoneboundDamage == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
                String bloss = stoneboundDamage > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                this.fontRendererObj.drawString(bloss + df.format(stoneboundDamage / 2f) + heart, baseX, baseY + offset * 10, 0xffffff);
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
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation6") + df.format(trueDraw) + "s", baseX, baseY + offset * 10, 0xffffff);
            offset++;
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation7") + df.format(flightSpeed) + "x", baseX, baseY + offset * 10, 0xffffff);
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

            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation10"), baseX, baseY + offset * 10, 0xffffff);
            offset++;
            String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
            if (attack % 2 == 0)
                this.fontRendererObj.drawString("- " + attack / 2 + heart, baseX, baseY + offset * 10, 0xffffff);
            else
                this.fontRendererObj.drawString("- " + attack / 2f + heart, baseX, baseY + offset * 10, 0xffffff);
            offset++;
            int minAttack = attack;
            int maxAttack = attack * 2;
            heart = StatCollector.translateToLocal("gui.partcrafter9");
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation11"), baseX, baseY + offset * 10, 0xffffff);
            offset++;
            this.fontRendererObj.drawString(df.format(minAttack / 2f) + "-" + df.format(maxAttack / 2f) + heart, baseX, baseY + offset * 10, 0xffffff);
            offset++;
            offset++;

            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation8") + df.format(mass), baseX, baseY + offset * 10, 0xffffff);
            offset++;
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation9") + df.format(accuracy - 4) + "%", baseX, baseY + offset * 10, 0xffffff);
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

            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation12"), baseX, baseY + offset * 10, 0xffffff);
            offset++;
            fontRendererObj.drawString("- " + df.format(trueSpeed) + ", " + df.format(trueSpeed2), baseX, baseY + offset * 10, 0xffffff);
            offset++;
            if (stoneboundSpeed != 0)
            {
                String bloss = stoneboundSpeed > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                fontRendererObj.drawString(bloss + df.format(stoneboundSpeed), baseX, baseY + offset * 10, 0xffffff);
                offset++;
            }
            offset++;
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation13"), baseX, baseY + offset * 10, 0xffffff);
            offset++;
            fontRendererObj.drawString("- " + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel")) + ", " + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel2")), baseX, baseY + offset * 10, 0xffffff);
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
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation14") + df.format(trueSpeed), baseX, baseY + offset * 10, 0xffffff);
            offset++;
            if (stoneboundSpeed != 0 && !Float.isNaN(stoneboundSpeed))
            {
                String bloss = stoneboundSpeed > 0 ? StatCollector.translateToLocal("gui.toolstation4") : StatCollector.translateToLocal("gui.toolstation5");
                fontRendererObj.drawString(bloss + df.format(stoneboundSpeed), baseX, baseY + offset * 10, 0xffffff);
                offset++;
            }
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation15") + HarvestLevels.getHarvestLevelName(tags.getInteger("HarvestLevel")), baseX, baseY + offset * 10, 0xffffff);
            offset++;
            offset++;
        }
        else if (categories.contains("utility"))
        {
            float mineSpeed = tags.getInteger("MiningSpeed");
            float trueSpeed = mineSpeed / (100f);
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation16") + trueSpeed, baseX, baseY + offset * 10, 0xffffff);
            offset++;
            offset++;
        }

        int modifiers = tags.getInteger("Modifiers");
        if (modifiers > 0)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), baseX, baseY + offset * 10, 0xffffff);
            offset++;
        }
        if (tags.hasKey("Tooltip1"))
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation17"), baseX, baseY + offset * 10, 0xffffff);
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
                fontRendererObj.drawString("- " + tipName, baseX, baseY + (offset + tipNum) * 10, 0xffffff);
            }
            else
                displayToolTips = false;
        }
    }

    private static DecimalFormat df =  new DecimalFormat("##.#");

    // todo: do this properly, quick and dirty fix
    protected void drawModularArmorStats ()
    {
        ArmorCore armor = (ArmorCore)centerStack.getItem();
        List categories = Arrays.asList(armor.getTraits());
        final int baseX = descTextLeft + 10;
        final int baseY = 24;
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
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation1"), baseX, baseY + offset * 11, 0xffffff);
                offset++;
                fontRendererObj.drawString("- " + availableDurability + "/" + maxDur, baseX, baseY + offset * 10, 0xffffff);
                offset++;
            }
            else
            {
                fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation2") + availableDurability + "/" + maxDur, baseX, baseY + offset * 10, 0xffffff);
                offset++;
            }
        }
        // Damage reduction
        double damageReduction = tags.getDouble("DamageReduction");
        if(damageReduction > 0.000001d)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation19") + df.format(damageReduction), baseX, baseY + offset * 10, 0xffffff);
            offset++;
        }

        // Protection
        double protection = armor.getProtection(centerStack);
        double maxProtection = tags.getDouble("MaxDefense");
        //if(maxProtection > protection)
        fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation20") + df.format(protection) + "/" + df.format(maxProtection), baseX, baseY + offset * 10, 0xffffff);
        //else
        //  fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation20") + df.format(protection), x, base + offset * 10, 0xffffff);
        offset++;

        offset++;
        int modifiers = tags.getInteger("Modifiers");
        if (modifiers > 0)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), baseX, baseY + offset * 10, 0xffffff);
            offset++;
        }
        if (tags.hasKey("Tooltip1"))
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation17"), baseX, baseY + offset * 10, 0xffffff);
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
                fontRendererObj.drawString("- " + tipName, baseX, baseY + (offset + tipNum) * 10, 0xffffff);
            }
            else
                displayToolTips = false;
        }
    }

    // todo: also quick and dirty fix
    protected void drawModularAccessoryStats ()
    {
        AccessoryCore accessory = (AccessoryCore)centerStack.getItem();
        List categories = Arrays.asList(accessory.getTraits());
        final int baseX = descTextLeft + 10;
        final int baseY = 24;
        int offset = 0;

        if (categories.contains("utility"))
        {
            float mineSpeed = tags.getInteger("MiningSpeed");
            float trueSpeed = mineSpeed / (100f);
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation16") + trueSpeed, baseX, baseY + offset * 10, 0xffffff);
            offset++;
        }

        offset++;
        int modifiers = tags.getInteger("Modifiers");
        if (modifiers > 0)
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation18") + tags.getInteger("Modifiers"), baseX, baseY + offset * 10, 0xffffff);
            offset++;
        }
        if (tags.hasKey("Tooltip1"))
        {
            fontRendererObj.drawString(StatCollector.translateToLocal("gui.toolstation17"), baseX, baseY + offset * 10, 0xffffff);
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
                fontRendererObj.drawString("- " + tipName, baseX, baseY + (offset + tipNum) * 10, 0xffffff);
            }
            else
                displayToolTips = false;
        }
    }
    
    protected void drawMaterialStats()
    {
        final int baseX = descTextLeft + 8;
        final int baseY = 24;

        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter4") + materialEnum.durability(), baseX, baseY + 16, 16777215);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter5") + materialEnum.handleDurability() + "x", baseX, baseY + 27, 16777215);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter6") + materialEnum.toolSpeed() / 100f, baseX, baseY + 38, 16777215);
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter7") + HarvestLevels.getHarvestLevelName(materialEnum.harvestLevel()), baseX, baseY + 49, 16777215);

        int attack = materialEnum.attack();
        String heart = attack == 2 ? StatCollector.translateToLocal("gui.partcrafter8") : StatCollector.translateToLocal("gui.partcrafter9");
        if (attack % 2 == 0)
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter10") + attack / 2 + heart, baseX, baseY + 60, 0xffffff);
        else
            this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.partcrafter10") + attack / 2f + heart, baseX, baseY + 60, 0xffffff);
    }

    void drawToolInformation ()
    {
        int offsetX = descTextLeft + 63;
        
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

        if (x + 4 > guiLeft + xSize + (logic.tinkerTable ? 126 : 0))
            return false;

        return true;
    }
    
    public boolean hasChest()
    {
        return logic.chest != null;
    }

}
