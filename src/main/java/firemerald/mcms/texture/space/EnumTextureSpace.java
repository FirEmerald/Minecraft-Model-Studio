package firemerald.mcms.texture.space;

import firemerald.mcms.texture.Color;

public enum EnumTextureSpace
{
	DIFFUSE("diffuse", "", Color.WHITE),
	NORMAL_LAB_PBR("normalLabPBR", "_n", Color.NORMALS),
	SPECULAR_LAB_PBR("specularLabPBR", "_s", Color.SPECULARS),
	NORMAL_OLD_PBR("normalOldPBR", "_n", Color.NORMALS),
	SPECULAR_OLD_PBR("specularOldPBR", "_s", Color.SPECULARS),
	EMISSIVE_OLD_PBR("emissiveOldPBR", "_e", Color.EMMISIVES);

	public final String name, suffix;
	public final Color defaultColor;
	
	EnumTextureSpace(String name, String suffix, Color defaultColor)
	{
		this.name = name;
		this.suffix = suffix;
		this.defaultColor = defaultColor;
	}
}