package tinker.armory.client;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderArmorStandEntity extends RenderLiving
{
    protected ModelBase mainModel;
    protected float sizeIncrease;
    protected ModelBiped armorChestplate;
    protected ModelBiped armorBody;

    /** List of armor texture filenames. */
    public static String[] bipedArmorFilenamePrefix = new String[] {"cloth", "chain", "iron", "diamond", "gold"};

    public RenderArmorStandEntity(ModelBase model, float par2)
    {
        this(model, par2, 1.0F);
    }

    public RenderArmorStandEntity(ModelBase model, float par2, float par3)
    {
        super(model, par2);
        this.mainModel = model;
        this.sizeIncrease = par3;
        this.func_82421_b();
    }

    protected void func_82421_b()
    {
        this.armorChestplate = new ModelBiped(1.0F);
        this.armorBody = new ModelBiped(0.5F);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2, float par3)
    {
        ItemStack stack = par1EntityLiving.getCurrentArmor(3 - par2);

        if (stack != null)
        {
            Item item = stack.getItem();

            if (item instanceof ItemArmor)
            {
                ItemArmor itemarmor = (ItemArmor)item;
                this.loadTexture(ForgeHooksClient.getArmorTexture(stack, "/armor/" + bipedArmorFilenamePrefix[itemarmor.renderIndex%4] + "_" + (par2 == 2 ? 2 : 1) + ".png"));
                ModelBiped biped = par2 == 2 ? this.armorBody : this.armorChestplate;
                biped.bipedHead.showModel = par2 == 0;
                biped.bipedHeadwear.showModel = par2 == 0;
                biped.bipedBody.showModel = par2 == 1 || par2 == 2;
                biped.bipedRightArm.showModel = par2 == 1;
                biped.bipedLeftArm.showModel = par2 == 1;
                biped.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
                biped.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
                this.setRenderPassModel(biped);

                if (biped != null)
                {
                    biped.onGround = this.mainModel.onGround;
                }

                if (biped != null)
                {
                    biped.isRiding = this.mainModel.isRiding;
                }

                if (biped != null)
                {
                    biped.isChild = this.mainModel.isChild;
                }

                float var8 = 1.0F;

                if (itemarmor.getArmorMaterial() == EnumArmorMaterial.CLOTH)
                {
                    int var9 = itemarmor.getColor(stack);
                    float var10 = (float)(var9 >> 16 & 255) / 255.0F;
                    float var11 = (float)(var9 >> 8 & 255) / 255.0F;
                    float var12 = (float)(var9 & 255) / 255.0F;
                    GL11.glColor3f(var8 * var10, var8 * var11, var8 * var12);

                    if (stack.isItemEnchanted())
                    {
                        return 31;
                    }

                    return 16;
                }

                GL11.glColor3f(var8, var8, var8);

                if (stack.isItemEnchanted())
                {
                    return 15;
                }

                return 1;
            }
        }

        return -1;
    }

    protected void func_82408_c(EntityLiving par1EntityLiving, int par2, float par3)
    {
        ItemStack var4 = par1EntityLiving.getCurrentArmor(3 - par2);

        if (var4 != null)
        {
            Item var5 = var4.getItem();

            if (var5 instanceof ItemArmor)
            {
                ItemArmor var6 = (ItemArmor)var5;
                this.loadTexture("/armor/" + bipedArmorFilenamePrefix[var6.renderIndex] + "_" + (par2 == 2 ? 2 : 1) + "_b.png");
                float var7 = 1.0F;
                GL11.glColor3f(var7, var7, var7);
            }
        }
    }

    public void doRenderLiving(EntityLiving par1EntityLiving, double posX, double posY, double posZ, float par8, float par9)
    {
        float var10 = 1.0F;
        GL11.glColor3f(var10, var10, var10);
        ItemStack var11 = par1EntityLiving.getHeldItem();
        this.func_82420_a(par1EntityLiving, var11);
        double var12 = posY - (double)par1EntityLiving.yOffset+0.25;

        //Insert super here
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        this.mainModel.onGround = this.renderSwingProgress(par1EntityLiving, par9);

        if (this.renderPassModel != null)
        {
            this.renderPassModel.onGround = this.mainModel.onGround;
        }

        this.mainModel.isRiding = par1EntityLiving.isRiding();

        if (this.renderPassModel != null)
        {
            this.renderPassModel.isRiding = this.mainModel.isRiding;
        }

        this.mainModel.isChild = par1EntityLiving.isChild();

        if (this.renderPassModel != null)
        {
            this.renderPassModel.isChild = this.mainModel.isChild;
        }

        try
        {
            //float rotate1 = this.interRotation(par1EntityLiving.prevRenderYawOffset, par1EntityLiving.renderYawOffset, par9);
            float rotate2 = this.interRotation(par1EntityLiving.prevRotationYawHead, par1EntityLiving.rotationYawHead, par9);
            float rotate1 = rotate2;
            //float rotate2 = rotate1;
            float rotate3 = par1EntityLiving.prevRotationPitch + (par1EntityLiving.rotationPitch - par1EntityLiving.prevRotationPitch) * par9;
            this.renderLivingAt(par1EntityLiving, posX, posY, posZ);
            //float rotationExisted = this.handleRotationFloat(par1EntityLiving, par9); // Makes the armor breathe
            float rotationExisted = 0;
            this.rotateCorpse(par1EntityLiving, rotationExisted, rotate1, par9);
            float var14 = 0.0625F;
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glScalef(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback(par1EntityLiving, par9);
            GL11.glTranslatef(0.0F, -24.0F * var14 - 0.0078125F - 0.25F, 0.0F);
            float var15 = par1EntityLiving.prevLegYaw + (par1EntityLiving.legYaw - par1EntityLiving.prevLegYaw) * par9;
            float var16 = par1EntityLiving.legSwing - par1EntityLiving.legYaw * (1.0F - par9);

            if (par1EntityLiving.isChild())
            {
                var16 *= 3.0F;
            }

            if (var15 > 1.0F)
            {
                var15 = 1.0F;
            }

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            this.mainModel.setLivingAnimations(par1EntityLiving, var16, var15, par9);
            this.renderModel(par1EntityLiving, var16, var15, rotationExisted, rotate2 - rotate1, rotate3, var14);
            float var19;
            int var18;
            float var20;
            float var22;

            for (int armorIter = 0; armorIter < 4; ++armorIter) //Armor
            {
                var18 = this.shouldRenderPass(par1EntityLiving, armorIter, par9);

                if (var18 > 0)
                {
                    this.renderPassModel.setLivingAnimations(par1EntityLiving, var16, var15, par9);
                    this.renderPassModel.render(par1EntityLiving, 0, 0, rotationExisted, rotate2 - rotate1, rotate3, var14);

                    if ((var18 & 240) == 16)
                    {
                        this.func_82408_c(par1EntityLiving, armorIter, par9);
                        this.renderPassModel.render(par1EntityLiving, var16, var15, rotationExisted, rotate2 - rotate1, rotate3, var14);
                    }

                    if ((var18 & 15) == 15)
                    {
                        var19 = (float)par1EntityLiving.ticksExisted + par9;
                        this.loadTexture("%blur%/misc/glint.png");
                        GL11.glEnable(GL11.GL_BLEND);
                        var20 = 0.5F;
                        GL11.glColor4f(var20, var20, var20, 1.0F);
                        GL11.glDepthFunc(GL11.GL_EQUAL);
                        GL11.glDepthMask(false);

                        for (int var21 = 0; var21 < 2; ++var21)
                        {
                            GL11.glDisable(GL11.GL_LIGHTING);
                            var22 = 0.76F;
                            GL11.glColor4f(0.5F * var22, 0.25F * var22, 0.8F * var22, 1.0F);
                            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                            GL11.glMatrixMode(GL11.GL_TEXTURE);
                            GL11.glLoadIdentity();
                            float var23 = var19 * (0.001F + (float)var21 * 0.003F) * 20.0F;
                            float var24 = 0.33333334F;
                            GL11.glScalef(var24, var24, var24);
                            GL11.glRotatef(30.0F - (float)var21 * 60.0F, 0.0F, 0.0F, 1.0F);
                            GL11.glTranslatef(0.0F, var23, 0.0F);
                            GL11.glMatrixMode(GL11.GL_MODELVIEW);
                            this.renderPassModel.render(par1EntityLiving, var16, var15, rotationExisted, rotate2 - rotate1, rotate3, var14);
                        }

                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        GL11.glMatrixMode(GL11.GL_TEXTURE);
                        GL11.glDepthMask(true);
                        GL11.glLoadIdentity();
                        GL11.glMatrixMode(GL11.GL_MODELVIEW);
                        GL11.glEnable(GL11.GL_LIGHTING);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glDepthFunc(GL11.GL_LEQUAL);
                    }

                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                }
            }

            GL11.glDepthMask(true);
            this.renderEquippedItems(par1EntityLiving, par9);
            float var26 = par1EntityLiving.getBrightness(par9);
            var18 = this.getColorMultiplier(par1EntityLiving, var26, par9);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

            if ((var18 >> 24 & 255) > 0 || par1EntityLiving.hurtTime > 0 || par1EntityLiving.deathTime > 0)
            {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDepthFunc(GL11.GL_EQUAL);

                if (par1EntityLiving.hurtTime > 0 || par1EntityLiving.deathTime > 0)
                {
                    GL11.glColor4f(var26, 0.0F, 0.0F, 0.4F);
                    this.mainModel.render(par1EntityLiving, var16, var15, rotationExisted, rotate2 - rotate1, rotate3, var14);

                    for (int var27 = 0; var27 < 4; ++var27)
                    {
                        if (this.inheritRenderPass(par1EntityLiving, var27, par9) >= 0)
                        {
                            GL11.glColor4f(var26, 0.0F, 0.0F, 0.4F);
                            this.renderPassModel.render(par1EntityLiving, var16, var15, rotationExisted, rotate2 - rotate1, rotate3, var14);
                        }
                    }
                }

                if ((var18 >> 24 & 255) > 0)
                {
                    var19 = (float)(var18 >> 16 & 255) / 255.0F;
                    var20 = (float)(var18 >> 8 & 255) / 255.0F;
                    float var29 = (float)(var18 & 255) / 255.0F;
                    var22 = (float)(var18 >> 24 & 255) / 255.0F;
                    GL11.glColor4f(var19, var20, var29, var22);
                    this.mainModel.render(par1EntityLiving, var16, var15, rotationExisted, rotate2 - rotate1, rotate3, var14);

                    for (int var28 = 0; var28 < 4; ++var28)
                    {
                        if (this.inheritRenderPass(par1EntityLiving, var28, par9) >= 0)
                        {
                            GL11.glColor4f(var19, var20, var29, var22);
                            this.renderPassModel.render(par1EntityLiving, var16, var15, rotationExisted, rotate2 - rotate1, rotate3, var14);
                        }
                    }
                }

                GL11.glDepthFunc(GL11.GL_LEQUAL);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        }
        catch (Exception var25)
        {
            var25.printStackTrace();
        }

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
        this.passSpecialRender(par1EntityLiving, posX, posY, posZ);
    }
    
    public float interRotation(float par1, float par2, float par3)
    {
        float var4;

        for (var4 = par2 - par1; var4 < -180.0F; var4 += 360.0F)
        {
            ;
        }

        while (var4 >= 180.0F)
        {
            var4 -= 360.0F;
        }

        return par1 + par3 * var4;
    }
    
    @Override
    protected float renderSwingProgress(EntityLiving par1EntityLiving, float par2)
    {
        return 0;
    }

    protected void func_82420_a(EntityLiving par1EntityLiving, ItemStack par2ItemStack)
    {
        this.armorChestplate.heldItemRight = this.armorBody.heldItemRight = par2ItemStack != null ? 1 : 0;
    }

    protected void renderEquippedItems(EntityLiving par1EntityLiving, float par2)
    {
        float var3 = 1.0F;
        GL11.glColor3f(var3, var3, var3);
        super.renderEquippedItems(par1EntityLiving, par2);
        ItemStack var4 = par1EntityLiving.getHeldItem();
        ItemStack var5 = par1EntityLiving.getCurrentArmor(3);
        float var6;

        if (var5 != null)
        {
            GL11.glPushMatrix();
            //this.mainModel.head.postRender(0.0625F);

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(var5, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, var5, BLOCK_3D));

            if (var5.getItem() instanceof ItemBlock)
            {
                if (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[var5.itemID].getRenderType()))
                {
                    var6 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(var6, -var6, -var6);
                }

                this.renderManager.itemRenderer.renderItem(par1EntityLiving, var5, 0);
            }
            else if (var5.getItem().itemID == Item.skull.itemID)
            {
                var6 = 1.0625F;
                GL11.glScalef(var6, -var6, -var6);
                String var7 = "";

                if (var5.hasTagCompound() && var5.getTagCompound().hasKey("SkullOwner"))
                {
                    var7 = var5.getTagCompound().getString("SkullOwner");
                }

                TileEntitySkullRenderer.skullRenderer.func_82393_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, var5.getItemDamage(), var7);
            }

            GL11.glPopMatrix();
        }

        if (var4 != null)
        {
            GL11.glPushMatrix();

            if (this.mainModel.isChild)
            {
                var6 = 0.5F;
                GL11.glTranslatef(0.0F, 0.625F, 0.0F);
                GL11.glRotatef(-20.0F, -1.0F, 0.0F, 0.0F);
                GL11.glScalef(var6, var6, var6);
            }

            //this.mainModel.bipedRightArm.postRender(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(var4, EQUIPPED);
            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, var4, BLOCK_3D));

            if (var4.getItem() instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[var4.itemID].getRenderType())))
            {
                var6 = 0.5F;
                GL11.glTranslatef(-0.4325F, 0.1875F, -0.3125F);
                var6 *= 0.75F;
                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(-var6, -var6, var6);
            }
            else if (var4.itemID == Item.bow.itemID)
            {
                var6 = 0.625F;
                GL11.glTranslatef(-0.5F, 0.125F, 0.3125F);
                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(var6, -var6, var6);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else if (Item.itemsList[var4.itemID].isFull3D())
            {
                var6 = 0.625F;

                if (Item.itemsList[var4.itemID].shouldRotateAroundWhenRendering())
                {
                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
                }

                this.func_82422_c();
                GL11.glScalef(var6, -var6, var6);
                GL11.glTranslatef(-0.5F, 0.0F, 0.0F);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            }
            else
            {
                var6 = 0.375F;
                GL11.glTranslatef(-0.125F, 0.1875F, -0.1875F);
                GL11.glScalef(var6, var6, var6);
                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
            }

            this.renderManager.itemRenderer.renderItem(par1EntityLiving, var4, 0);

            if (var4.getItem().requiresMultipleRenderPasses())
            {
                for (int x = 1; x < var4.getItem().getRenderPasses(var4.getItemDamage()); x++)
                {
                    this.renderManager.itemRenderer.renderItem(par1EntityLiving, var4, x);
                }
            }

            GL11.glPopMatrix();
        }
    }

    protected void func_82422_c()
    {
        GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.doRenderLiving((EntityLiving)par1Entity, par2, par4, par6, par8, par9);
    }
}
