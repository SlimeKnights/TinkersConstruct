package tconstruct.library.armor;

public enum EnumArmorPart
{

    HELMET(0), CHEST(1), PANTS(2), SHOES(3);

    private final int partID;

    private EnumArmorPart(int partID)
    {
        this.partID = partID;
    }

    public int getPartId ()
    {
        return this.partID;
    }
}