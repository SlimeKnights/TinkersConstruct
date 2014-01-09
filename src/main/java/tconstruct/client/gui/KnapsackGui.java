package tconstruct.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import tconstruct.client.tabs.InventoryTabKnapsack;
import tconstruct.client.tabs.TabRegistry;
import tconstruct.inventory.KnapsackContainer;
import tconstruct.util.player.KnapsackInventory;

public class KnapsackGui extends GuiContainer
{
    public InventoryPlayer inv;
    public KnapsackInventory stats;

    public KnapsackGui(InventoryPlayer inventoryplayer, KnapsackInventory holder)
    {
        super(new KnapsackContainer(inventoryplayer, holder));
        inv = inventoryplayer;
        stats = holder;
    }

    public void initGui ()
    {
        super.initGui();

        int cornerX = guiLeft;
        int cornerY = (this.field_146294_l - this.ySize) / 2;
        this.field_146292_n.clear();

        TabRegistry.updateTabValues(cornerX, cornerY, InventoryTabKnapsack.class);
        TabRegistry.addTabsToList(this.field_146292_n);

        //        InventoryTab tab = new InventoryTab(2, cornerX, cornerY - 28, new ItemStack(Block.workbench), 0);
        //        this.field_146292_n.add(tab);
        //        tab = new InventoryTab(3, cornerX + 28, cornerY - 28, new ItemStack(Item.plateDiamond), 1);
        //        this.field_146292_n.add(tab);
        //        tab = new InventoryTab(4, cornerX + 56, cornerY - 28, new ItemStack(TContent.knapsack), 1);
        //        tab.enabled = false;
        //        this.field_146292_n.add(tab);
    }

    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        field_146289_q.drawString(StatCollector.translateToLocal("inventory.knapsack"), 8, 6, 0x404040);
        field_146289_q.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 4, 0x404040);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/knapsack.png");

    protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146297_k.getTextureManager().bindTexture(background);
        int cornerX = guiLeft;
        int cornerY = (field_146294_l - ySize) / 2;
        drawTexturedModalRect(cornerX, cornerY, 0, 0, xSize, ySize);
    }

    /*protected void keyTyped(char par1, int par2)
    {
        if (par2 == TControls.armorKey.keyCode)
        {
            this.mc.thePlayer.closeScreen();
        }

        super.keyTyped(par1, par2);
    }*/
}
