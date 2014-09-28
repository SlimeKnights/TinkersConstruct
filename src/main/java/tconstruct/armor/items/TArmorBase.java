package tconstruct.armor.items;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class TArmorBase extends ItemArmor
// implements ISpecialArmor
{
    IIcon[] icons;
    String[] iconNames = { "wood_boots" };

    // static Minecraft mc = Minecraft.getMinecraft();
    // private ModelBiped modelArmor;

    public TArmorBase(int armorSlot)
    {
        super(ArmorMaterial.CLOTH, 0, armorSlot);
        this.maxStackSize = 1;
        this.setMaxDamage(100);
        setNoRepair();
        canRepair = false;
        this.setCreativeTab(CreativeTabs.tabMisc);
        // this.modelArmor = new ModelBiped(0.75F);
        // this.setCreativeTab(TConstructRegistry.toolTab);
    }

    public void onArmorTickUpdate (World world, EntityPlayer player, ItemStack itemStack)
    {

    }

    /**
     * Called by RenderBiped and RenderPlayer to determine the armor texture
     * that should be use for the currently equiped item. This will only be
     * called on instances of ItemArmor.
     * 
     * Returning null from this function will use the default value.
     * 
     * @param stack
     *            ItemStack for the equpt armor
     * @param entity
     *            The entity wearing the armor
     * @param slot
     *            The slot the armor is in
     * @param layer
     *            The render layer, either 1 or 2, 2 is only used for CLOTH
     *            armor by default
     * @return Path of texture to bind, or null to use default
     */
    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, String layer)
    {
        return "/mods/tinker/textures/armor/wood_1.png";
    }

    /**
     * Override this method to have an item handle its own armor rendering.
     * 
     * @param entityLiving
     *            The entity wearing the armor
     * @param itemStack
     *            The itemStack to render the model of
     * @param armorSlot
     *            0=head, 1=torso, 2=legs, 3=feet
     * 
     * @return A ModelBiped to render instead of the default
     */
    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel (EntityLivingBase par1EntityLiving, ItemStack itemStack, int armorSlot)
    {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses ()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage (int meta)
    {
        return icons[0];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[iconNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:armor/" + iconNames[i]);
        }
    }

    /*
     * @Override public ArmorProperties getProperties (EntityLiving player,
     * ItemStack armor, DamageSource source, double damage, int slot) { // TODO
     * Auto-generated method stub return null; }
     * 
     * @Override public int getArmorDisplay (EntityPlayer player, ItemStack
     * armor, int slot) { // TODO Auto-generated method stub return 0; }
     * 
     * @Override public void damageArmor (EntityLiving entity, ItemStack stack,
     * DamageSource source, int damage, int slot) { // TODO Auto-generated
     * method stub
     * 
     * }
     */
}
