package tconstruct.blocks.logic;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityEnchantmentTable;

import java.text.DecimalFormat;
import java.util.Random;

public class EssenceExtractorLogic extends TileEntityEnchantmentTable
{
    /** Used by the render to make the book 'bounce' */
    public int tickCount;

    /** Value used for determining how the page flip should look. */
    public float pageFlip;

    /** The last tick's pageFlip value. */
    public float pageFlipPrev;
    public float field_70373_d;
    public float field_70374_e;

    /** The amount that the book is open. */
    public float bookSpread;

    /** The amount that the book is open. */
    public float bookSpreadPrev;
    public float bookRotation2;
    public float bookRotationPrev;
    public float bookRotation;
    private static Random rand = new Random();
    private String field_94136_s;

    public int essenceAmount;
    static DecimalFormat df = new DecimalFormat("##.##");

    /*public EssenceExtractorLogic()
    {
        df.setRoundingMode(RoundingMode.DOWN);
    }*/

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT (NBTTagCompound tags)
    {
        super.writeToNBT(tags);

        if (this.func_94135_b())
        {
            tags.setString("CustomName", this.field_94136_s);
        }
        tags.setInteger("Essence", essenceAmount);
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT (NBTTagCompound tags)
    {
        super.readFromNBT(tags);

        if (tags.hasKey("CustomName"))
        {
            this.field_94136_s = tags.getString("CustomName");
        }

        essenceAmount = tags.getInteger("Essence");
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    public void updateEntity ()
    {
        super.updateEntity();
        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation2;
        EntityPlayer entityplayer = this.worldObj.getClosestPlayer((double) ((float) this.xCoord + 0.5F), (double) ((float) this.yCoord + 0.5F), (double) ((float) this.zCoord + 0.5F), 3.0D);

        if (entityplayer != null)
        {
            double d0 = entityplayer.posX - (double) ((float) this.xCoord + 0.5F);
            double d1 = entityplayer.posZ - (double) ((float) this.zCoord + 0.5F);
            this.bookRotation = (float) Math.atan2(d1, d0);
            this.bookSpread += 0.1F;

            if (this.bookSpread < 0.5F || rand.nextInt(40) == 0)
            {
                float f = this.field_70373_d;

                do
                {
                    this.field_70373_d += (float) (rand.nextInt(4) - rand.nextInt(4));
                } while (f == this.field_70373_d);
            }
        }
        else
        {
            this.bookRotation += 0.02F;
            this.bookSpread -= 0.1F;
        }

        while (this.bookRotation2 >= (float) Math.PI)
        {
            this.bookRotation2 -= ((float) Math.PI * 2F);
        }

        while (this.bookRotation2 < -(float) Math.PI)
        {
            this.bookRotation2 += ((float) Math.PI * 2F);
        }

        while (this.bookRotation >= (float) Math.PI)
        {
            this.bookRotation -= ((float) Math.PI * 2F);
        }

        while (this.bookRotation < -(float) Math.PI)
        {
            this.bookRotation += ((float) Math.PI * 2F);
        }

        float f1;

        for (f1 = this.bookRotation - this.bookRotation2; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F))
        {
            ;
        }

        while (f1 < -(float) Math.PI)
        {
            f1 += ((float) Math.PI * 2F);
        }

        this.bookRotation2 += f1 * 0.4F;

        if (this.bookSpread < 0.0F)
        {
            this.bookSpread = 0.0F;
        }

        if (this.bookSpread > 1.0F)
        {
            this.bookSpread = 1.0F;
        }

        ++this.tickCount;
        this.pageFlipPrev = this.pageFlip;
        float f2 = (this.field_70373_d - this.pageFlip) * 0.4F;
        float f3 = 0.2F;

        if (f2 < -f3)
        {
            f2 = -f3;
        }

        if (f2 > f3)
        {
            f2 = f3;
        }

        this.field_70374_e += (f2 - this.field_70374_e) * 0.9F;
        this.pageFlip += this.field_70374_e;
    }

    public String func_94133_a ()
    {
        return this.func_94135_b() ? this.field_94136_s : "container.enchant";
    }

    public boolean func_94135_b ()
    {
        return this.field_94136_s != null && this.field_94136_s.length() > 0;
    }

    public void func_94134_a (String par1Str)
    {
        this.field_94136_s = par1Str;
    }

    public void addEssence (EntityPlayer player)
    {
        int amount = player.experienceTotal;
        essenceAmount += amount;
        player.addExperienceLevel(-player.experienceLevel - 1);
    }

    public int removeEssence ()
    {
        int amount = this.essenceAmount;
        this.essenceAmount = 0;
        return amount;
    }

    public void getEssenceMessage (EntityPlayer player)
    {
        player.addChatMessage("Total Essence: " + this.essenceAmount);
        player.addChatMessage("Stored Levels: " + getEssencelevels(this.essenceAmount));
    }

    public static String getEssencelevels (int essence)
    {
        if (essence < 255) //0-14
        {
            float ret = essence / 17f;
            return String.valueOf(df.format(ret));
        }
        else if (essence < 825)
        {
            int amount = essence - 255;
            int levels = 15;
            int offset = 3;

            int sub = 17 + offset;
            while (amount - sub >= 0)
            {
                amount -= sub;
                sub += offset;
                levels++;
            }

            float ret = levels + amount / (float) sub;
            return String.valueOf(df.format(ret));
        }
        else
        {
            int amount = essence - 825;
            int levels = 30;
            int offset = 7;

            int sub = 62 + offset;
            while (amount - sub >= 0)
            {
                amount -= sub;
                sub += offset;
                levels++;
            }

            float ret = levels + amount / (float) sub;
            return String.valueOf(df.format(ret));
        }
    }
}
