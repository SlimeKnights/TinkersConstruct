package tinker.tconstruct.client.entityrender;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.IArmorModelProvider;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerArmorRender extends RenderPlayer
{
    private ModelBiped modelBipedMain;
    private ModelBiped modelArmorChestplate;
    private ModelBiped modelArmor;
    public static String[] armorFilenamePrefix = new String[] {"cloth", "chain", "iron", "diamond", "gold"};
    public static float NAME_TAG_RANGE = 64.0f;
    public static float NAME_TAG_RANGE_SNEAK = 32.0f;

    public PlayerArmorRender()
    {
        super();
        this.modelBipedMain = (ModelBiped)this.mainModel;
        this.modelArmorChestplate = new ModelBiped(1.0F);
        this.modelArmor = new ModelBiped(0.5F);
    }

    /**
     * Set the specified armor model as the player model. Args: player, armorSlot, partialTick
     */
    protected int setArmorModel(EntityPlayer par1EntityPlayer, int slot, float par3)
    {
        ItemStack stack = par1EntityPlayer.inventory.armorItemInSlot(3 - slot);

        if (stack != null)
        {
            Item armoritem = stack.getItem();

            if (armoritem instanceof ItemArmor)
            {
                ItemArmor var6 = (ItemArmor)armoritem;
                this.loadTexture(ForgeHooksClient.getArmorTexture(stack, "/armor/" + armorFilenamePrefix[var6.renderIndex] + "_" + (slot == 2 ? 2 : 1) + ".png"));
                ModelBiped var7;
                if (armoritem instanceof IArmorModelProvider)
                {
                	var7 = ((IArmorModelProvider) armoritem).provideArmorModel(slot);
                }
                else
                {
	                var7 = slot == 2 ? this.modelArmor : this.modelArmorChestplate;
	                var7.bipedHead.showModel = slot == 0;
	                var7.bipedHeadwear.showModel = slot == 0;
	                var7.bipedBody.showModel = slot == 1 || slot == 2;
	                var7.bipedRightArm.showModel = slot == 1;
	                var7.bipedLeftArm.showModel = slot == 1;
	                var7.bipedRightLeg.showModel = slot == 2 || slot == 3;
	                var7.bipedLeftLeg.showModel = slot == 2 || slot == 3;
                }
                this.setRenderPassModel(var7);

                if (var7 != null)
                {
                    var7.onGround = this.mainModel.onGround;
                }

                if (var7 != null)
                {
                    var7.isRiding = this.mainModel.isRiding;
                }

                if (var7 != null)
                {
                    var7.isChild = this.mainModel.isChild;
                }

                float var8 = 1.0F;

                if (var6.getArmorMaterial() == EnumArmorMaterial.CLOTH)
                {
                    int var9 = var6.getColor(stack);
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
}
