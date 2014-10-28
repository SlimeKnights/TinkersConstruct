package tconstruct.library.weaponry;

import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.BowstringMaterial;
import tconstruct.weaponry.ammo.ArrowAmmo;
import tconstruct.weaponry.entity.ArrowEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tconstruct.library.crafting.ToolBuilder;

import java.util.List;

public abstract class BowBaseAmmo extends ProjectileWeapon {
    public BowBaseAmmo(int baseDamage, String name) {
        super(baseDamage, name);
    }

    @Override
    public int durabilityTypeAccessory() {
        return 2; // head-type
    }

    @Override
    public int durabilityTypeExtra() {
        return 1; // handle-type
    }

    @Override
    public boolean zoomOnWindup(ItemStack itemStack) {
        return true;
    }

    @Override
    public float getZoom(ItemStack itemStack) {
        return 1.2f;
    }

    @Override
    public float getMinWindupProgress(ItemStack itemStack) {
        return 0.5f;
    }

    @Override
    public ItemStack searchForAmmo(EntityPlayer player, ItemStack weapon)
    {
        // arrow priority: hotbar > inventory, tinker arrows > regular arrows
        ItemStack[] inventory = player.inventory.mainInventory;

        // check hotbar for tinker arrows
        for(int i = 0; i < InventoryPlayer.getHotbarSize(); i++)
            if(checkTinkerArrow(inventory[i]))
                return inventory[i];

        // check hotbar for vanilla arrows
        for(int i = 0; i < InventoryPlayer.getHotbarSize(); i++)
            if(checkVanillaArrow(inventory[i]))
                return inventory[i];

        // check the rest of the inventory for tinker arrows
        for(int i = InventoryPlayer.getHotbarSize(); i < inventory.length; i++)
            if(checkTinkerArrow(inventory[i]))
                return inventory[i];

        // check the rest of the inventory for vanilla arrows
        for(int i = InventoryPlayer.getHotbarSize(); i < inventory.length; i++)
            if(checkVanillaArrow(inventory[i]))
                return inventory[i];

        // creative mode
        if(player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, weapon) > 0)
            return new ItemStack(Items.arrow);

        return null;
    }

    private boolean checkTinkerArrow(ItemStack stack)
    {
        // null
        if(stack == null)
            return false;
        // no tinker arrow
        if(!(stack.getItem() instanceof ArrowAmmo))
            return false;
        // no ammo left
        if(((IAmmo) stack.getItem()).getAmmoCount(stack) <= 0)
            return false;

        return true;
    }

    private boolean checkVanillaArrow(ItemStack stack)
    {
        // null
        if(stack == null)
            return false;
        // no arrow
        if(!(stack.getItem() == Items.arrow))
            return false;
        // inventory shouldn't contain stacksize 0 items so we don't have to check that.

        return true;
    }

    @Override
    protected Entity createProjectile(ItemStack arrows, World world, EntityPlayer player, float speed, float accuracy) {
        EntityArrow arrow;

        if(arrows.getItem() == Items.arrow) {
            arrow = new EntityArrow(world, player, speed/1.5f); // vanilla arrows internally do x1.5
        }
        else {
            ItemStack reference = arrows.copy();
            reference.stackSize = 1;
            reference.getTagCompound().getCompoundTag("InfiTool").setInteger("Ammo", 1);
            arrow = new ArrowEntity(world, player, speed, accuracy, reference);
        }

        if(player.capabilities.isCreativeMode)
            arrow.canBePickedUp = 2;

        return arrow;
    }

    @Override
    public void playFiringSound(World world, EntityPlayer player, ItemStack weapon, ItemStack ammo, float speed, float accuracy) {
        world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + speed * 0.5F);
    }

    @Override
    public void buildTool (int id, String name, List list)
    {
        // all creative bows use regular bowstring
        ItemStack handleStack = new ItemStack(getHandleItem(), 1, 0); // regular bowstring
        ItemStack accessoryStack = getPartAmount() > 2 ? new ItemStack(getAccessoryItem(), 1, id) : null;
        ItemStack extraStack = getPartAmount() > 3 ? new ItemStack(getExtraItem(), 1, id) : null;

        ItemStack tool = ToolBuilder.instance.buildTool(new ItemStack(getHeadItem(), 1, id), handleStack, accessoryStack, extraStack, "");
        if (tool != null)
        {
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
            list.add(tool);
        }
    }

    @Override
    protected int getDefaultColor(int renderPass, int materialID) {
        // bowstring uses custom material
        if(renderPass == 0)
            return TConstructRegistry.getCustomMaterial(materialID, BowstringMaterial.class).color;

        return super.getDefaultColor(renderPass, materialID);
    }

    @Override
    public String[] getTraits() {
        return new String[] {"weapon", "bow", "windup"};
    }
}
