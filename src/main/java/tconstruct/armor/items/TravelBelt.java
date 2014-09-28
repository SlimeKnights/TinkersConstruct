package tconstruct.armor.items;

import cpw.mods.fml.relauncher.*;
import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import tconstruct.armor.ArmorProxyClient;
import tconstruct.client.ArmorControls;
import tconstruct.library.accessory.*;

public class TravelBelt extends AccessoryCore implements IAccessoryModel
{
    public TravelBelt()
    {
        super("travelgear/travel_belt");
    }

    @Override
    public boolean canEquipAccessory (ItemStack item, int slot)
    {
        return slot == 3;
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void registerModifiers (IIconRegister iconRegister)
    {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel (EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        return ArmorProxyClient.belt;
    }

    ResourceLocation texture = new ResourceLocation("tinker", "textures/armor/travel_2.png");

    @Override
    @SideOnly(Side.CLIENT)
    public ResourceLocation getWearbleTexture (Entity entity, ItemStack stack, int slot)
    {
        return texture;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (!stack.hasTagCompound())
            return;

        list.add("\u00a76Ability: Swap Hotbar");
        list.add("\u00a76Control: " + GameSettings.getKeyDisplayString(ArmorControls.beltSwap.getKeyCode()));
    }
}
