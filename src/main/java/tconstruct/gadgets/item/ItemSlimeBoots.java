package tconstruct.gadgets.item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import javax.vecmath.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import tconstruct.library.SlimeBounceHandler;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.armor.ArmorPart;
import tconstruct.tools.entity.FancyEntityItem;

public class ItemSlimeBoots extends ItemArmor implements ISpecialArmor {

    public static ArmorMaterial SLIME_MATERIAL = EnumHelper.addArmorMaterial("SLIME", 100, new int[] {0, 0, 0, 0}, 0);

    public final ArmorPart armorPart;
    protected final String textureFolder;
    protected final String textureName;

    public ItemSlimeBoots() {
        super(SLIME_MATERIAL, 0, 3);
        this.setCreativeTab(TConstructRegistry.gadgetsTab);
        this.setMaxStackSize(1);
        this.setMaxDamage(100);
        SLIME_MATERIAL.customCraftingMaterial = Items.slime_ball;
        armorPart = ArmorPart.Feet;
        textureFolder = "armor";
        textureName = "slime";
        SlimeBounceHandler.registerEvent(this);
    }

    @Override
    public boolean isValidArmor(ItemStack stack, int armorType1, Entity entity) {
        // can be worn as boots
        return armorType1 == 3;
    }

    // equipping with rightclick
    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        int slot = 1; // 0 = current item, 1 = feet
        ItemStack itemstack = playerIn.getEquipmentInSlot(slot);
        if (itemstack == null) {
            playerIn.setCurrentItemOrArmor(slot, itemStackIn.copy());
            itemStackIn.stackSize--;
        } else {
            itemstack = itemstack.copy();
            playerIn.setCurrentItemOrArmor(1, itemStackIn.copy());
            // playerIn.setCurrentItemOrArmor(0, itemstack);
            playerIn.entityDropItem(itemstack, 0);
            itemStackIn.stackSize--;
        }
        return itemStackIn;
    }

    /** Called when an entity lands to handle the event */
    @SubscribeEvent
    // RUBBERY BOUNCY BOUNCERY WOOOOO
    public void onFall(LivingFallEvent event) {
        EntityLivingBase living = event.entityLiving;
        // TinkerGadgets.log.info("Fall event.");
        // using fall distance as the event distance could be reduced by jump boost
        if (living == null || living.fallDistance <= 2f) {
            // TinkerGadgets.log.info("Invalid event.");
            return;
        }
        // can the entity bounce?
        if (!SlimeBounceHandler.hasSlimeBoots(living)) {
            // TinkerGadgets.log.info("No Boots.");
            return;
        }

        // reduced fall damage when crouching
        if (living.isSneaking()) {
            // TinkerGadgets.log.info("Sneaking");
            event.distance = 1;
            return;
        }

        // thing is wearing slime boots. let's get bouncyyyyy
        event.setCanceled(true);
        // skip further client processing on players
        if (living.worldObj.isRemote) {
            // TinkerGadgets.log.info("Client Fall Handler.");
            living.playSound("mob.slime.small", 1f, 1f);
            SlimeBounceHandler.addBounceHandler(living);
            // TConstruct.packetPipeline.sendToServer(new BouncedPacket(living));
            return;
        }

        // server players behave differently than non-server players, they have no
        // velocity during the event, so we need to reverse engineer it
        Vector3d motion = SlimeBounceHandler.getMotion(living);
        if (living instanceof EntityPlayerMP) {
            // velocity is lost on server players, but we dont have to defer the bounce
            double gravity = 0.2353455252;
            double time = Math.sqrt(living.fallDistance / gravity);
            double velocity = gravity * time;
            living.setVelocity(motion.x / 0.95f, velocity, motion.z / 0.95f);
            living.velocityChanged = true;
            // preserve momentum
            SlimeBounceHandler.addBounceHandler(living);
            // TinkerGadgets.log.info("Player");
        } else {
            // for non-players, need to defer the bounce
            // only slow down half as much when bouncing
            living.setVelocity(motion.x / 0.95f, motion.y * -0.9, motion.z / 0.95f);
            SlimeBounceHandler.addBounceHandler(living, SlimeBounceHandler.getMotion(living).y);
            // TinkerGadgets.log.info("Not Player");
        }
        // TinkerGadgets.log.info("Server Fall Handler.");
        // update airborn status
        living.isAirBorne = true;
        living.onGround = false;
        event.distance = 0f;
        living.playSound("mob.slime.small", 1f, 1f);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return "tinker:textures/armor/" + textureName + "_" + 1 + ".png";
    }

    @SideOnly(Side.CLIENT)
    protected IIcon[] modifiers;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("tinker:" + textureFolder + "/" + textureName + "_boots");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass) {
        return itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack) {
        return false;
    }

    // ISpecialArmor overrides
    @Override
    public ArmorProperties getProperties(
            EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        return new ArmorProperties(0, armor.getItemDamage() / 100, 100);
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack armor, DamageSource source, int damage, int slot) {
        if (source == DamageSource.fall) {
            return;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        par3List.add(new ItemStack(par1));
    }

    // Vanilla overrides
    @Override
    public boolean isItemTool(ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public boolean isRepairable() {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        list.add("Boooounce!");
        list.add("Hold "
                + (I18n.format(
                        Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyDescription(), new Object[0]))
                + " to stop bouncing.");
    }

    /* Prevent armor from dying */
    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        return new FancyEntityItem(world, location, itemstack);
    }
}
