package tconstruct.items.armor;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.client.TControls;
import tconstruct.client.TProxyClient;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.ArmorCore;
import tconstruct.library.armor.EnumArmorPart;
import tconstruct.library.tools.ToolCore;
import tconstruct.util.player.TPlayerStats;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TravelGear extends ArmorCore
{
    String textureName;

    public TravelGear(int id, EnumArmorPart part, String texture)
    {
        super(id, 0, part);
        this.textureName = texture;
        this.setCreativeTab(TConstructRegistry.materialTab);
    }

    @Override
    public String getModifyType ()
    {
        return "Clothing";
    }

    @SideOnly(Side.CLIENT)
    protected Icon[] modifiers;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IconRegister iconRegister)
    {
        this.itemIcon = iconRegister.registerIcon("tinker:armor/" + textureName + "_"
                + (this.armorType == 0 ? "goggles" : this.armorType == 1 ? "vest" : this.armorType == 2 ? "wings" : this.armorType == 3 ? "boots" : "helmet"));
        registerModifiers(iconRegister);
    }

    @SideOnly(Side.CLIENT)
    protected void registerModifiers (IconRegister iconRegister)
    {

    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture (ItemStack stack, Entity entity, int slot, int layer)
    {
        if (slot == 2)
            return "tinker:textures/armor/" + textureName + "_" + 2 + ".png";
        return "tinker:textures/armor/" + textureName + "_" + layer + ".png";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean requiresMultipleRenderPasses ()
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 4;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect (ItemStack par1ItemStack)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIcon (ItemStack stack, int renderPass)
    {
        if (renderPass > 0)
        {
            if (stack.hasTagCompound())
            {
                NBTTagCompound tags = stack.getTagCompound().getCompoundTag(getBaseTag());
                if (renderPass == 1 && tags.hasKey("Effect1"))
                {
                    return modifiers[tags.getInteger("Effect1")];
                }
                if (renderPass == 2 && tags.hasKey("Effect2"))
                {
                    return modifiers[tags.getInteger("Effect2")];
                }
                if (renderPass == 3 && tags.hasKey("Effect3"))
                {
                    return modifiers[tags.getInteger("Effect3")];
                }
            }
            return ToolCore.blankSprite;
        }

        return itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel (EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot)
    {
        if (armorSlot == 1)
            return TProxyClient.vest;
        if (armorSlot == 2)
            return TProxyClient.wings;
        if (armorSlot == 3)
            return TProxyClient.bootbump;
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems (int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        par3List.add(getDefaultItem());
    }

    public ItemStack getDefaultItem ()
    {
        ItemStack gear = new ItemStack(this.itemID, 1, 0);
        NBTTagCompound baseTag = new NBTTagCompound();

        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("Modifiers", 3);
        if (this.armorPart == EnumArmorPart.Head)
            tag.setDouble("Protection", 4);
        else
            tag.setDouble("Protection", 8);
        baseTag.setTag(getBaseTag(), tag);

        int baseDurability = 500;

        baseTag.setInteger("Damage", 0); //Damage is damage to the tool
        baseTag.setInteger("TotalDurability", baseDurability);
        baseTag.setInteger("BaseDurability", baseDurability);
        baseTag.setInteger("BonusDurability", 0); //Modifier
        baseTag.setFloat("ModDurability", 0f); //Modifier
        baseTag.setBoolean("Broken", false);
        baseTag.setBoolean("Built", true);

        gear.setTagCompound(baseTag);
        return gear;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        if (!stack.hasTagCompound())
            return;

        switch (armorPart)
        {
        case Head:
            list.add("\u00a76Ability: Clear Vision");
            list.add("\u00a76Toggle with: "+GameSettings.getKeyDisplayString(TControls.toggleGoggles.keyCode));
            break;
        case Chest:
            list.add("\u00a76Ability: Swift Swim");
            break;
        case Legs:
            list.add("\u00a76Ability: Featherfall");
            break;
        case Feet:
            list.add("\u00a76Ability: High Step");
            break;
        default:            
        }

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag(getBaseTag());
        double protection = tags.getDouble("Protection");
        if (protection > 0)
            list.add("\u00a77Protection: " + protection + "%");

        boolean displayToolTips = true;
        int tipNum = 0;
        while (displayToolTips)
        {
            tipNum++;
            String tooltip = "Tooltip" + tipNum;
            if (tags.hasKey(tooltip))
            {
                String tipName = tags.getString(tooltip);
                if (!tipName.equals(""))
                    list.add(tipName);
            }
            else
                displayToolTips = false;
        }
    }

    @Override
    public void damageArmor (EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot)
    {
        //Deimplemented for now
    }

    @Override
    public void onArmorTickUpdate (World world, EntityPlayer player, ItemStack itemStack)
    {
        if (armorPart == EnumArmorPart.Chest)
        {
            if (player.isInWater())
            {
                player.motionX *= 1.2D;
                if (player.motionY > 0.0D)
                {
                    player.motionY *= 1.2D;
                }
                player.motionZ *= 1.2D;
                double maxSpeed = 0.2D;
                if (player.motionX > maxSpeed)
                {
                    player.motionX = maxSpeed;
                }
                else if (player.motionX < -maxSpeed)
                {
                    player.motionX = -maxSpeed;
                }
                if (player.motionY > maxSpeed)
                {
                    player.motionY = maxSpeed;
                }
                if (player.motionZ > maxSpeed)
                {
                    player.motionZ = maxSpeed;
                }
                else if (player.motionZ < -maxSpeed)
                {
                    player.motionZ = -maxSpeed;
                }
            }
        }

        if (armorPart == EnumArmorPart.Feet)
        {
            if (player.stepHeight < 1.0f)
                player.stepHeight = 1.0f;
            /*if (player.isInWater())
                world.setBlock((int) Math.floor(player.posX), (int) Math.floor(player.posY) - 1, (int) Math.floor(player.posZ), Block.ice.blockID);*/
            /*for (int x = -1; x <= 1; x++)
            {
                for (int z = -1; z <= 1; z++)
                {
                    Block block = Block.blocksList[world.getBlockId((int) Math.floor(player.posX) + x, (int) Math.floor(player.posY) - 1, (int) Math.floor(player.posZ) + z)];
                    if (block == Block.waterStill || block == Block.waterMoving)
                        world.setBlock((int) Math.floor(player.posX) + x, (int) Math.floor(player.posY) - 1, (int) Math.floor(player.posZ) + z, Block.ice.blockID);
                }
            }*/
        }

        if (armorPart == EnumArmorPart.Head)
        {
            TPlayerStats stats = TConstruct.playerTracker.getPlayerStats(player.username);
            if (stats.activeGoggles)
            {
                player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 15 * 20, 0, true));
                //player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 1, 0, true));
            }
        }
    }
}
