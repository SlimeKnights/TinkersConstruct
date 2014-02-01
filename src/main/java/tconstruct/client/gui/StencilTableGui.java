package tconstruct.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import tconstruct.TConstruct;
import tconstruct.blocks.logic.StencilTableLogic;
import tconstruct.common.TRepo;
import tconstruct.inventory.PatternShaperContainer;
import tconstruct.util.network.packet.PacketStencilTable;

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

    public void onGuiClosed ()
    {
        super.onGuiClosed();
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        field_146289_q.drawString(StatCollector.translateToLocal("crafters.PatternShaper"), 50, 6, 0x404040);
        field_146289_q.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/patternshaper.png");

    protected void drawGuiContainerBackgroundLayer (float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146297_k.getTextureManager().bindTexture(background);
        int cornerX = (this.field_146294_l - this.xSize) / 2;
        int cornerY = (this.field_146295_m - this.ySize) / 2;
        this.drawTexturedModalRect(cornerX, cornerY, 0, 0, this.xSize, this.ySize);
        if (!logic.isStackInSlot(0))
        {
            this.drawTexturedModalRect(cornerX + 47, cornerY + 34, 176, 0, 18, 18);
        }
    }

    public void initGui ()
    {
        super.initGui();
        int cornerX = (this.field_146294_l - this.xSize) / 2;
        int cornerY = (this.field_146295_m - this.ySize) / 2;

        this.field_146292_n.clear();
        /*ToolGuiElement repair = TConstruct.toolButtons.get(0);
        GuiButtonTool repairButton = new GuiButtonTool(0, cornerX - 110, cornerY, repair.buttonIconX, repair.buttonIconY, repair.texture); // Repair
        repairButton.enabled = false;
        this.field_146292_n.add(repairButton);*/
        this.field_146292_n.add(new GuiButton(0, cornerX - 120, cornerY, 120, 20, StatCollector.translateToLocal("gui.stenciltable1")));
        this.field_146292_n.add(new GuiButton(1, cornerX - 120, cornerY + 20, 120, 20, StatCollector.translateToLocal("gui.stenciltable1")));

        //for (int iter = 0; iter < TConstructContent.patternOutputs.length; iter++)
        //{

        /*ToolGuiElement element = TConstruct.toolButtons.get(iter);
        GuiButtonTool button = new GuiButtonTool(iter, cornerX - 110 + 22 * (iter % 5), cornerY + 22 * (iter / 5), element.buttonIconX, element.buttonIconY, element.texture); // Repair
        this.field_146292_n.add(button);*/
        //}
    }

    protected void actionPerformed (GuiButton button)
    {
        ItemStack pattern = logic.getStackInSlot(0);
        if (pattern != null && pattern.getItem() == TRepo.blankPattern)
        {
            int meta = pattern.getItemDamage();
            if (meta == 0)
            {
                if (button.field_146127_k == 0)
                {
                    patternIndex++;
                    if (patternIndex == 21)
                        patternIndex++;
                    if (patternIndex >= TRepo.patternOutputs.length - 1)
                        patternIndex = 0;
                }
                else if (button.field_146127_k == 1)
                {
                    patternIndex--;
                    if (patternIndex < 0)
                        patternIndex = TRepo.patternOutputs.length - 2;
                    if (patternIndex == 21)
                        patternIndex--;
                }
                ItemStack stack = new ItemStack(TRepo.woodPattern, 1, patternIndex + 1);
                logic.setInventorySlotContents(1, stack);
                updateServer(stack);
            }
            /*else if (meta == 1 || meta == 2)
            {
                ItemStack stack = new ItemStack(TContent.metalPattern, 1, 0);
                logic.setInventorySlotContents(1, stack);
                updateServer(stack);
            }*/
        }
    }

    void updateServer (ItemStack stack)
    {
        /*ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try
        {
            outputStream.writeByte(2);
            outputStream.writeInt(logic.getWorld().provider.dimensionId);
            outputStream.writeInt(logic.field_145851_c);
            outputStream.writeInt(logic.field_145848_d);
            outputStream.writeInt(logic.field_145849_e);
            outputStream.writeShort(stack.itemID);
            outputStream.writeShort(stack.getItemDamage());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "TConstruct";
        packet.data = bos.toByteArray();
        packet.length = bos.size();

        PacketDispatcher.sendPacketToServer(packet);*/
        
        TConstruct.packetPipeline.sendToServer(new PacketStencilTable(logic.field_145851_c, logic.field_145848_d, logic.field_145849_e, stack));
    }
}
