package tconstruct.weaponry.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import tconstruct.library.client.TConstructClientRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.util.Reference;
import tconstruct.weaponry.TinkerWeaponry;
import tconstruct.weaponry.ammo.ArrowAmmo;

import java.util.List;

public class GlassArrows extends ArrowAmmo {
    public GlassArrows() {
        super();
        this.setUnlocalizedName(Reference.prefix("glassArrows"));
    }

    @Override
    public float getAmmoModifier() {
        return 1.0f;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(stack, player, list, par4);

        list.add(EnumChatFormatting.DARK_PURPLE + "Crafted by a legendary Glassmaker");
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List list) {
        ItemStack headStack = new ItemStack(getHeadItem(), 1, 0);
        ItemStack handleStack = new ItemStack(getHandleItem(), 1, 0); // wooden shaft
        ItemStack accessoryStack = new ItemStack(getAccessoryItem(), 1, 0); // feather fletchling

        ItemStack tool = ToolBuilder.instance.buildTool(headStack, handleStack, accessoryStack, null, "");
        if (tool != null)
        {
            tool.getTagCompound().getCompoundTag("InfiTool").setBoolean("Built", true);
        }
        else
            return;

        // now turn it into legendary glass arrows!
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        tags.setInteger("TotalDurability", 100); // 100 arrows
        tags.setInteger("Ammo", 100); // full ammo
        tags.setFloat("Mass", 5.0f); // durp
        tags.setFloat("BreakChance", 200f); // 200% break chance!
        tags.setFloat("Accuracy", 0.001f); // very full accuraccy
        tags.setFloat("Shoddy", 0); // no stonebound/jagged
        tags.setInteger("Unbreaking", 0); // no reinforced
        tags.setInteger("Attack", 10); // insane damage!

        // now make them look like glass
        tags.setInteger("RenderHead", -1);
        tags.setInteger("RenderHandle", -1);
        tags.setInteger("RenderAccessory", -1);
        tags.setInteger("HeadColor", 0xccfff3);
        tags.setInteger("HandleColor", 0xbcfff3);
        tags.setInteger("AccessoryColor", 0xccfff3);

        list.add(tool);
    }
}
