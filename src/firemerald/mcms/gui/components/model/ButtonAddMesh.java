package firemerald.mcms.gui.components.model;

import java.io.File;

import firemerald.mcms.api.model.ObjData;
import firemerald.mcms.gui.popups.PopupMessageOK;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IComponentParent;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.Textures;

public class ButtonAddMesh extends EditableButton
{
	private IComponentParent parent;
	
	public ButtonAddMesh(float x, float y, EditorPanes editorPanes)
	{
		super(x, y, editorPanes);
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
			if (objData != null && objData.groupObjects.size() != 0)
			{
				/** TODO load mesh, select groups
				ComponentBox box = new ComponentBox(component, "new box");
				Main main = Main.instance;
				if (main.editing != null) main.editing.onDeselect(editorPanes);
				(main.editing = box).onSelect(editorPanes);
				selector.updateBase();
				**/
			}
			else new PopupMessageOK(meshFile + " is not a valid Wavefront object file, or is an empty model.").activate();
		}
	}
	
	public void setParent(IComponentParent parent)
	{
		enabled = (this.parent = parent) != null;
	}
}