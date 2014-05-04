package tconstruct.modifiers.tools;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import tconstruct.library.tools.ToolCore;
import net.minecraft.util.StatCollector;

public class ModButtertouch extends ModBoolean
{

    public ModButtertouch(ItemStack[] items, int effect)
    {
        super(items, effect, StatCollector.translateToLocal("gui.modifier.silk"), "\u00a7e", StatCollector.translateToLocal("modifier.tool.silk"));
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        if (tool.getItem() instanceof ToolCore)
        {
            ToolCore toolItem = (ToolCore) tool.getItem();
            for (ItemStack stack : input)
            {
                if (stack != null && stack.hasTagCompound())
                {
                    String targetLock = stack.getTagCompound().getString("TargetLock");
                    if (!targetLock.equals("") && !targetLock.equals(toolItem.getToolName()))
                        return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        tags.setBoolean(key, true);
        addEnchantment(tool, Enchantment.silkTouch, 1);

        int modifiers = tags.getInteger("Modifiers");
        modifiers -= 1;
        tags.setInteger("Modifiers", modifiers);

        int attack = tags.getInteger("Attack");
        attack -= 3;
        if (attack < 0)
            attack = 0;
        tags.setInteger("Attack", attack);

        int miningSpeed = tags.getInteger("MiningSpeed");
        miningSpeed -= 300;
        if (miningSpeed < 0)
            miningSpeed = 0;
        tags.setInteger("MiningSpeed", miningSpeed);

        if (tags.hasKey("MiningSpeed2"))
        {
            int miningSpeed2 = tags.getInteger("MiningSpeed2");
            miningSpeed2 -= 300;
            if (miningSpeed2 < 0)
                miningSpeed2 = 0;
            tags.setInteger("MiningSpeed2", miningSpeed2);
        }

        addToolTip(tool, color + tooltipName, color + key);
    }

    public void addEnchantment (ItemStack tool, Enchantment enchant, int level)
    {
        NBTTagList tags = new NBTTagList();
        Map enchantMap = EnchantmentHelper.getEnchantments(tool);
        Iterator iterator = enchantMap.keySet().iterator();
        int index;
        int lvl;
        boolean hasEnchant = false;
        while (iterator.hasNext())
        {
            NBTTagCompound enchantTag = new NBTTagCompound();
            index = ((Integer) iterator.next()).intValue();
            lvl = (Integer) enchantMap.get(index);
            if (index == enchant.effectId)
            {
                hasEnchant = true;
                enchantTag.setShort("id", (short) index);
                enchantTag.setShort("lvl", (short) ((byte) level));
                tags.appendTag(enchantTag);
            }
            else
            {
                enchantTag.setShort("id", (short) index);
                enchantTag.setShort("lvl", (short) ((byte) lvl));
                tags.appendTag(enchantTag);
            }
        }
        if (!hasEnchant)
        {
            NBTTagCompound enchantTag = new NBTTagCompound();
            enchantTag.setShort("id", (short) enchant.effectId);
            enchantTag.setShort("lvl", (short) ((byte) level));
            tags.appendTag(enchantTag);
        }
        tool.stackTagCompound.setTag("ench", tags);
    }

    @Override
    public boolean validType (ToolCore tool)
    {
        List list = Arrays.asList(tool.getTraits());
        return list.contains("weapon") || list.contains("harvest");
    }
}
