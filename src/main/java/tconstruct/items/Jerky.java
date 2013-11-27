package tconstruct.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Jerky extends SpecialFood
{
    public static String[] textureNames = new String[] { "beef", "chicken", "pork", "mutton", "fish", "monster" };
    public static String[] itemNames = new String[] { "jerky.beef", "jerky.chicken", "jerky.pig", "jerky.sheep", "jerky.fish", "jerky.zombie" };
    public static int[] hunger = new int[] { 8, 6, 8, 6, 5, 4 };
    public static float[] saturation = new float[] { 1.0f, 0.8f, 1.0f, 0.8f, 0.8f, 0.4f };
    public static int[] overhaulHunger = new int[] { 1, 1, 1, 1, 1, 1 };

    public Jerky(int id, boolean hungerOverhaul)
    {
        super(id, hungerOverhaul ? overhaulHunger : hunger, saturation, itemNames, textureNames);
        this.setHasSubtypes(true);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        this.icons = new Icon[iconNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:food/jerky_" + iconNames[i]);
        }
    }

    public String getUnlocalizedName (ItemStack stack)
    {
        int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, unlocalizedNames.length);
        return "item.tconstruct." + unlocalizedNames[arr];
    }
}
