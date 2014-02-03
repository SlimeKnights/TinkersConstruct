package tconstruct.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import tconstruct.blocks.logic.PatternChestLogic;

public class PatternChestGui extends GuiContainer
{
    public PatternChestLogic logic;

    public PatternChestGui(InventoryPlayer inventoryplayer, PatternChestLogic holder, World world, int x, int y, int z)
    {
        super(holder.getGuiContainer(inventoryplayer, world, x, y, z));
        logic = holder;
        field_146999_f = 194;
        field_147000_g = 168;
    }

    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        field_146289_q.drawString(StatCollector.translateToLocal("inventory.PatternChest"), 60, 6, 0x404040);
        field_146289_q.drawString(StatCollector.translateToLocal("container.inventory"), 17, (field_147000_g - 96) + 2, 0x404040);
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/patternchest.png");

    protected void func_146976_a (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146297_k.getTextureManager().bindTexture(background);
        int cornerX = (field_146294_l - field_146999_f) / 2;
        int cornerY = (field_146295_m - field_147000_g) / 2;
        drawTexturedModalRect(cornerX, cornerY, 0, 0, field_146999_f, field_147000_g);
    }
}
