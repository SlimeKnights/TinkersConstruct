package tconstruct.items.accessory;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import tconstruct.client.TProxyClient;
import tconstruct.library.IAccessoryModel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TravelBelt extends AccessoryCore implements IAccessoryModel
{
    public TravelBelt(int id)
    {
        super(id, "armor/travel_belt");
    }

    @Override
    public boolean canEquipAccessory (ItemStack item, int slot)
    {
        return slot == 3;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void registerModifiers (IconRegister iconRegister)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel (EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        return TProxyClient.belt;
    }

    @SideOnly(Side.CLIENT)
    ResourceLocation texture = new ResourceLocation("tinker", "textures/armor/travel_2.png");

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearbleTexture (Entity entity, ItemStack stack, int slot)
    {
        return texture;
    }
}
