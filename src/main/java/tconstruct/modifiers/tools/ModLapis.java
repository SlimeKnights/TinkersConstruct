package tconstruct.modifiers.tools;

import java.util.*;
import net.minecraft.enchantment.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import tconstruct.library.tools.ToolCore;

public class ModLapis extends ItemModTypeFilter
{
    String tooltipName;
    int max = 450;

    public ModLapis(int effect, ItemStack[] items, int[] values)
    {
        super(effect, "Lapis", items, values);
        tooltipName = "\u00a79Luck";
    }

    @Override
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        if (tool.getItem() instanceof ToolCore)
        {
            ToolCore toolItem = (ToolCore) tool.getItem();
            if (!validType(toolItem))
                return false;

            if (matchingAmount(input) > max)
                return false;

            NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");

            if (tags.getBoolean("Silk Touch"))
                return false;

            if (!tags.hasKey(key))
                return tags.getInteger("Modifiers") > 0 && matchingAmount(input) <= max;

            int keyPair[] = tags.getIntArray(key);
            if (keyPair[0] + matchingAmount(input) <= max)
                return true;
        }
        return false;
    }

    @Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (!tags.hasKey(key))
        {
            tags.setBoolean(key, true);

            String modName = "\u00a79Lapis (0/450)";
            int tooltipIndex = addToolTip(tool, "\u00a79Luck", modName);
            int[] keyPair = new int[] { 0, tooltipIndex };
            tags.setIntArray(key, keyPair);

            int modifiers = tags.getInteger("Modifiers");
            modifiers -= 1;
            tags.setInteger("Modifiers", modifiers);
        }

        int increase = matchingAmount(input);
        int keyPair[] = tags.getIntArray(key);
        keyPair[0] += increase;
        tags.setIntArray(key, keyPair);
        ToolCore toolcore = (ToolCore) tool.getItem();
        String[] types = toolcore.getTraits();
        boolean weapon = false;
        boolean harvest = false;
        for (String s : types)
        {
            if (s.equals("harvest"))
                harvest = true;
            else if (s.equals("weapon"))
                weapon = true;
        }
        if (weapon)
        {
            if (keyPair[0] >= 450)
                addEnchantment(tool, Enchantment.looting, 3);
            else if (keyPair[0] >= 300)
                addEnchantment(tool, Enchantment.looting, 2);
            else if (keyPair[0] >= 100)
                addEnchantment(tool, Enchantment.looting, 1);
        }
        if (harvest)
        {
            if (keyPair[0] >= 450)
                addEnchantment(tool, Enchantment.fortune, 3);
            else if (keyPair[0] >= 300)
                addEnchantment(tool, Enchantment.fortune, 2);
            else if (keyPair[0] >= 100)
                addEnchantment(tool, Enchantment.fortune, 1);
        }

        updateModTag(tool, keyPair);
    }

    public void midStreamModify (ItemStack tool, ToolCore toolItem)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        if (!tags.hasKey(key))
            return;

        int keyPair[] = tags.getIntArray(key);
        if (keyPair[0] == max)
            return;

        if (random.nextInt(50) == 0)
        {
            keyPair[0] += 1;
            tags.setIntArray(key, keyPair);
            updateModTag(tool, keyPair);
        }

        List list = Arrays.asList(toolItem.getTraits());
        if (list.contains("weapon"))
        {
            if (keyPair[0] >= 450)
            {
                addEnchantment(tool, Enchantment.looting, 3);
            }
            else if (keyPair[0] >= 350)
            {
                int chance = keyPair[0] - 300;
                if (random.nextInt(1000 - chance) == 0)
                    addEnchantment(tool, Enchantment.looting, 3);
            }
            else if (keyPair[0] >= 125)
            {
                int chance = keyPair[0] - 175;
                if (random.nextInt(600 - chance) == 0)
                    addEnchantment(tool, Enchantment.looting, 2);
            }
            else if (keyPair[0] >= 10)
            {
                int chance = keyPair[0] - 25;
                if (random.nextInt(250 - chance) == 0)
                    addEnchantment(tool, Enchantment.looting, 1);
            }
        }

        if (list.contains("harvest"))
        {
            if (keyPair[0] >= 450)
            {
                addEnchantment(tool, Enchantment.fortune, 3);
            }
            else if (keyPair[0] >= 350)
            {
                int chance = keyPair[0] - 300;
                if (random.nextInt(1000 - chance) == 0)
                    addEnchantment(tool, Enchantment.fortune, 3);
            }
            else if (keyPair[0] >= 125)
            {
                int chance = keyPair[0] - 175;
                if (random.nextInt(600 - chance) == 0)
                    addEnchantment(tool, Enchantment.fortune, 2);
            }
            else if (keyPair[0] >= 10)
            {
                int chance = keyPair[0] - 25;
                if (random.nextInt(250 - chance) == 0)
                    addEnchantment(tool, Enchantment.fortune, 1);
            }
        }
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

    void updateModTag (ItemStack tool, int[] keys)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        String tip = "ModifierTip" + keys[1];
        String modName = "\u00a79Lapis (" + keys[0] + "/" + max + ")";
        tags.setString(tip, modName);
    }

    public boolean validType (ToolCore tool)
    {
        List list = Arrays.asList(tool.getTraits());
        return list.contains("weapon") || list.contains("harvest");
    }
}
