package tconstruct.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import tconstruct.client.tabs.InventoryTabArmorExtended;
import tconstruct.client.tabs.TabRegistry;
import tconstruct.common.TContent;
import tconstruct.inventory.ArmorExtendedContainer;
import tconstruct.util.player.ArmorExtended;

public class ArmorExtendedGui extends InventoryEffectRenderer
{
    public InventoryPlayer inv;
    public ArmorExtended stats;

    private float xSize_lo;
    private float ySize_lo;

    public ArmorExtendedGui(InventoryPlayer inventoryplayer, ArmorExtended holder)
    {
        super(new ArmorExtendedContainer(inventoryplayer, holder));
        inv = inventoryplayer;
        stats = holder;
    }

    public void initGui ()
    {
        super.initGui();

        int cornerX = guiLeft;
        int cornerY = (this.width - this.ySize) / 2;
        this.buttonList.clear();

        //        InventoryTab tab = new InventoryTab(2, cornerX, cornerY - 28, new ItemStack(Block.workbench), 0);
        //        this.buttonList.add(tab);
        //        tab = new InventoryTab(3, cornerX + 28, cornerY - 28, new ItemStack(Item.plateDiamond), 1);
        //        tab.enabled = false;
        //        this.buttonList.add(tab);

        TabRegistry.updateTabValues(cornerX, cornerY, InventoryTabArmorExtended.class);
        TabRegistry.addTabsToList(this.buttonList);
    }

    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        //fontRenderer.drawString(StatCollector.translateToLocal("inventory.armorextended"), 60, 6, 0x404040);
        //fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 17, (ySize - 96) + 2, 0x404040);
    }

    public void drawScreen (int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
        this.xSize_lo = (float) par1;
        this.ySize_lo = (float) par2;
        //        if (stats.inventory[2] != null && stats.inventory[2].getItem() == TContent.knapsack)
        //        {
        //            if (this.buttonList.size() < 3)
        //            {
        //                int cornerX = guiLeft;
        //                int cornerY = (this.height - this.ySize) / 2;
        //                InventoryTab tab = new InventoryTab(4, cornerX + 56, cornerY - 28, new ItemStack(TContent.knapsack), 1);
        //                this.buttonList.add(tab);
        //            }
        //        }
        //        else
        //        {
        //            if (this.buttonList.size() >= 3)
        //            {
        //                buttonList.remove(2);
        //            }
        //        }
    }

    private static final ResourceLocation background = new ResourceLocation("tinker", "textures/gui/armorextended.png");

    protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //mc.renderEngine.bindTexture("/mods/tinker/textures/gui/armorextended.png");
        this.mc.getTextureManager().bindTexture(background);
        int cornerX = guiLeft;
        int cornerY = (width - ySize) / 2;
        drawTexturedModalRect(cornerX, cornerY, 0, 0, xSize, ySize);

        if (!stats.isStackInSlot(0))
            drawTexturedModalRect(cornerX + 79, cornerY + 16, 176, 9, 18, 18);
        if (!stats.isStackInSlot(1))
            drawTexturedModalRect(cornerX + 79, cornerY + 34, 176, 27, 18, 18);
        if (!stats.isStackInSlot(2))
            drawTexturedModalRect(cornerX + 115, cornerY + 16, 212, 9, 18, 18);
        if (!stats.isStackInSlot(3))
            drawTexturedModalRect(cornerX + 115, cornerY + 34, 212, 27, 18, 18);
        if (!stats.isStackInSlot(4))
            drawTexturedModalRect(cornerX + 151, cornerY + 16, 230, 0, 18, 18);
        if (!stats.isStackInSlot(5))
            drawTexturedModalRect(cornerX + 151, cornerY + 34, 230, 18, 18, 18);
        if (!stats.isStackInSlot(6))
            drawTexturedModalRect(cornerX + 151, cornerY + 52, 230, 36, 18, 18);

        cornerX = this.guiLeft;
        cornerY = this.guiTop;
        drawPlayerOnGui(this.mc, cornerX + 33, cornerY + 75, 30, (float) (cornerX + 51) - this.xSize_lo, (float) (cornerY + 75 - 50) - this.ySize_lo);
    }

    public static void drawPlayerOnGui (Minecraft par0Minecraft, int par1, int par2, int par3, float par4, float par5)
    {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) par1, (float) par2, 50.0F);
        GL11.glScalef((float) (-par3), (float) par3, (float) par3);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = par0Minecraft.thePlayer.renderYawOffset;
        float f3 = par0Minecraft.thePlayer.rotationYaw;
        float f4 = par0Minecraft.thePlayer.rotationPitch;
        par4 -= 19;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float) Math.atan((double) (par5 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        par0Minecraft.thePlayer.renderYawOffset = (float) Math.atan((double) (par4 / 40.0F)) * 20.0F;
        par0Minecraft.thePlayer.rotationYaw = (float) Math.atan((double) (par4 / 40.0F)) * 40.0F;
        par0Minecraft.thePlayer.rotationPitch = -((float) Math.atan((double) (par5 / 40.0F))) * 20.0F;
        par0Minecraft.thePlayer.rotationYawHead = par0Minecraft.thePlayer.rotationYaw;
        GL11.glTranslatef(0.0F, par0Minecraft.thePlayer.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(par0Minecraft.thePlayer, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        par0Minecraft.thePlayer.renderYawOffset = f2;
        par0Minecraft.thePlayer.rotationYaw = f3;
        par0Minecraft.thePlayer.rotationPitch = f4;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
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
