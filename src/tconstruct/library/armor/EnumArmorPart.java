package tconstruct.library.armor;

public enum EnumArmorPart {

	HELMET(0), CHESTPLATE(1), LEGGINGS(2), BOOTS(3);

	private final int partID;

	private EnumArmorPart(int partID) {
		this.partID = partID;
	}

	public int getPartId() {
		return this.partID;
	}
}