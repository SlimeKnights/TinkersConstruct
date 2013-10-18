package appeng.api.config;


public enum PowerUnits implements IConfigEnum<PowerUnits> {
	AE,
	MJ,
	EU,
	UE;

	@Override
	public IConfigEnum[] getValues() {
		return values();
	}

	@Override
	public String getName() {
		return "PowerUnits";
	}
}