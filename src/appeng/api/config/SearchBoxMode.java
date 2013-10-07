package appeng.api.config;

import net.minecraft.src.ModLoader;

public enum SearchBoxMode implements IConfigEnum<ActionItems> {
	Autosearch,
	Standard,
	NEIAutoSearch,
	NEIStandard;

	@Override
	public IConfigEnum[] getValues() {
		if ( ModLoader.isModLoaded( "NotEnoughItems" ) )
			return values();
		return new SearchBoxMode[]{ Autosearch, Standard };
	}

	@Override
	public String getName() {
		return "SearchBoxMode";
	}
}