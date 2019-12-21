package firemerald.mcms.gui.main.components.elements;

import java.io.File;

import firemerald.mcms.Main;
import firemerald.mcms.api.model.ObjData;
import firemerald.mcms.gui.popups.GuiPopupMessageOK;
import firemerald.mcms.model.IComponentParent;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.Textures;
import firemerald.mcms.util.font.FormattedText;

public class ButtonAddMesh extends EditableButton
{
	private IComponentParent parent;
	
	public ButtonAddMesh(int x, int y)
	{
		super(x, y);
	}

	@Override
	public String getTexture()
	{
		return Textures.EDITABLE_ADD_MESH;
	}
	
	@Override
	public void onRelease()
	{
		File meshFile = FileUtils.getOpenFile("obj", null);
		if (meshFile != null)
		{
			System.out.println(meshFile);
			ObjData objData = ObjData.tryLoad(meshFile);
			FormattedText msg = null;
			if (objData != null)
			{
				if (objData.groupObjects.size() != 0)
				{
					/** TODO load mesh, select groups
					ComponentBox box = new ComponentBox(component, "new box");
					Main main = Main.instance;
					if (main.editing != null) main.editing.onDeselect(editorPanes);
					(main.editing = box).onSelect(editorPanes);
					selector.updateBase();
					**/
				}
				else (msg = new FormattedText(meshFile.toString(), getTheme().getTextColor(), false, true, false, false, Main.instance.fontMsg)).append(" contains no meshes.", null, null, false, null, null, null);
			}
			else (msg = new FormattedText(meshFile.toString(), getTheme().getTextColor(), false, true, false, false, Main.instance.fontMsg)).append(" is not a valid Wavefront object file.", null, null, false, null, null, null);
			if (msg != null) new GuiPopupMessageOK(msg).activate();
		}
	}
	
	public void setParent(IComponentParent parent)
	{
		this.parent = parent;
	}

	@Override
	public boolean isEnabled()
	{
		return this.parent != null;
	}
}