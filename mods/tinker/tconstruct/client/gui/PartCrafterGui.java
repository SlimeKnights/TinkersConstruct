package mods.tinker.tconstruct.client.gui;

import mods.tinker.tconstruct.blocks.logic.PartCrafterLogic;
import mods.tinker.tconstruct.inventory.ActiveContainer;
import mods.tinker.tconstruct.inventory.PartCrafterChestContainer;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.ToolMaterial;
import mods.tinker.tconstruct.library.crafting.PatternBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class PartCrafterGui extends NewContainerGui
{
    PartCrafterLogic logic;
    String title, otherTitle = "";
    boolean drawChestPart;
    boolean hasTop, hasBottom;
    ItemStack topMaterial, bottomMaterial;
    ToolMaterial topEnum, bottomEnum;

    public PartCrafterGui(InventoryPlayer inventoryplayer, PartCrafterLogic partlogic, World world, int x, int y, int z)
    {
        super((ActiveContainer) partlogic.getGuiContainer(inventoryplayer, world, x, y, z));
        logic = partlogic;
        drawChestPart = container instanceof PartCrafterChestContainer;

        title = "\u00A7nTool Part Crafting";
    }

    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        this.fontRenderer.drawString(StatCollector.translateToLocal("crafters.PartBuilder"), 6, 6, 4210752);
        this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
        if (drawChestPart)
            this.fontRenderer.drawString(StatCollector.translateToLocal("inventory.PatternChest"), -108, this.ySize - 148, 4210752);

        drawMaterialInformation();
    }

    void drawDefaultInformation ()
    {
        title = "\u00A7nTool Part Building";
        this.drawCenteredString(fontRenderer, title, xSize + 63, 8, 16777215);
        fontRenderer.drawSplitString("Place a pattern and a material on the left to get started.", xSize + 8, 24, 115, 16777215);
    }

    void drawMaterialInformation ()
    {
        ItemStack top = logic.getStackInSlot(2);
        ItemStack bottom = logic.getStackInSlot(3);
        if (topMaterial != top)
        {
            topMaterial = top;
            int topID = PatternBuilder.instance.getPartID(top);

            if (topID != Short.MAX_VALUE)
            {
                topEnum = TConstructRegistry.getMaterial(topID);
                hasTop = true;
                title = "\u00A7n" + topEnum.name();
            }
            else
                hasTop = false;
        }

        if (bottomMaterial != bottom)
        {
            bottomMaterial = bottom;
            int bottomID = PatternBuilder.instance.getPartID(bottom);

            if (bottomID != Short.MAX_VALUE)
            {
                bottomEnum = TConstructRegistry.getMaterial(bottomID);
                hasBottom = true;
                otherTitle = "\u00A7n" + bottomEnum.name();
            }
            else
                hasBottom = false;
        }

        int offset = 8;
        if (hasTop)
        {
            this.drawCenteredString(fontRenderer, title, xSize + 63, offset, 16777215);
            this.fontRenderer.drawString("Base Durability: " + topEnum.durability(), xSize + 8, offset + 16, 16777215);
            this.fontRenderer.drawString("Handle Modifier: " + topEnum.handleDurability() + "x", xSize + 8, offset + 27, 16777215);
            this.fontRenderer.drawString("Mining Speed: " + topEnum.toolSpeed() / 100f, xSize + 8, offset + 38, 16777215);
            this.fontRenderer.drawString("Mining Level: " + getHarvestLevelName(topEnum.harvestLevel()), xSize + 8, offset + 49, 16777215);
            this.fontRenderer.drawString("Base Attack: " + topEnum.attack(), xSize + 8, offset + 60, 16777215);
        }

        offset = 90;
        if (hasBottom)
        {
            this.drawCenteredString(fontRenderer, otherTitle, xSize + 63, offset, 16777215);
            this.fontRenderer.drawString("Base Durability: " + bottomEnum.durability(), xSize + 8, offset + 16, 16777215);
            this.fontRenderer.drawString("Handle Modifier: " + bottomEnum.handleDurability() + "x", xSize + 8, offset + 27, 16777215);
            this.fontRenderer.drawString("Mining Speed: " + bottomEnum.toolSpeed() / 100f, xSize + 8, offset + 38, 16777215);
            this.fontRenderer.drawString("Mining Level: " + getHarvestLevelName(bottomEnum.harvestLevel()), xSize + 8, offset + 49, 16777215);
            this.fontRenderer.drawString("Base Attack: " + bottomEnum.attack(), xSize + 8, offset + 60, 16777215);
        }

        if (!hasTop && !hasBottom)
            drawDefaultInformation();
    }

    public static String getHarvestLevelName (int num)
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
            return "Obsidian";
        case 4:
            return "Cobalt";
        case 5:
            return "Manyullyn";
        default:
            return String.valueOf(num);
        }
    }

    protected void drawGuiContainerBackgroundLayer (float par1, int par2, int par3)
    {
        // Draw the background
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/toolparts.png");
        int cornerX = (this.width - this.xSize) / 2;
        int cornerY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);

        // Draw Slots
        this.drawTexturedModalRect(cornerX + 39, cornerY + 26, 0, 166, 98, 36);
        if (!logic.isStackInSlot(0))
        {
            this.drawTexturedModalRect(cornerX + 39, cornerY + 26, 176, 0, 18, 18);
        }
        if (!logic.isStackInSlot(2))
        {
            this.drawTexturedModalRect(cornerX + 57, cornerY + 26, 176, 18, 18, 18);
        }
        if (!logic.isStackInSlot(1))
        {
            this.drawTexturedModalRect(cornerX + 39, cornerY + 44, 176, 0, 18, 18);
        }
        if (!logic.isStackInSlot(3))
        {
            this.drawTexturedModalRect(cornerX + 57, cornerY + 44, 176, 36, 18, 18);
        }

        // Draw chest
        if (drawChestPart)
        {
            //texID = this.mc.renderEngine.getTexture("/mods/tinker/textures/gui/patternchestmini.png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            //his.mc.renderEngine.bindTexture(texID);
            this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/patternchestmini.png");
            this.drawTexturedModalRect(cornerX - 116, cornerY + 11, 0, 0, this.xSize, this.ySize);
        }

        // Draw description
        //texID = this.mc.renderEngine.getTexture("/mods/tinker/textures/gui/description.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //this.mc.renderEngine.bindTexture(texID);
        this.mc.renderEngine.bindTexture("/mods/tinker/textures/gui/description.png");
        cornerX = (this.width + this.xSize) / 2;
        cornerY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(cornerX, cornerY, 126, 0, 126, this.ySize);
    }

    @Override
    protected void mouseClicked (int mouseX, int mouseY, int clickNum)
    {
        boolean flag = clickNum == this.mc.gameSettings.keyBindPickBlock.keyCode + 100;
        Slot slot = this.getSlotAtPosition(mouseX, mouseY);
        long l = Minecraft.getSystemTime();
        this.field_94074_J = this.field_94072_H == slot && l - this.field_94070_G < 250L && this.field_94073_I == clickNum;
        this.field_94068_E = false;

        int offsetLeft = drawChestPart ? 108 : 0;

        if (clickNum == 0 || clickNum == 1 || flag)
        {
            int i1 = this.guiLeft;
            int j1 = this.guiTop;
            boolean flag1 = mouseX < i1 - offsetLeft || mouseY < j1 || mouseX >= i1 + this.xSize || mouseY >= j1 + this.ySize;
            int k1 = -1;

            if (slot != null)
            {
                k1 = slot.slotNumber;
            }

            if (flag1)
            {
                k1 = -999;
            }

            if (this.mc.gameSettings.touchscreen && flag1 && this.mc.thePlayer.inventory.getItemStack() == null)
            {
                this.mc.displayGuiScreen((GuiScreen) null);
                return;
            }

            if (k1 != -1)
            {
                if (this.mc.gameSettings.touchscreen)
                {
                    if (slot != null && slot.getHasStack())
                    {
                        this.clickedSlot = slot;
                        this.draggedStack = null;
                        this.isRightMouseClick = clickNum == 1;
                    }
                    else
                    {
                        this.clickedSlot = null;
                    }
                }
                else if (!this.field_94076_q)
                {
                    if (this.mc.thePlayer.inventory.getItemStack() == null)
                    {
                        if (clickNum == this.mc.gameSettings.keyBindPickBlock.keyCode + 100)
                        {
                            this.handleMouseClick(slot, k1, clickNum, 3);
                        }
                        else
                        {
                            boolean flag2 = k1 != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                            byte b0 = 0;

                            if (flag2)
                            {
                                this.field_94075_K = slot != null && slot.getHasStack() ? slot.getStack() : null;
                                b0 = 1;
                            }
                            else if (k1 == -999)
                            {
                                b0 = 4;
                            }

                            this.handleMouseClick(slot, k1, clickNum, b0);
                        }

                        this.field_94068_E = true;
                    }
                    else
                    {
                        this.field_94076_q = true;
                        this.field_94067_D = clickNum;
                        this.field_94077_p.clear();

                        if (clickNum == 0)
                        {
                            this.field_94071_C = 0;
                        }
                        else if (clickNum == 1)
                        {
                            this.field_94071_C = 1;
                        }
                    }
                }
            }
        }

        this.field_94072_H = slot;
        this.field_94070_G = l;
        this.field_94073_I = clickNum;
    }
}
