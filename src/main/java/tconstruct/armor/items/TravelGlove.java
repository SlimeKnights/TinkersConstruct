package tconstruct.armor.items;

import cpw.mods.fml.relauncher.*;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import tconstruct.armor.ArmorProxyClient;
import tconstruct.library.accessory.*;

public class TravelGlove extends AccessoryCore implements IAccessoryModel
{
    public TravelGlove()
    {
        super("travelgear/travel_glove");
    }

    @Override
    public boolean canEquipAccessory (ItemStack item, int slot)
    {
        return slot == 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void registerModifiers (IIconRegister iconRegister)
    {
        this.modifiers = new IIcon[4];
        this.modifiers[0] = iconRegister.registerIcon("tinker:travelgear/glove_guard");
        this.modifiers[1] = iconRegister.registerIcon("tinker:travelgear/glove_speedaura");
        this.modifiers[2] = iconRegister.registerIcon("tinker:travelgear/glove_spines");
        this.modifiers[3] = iconRegister.registerIcon("tinker:travelgear/glove_sticky");
    }

    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel (EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        return ArmorProxyClient.glove;
    }

    ResourceLocation texture = new ResourceLocation("tinker", "textures/armor/travel_1.png");

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearbleTexture (Entity entity, ItemStack stack, int slot)
    {
        return texture;
    }
}
