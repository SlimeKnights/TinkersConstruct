package tconstruct.tools.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import tconstruct.TConstruct;
import tconstruct.library.crafting.StencilBuilder;
import tconstruct.tools.TinkerTools;
import tconstruct.tools.inventory.PatternShaperContainer;
import tconstruct.tools.logic.StencilTableLogic;
import tconstruct.util.network.PatternTablePacket;

public class StencilTableGui extends GuiContainer
{
    StencilTableLogic logic;
    int patternIndex;

    public StencilTableGui(InventoryPlayer inventoryplayer, StencilTableLogic shaper, World world, int x, int y, int z)
    {
        super(new PatternShaperContainer(inventoryplayer, shaper));
        logic = shaper;
        patternIndex = 0;
    }

    @Override
    public void onGuiClosed ()
    {
        super.onGuiClosed();
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        fontRendererObj.drawString(StatCollector.translateToLocal("crafters.PatternShaper"), 50, 6, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/patternshaper.png");

    @Override
    protected void drawGuiContainerBackgroundLayer (float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(background);
        int cornerX = (this.width - this.xSize) / 2;
        int cornerY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);
        if (!logic.isStackInSlot(0))
        {
            this.drawTexturedModalRect(cornerX + 47, cornerY + 34, 176, 0, 18, 18);
        }
    }

    @Override
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
        this.buttonList.add(new GuiButton(0, cornerX - 120, cornerY, 120, 20, (StatCollector.translateToLocal("gui.stenciltable1"))));
        this.buttonList.add(new GuiButton(1, cornerX - 120, cornerY + 20, 120, 20, (StatCollector.translateToLocal("gui.stenciltable2"))));

        //for (int iter = 0; iter < TConstructContent.patternOutputs.length; iter++)
        //{

        /*ToolGuiElement element = TConstruct.toolButtons.get(iter);
        GuiButtonTool button = new GuiButtonTool(iter, cornerX - 110 + 22 * (iter % 5), cornerY + 22 * (iter / 5), element.buttonIconX, element.buttonIconY, element.texture); // Repair
        this.buttonList.add(button);*/
        //}
    }

    @Override
    protected void actionPerformed (GuiButton button)
    {
        ItemStack pattern = logic.getStackInSlot(0);
        if (pattern != null && StencilBuilder.isBlank(pattern))
        {
            if (button.id == 0)
            {
                patternIndex++;
                if (patternIndex >= StencilBuilder.getStencilCount() - 1)
                    patternIndex = 0;
            }
            else if (button.id == 1)
            {
                patternIndex--;
                if (patternIndex < 0)
                    patternIndex = StencilBuilder.getStencilCount() - 2;
            }

            ItemStack stack = StencilBuilder.getStencil(patternIndex);
            logic.setInventorySlotContents(1, stack);
            updateServer(stack);
        }
    }

    void updateServer (ItemStack stack)
    {

        TConstruct.packetPipeline.sendToServer(new PatternTablePacket(logic.xCoord, logic.yCoord, logic.zCoord, stack));
    }
}