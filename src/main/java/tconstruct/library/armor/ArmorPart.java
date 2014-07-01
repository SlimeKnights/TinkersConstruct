package tconstruct.library.armor;

public enum ArmorPart
{

    Head(0), Chest(1), Legs(2), Feet(3);

    private final int partID;

    private ArmorPart(int partID)
    {
        this.partID = partID;
    }

    public int getPartId ()
    {
        return this.partID;
    }
}