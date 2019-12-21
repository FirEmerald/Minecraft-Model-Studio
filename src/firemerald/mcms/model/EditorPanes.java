package firemerald.mcms.model;

import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.GuiSection;
import firemerald.mcms.gui.main.components.elements.ButtonAddBone;
import firemerald.mcms.gui.main.components.elements.ButtonAddBox;
import firemerald.mcms.gui.main.components.elements.ButtonAddMesh;
import firemerald.mcms.gui.main.components.elements.ButtonCopy;
import firemerald.mcms.gui.main.components.elements.ButtonRemove;
import firemerald.mcms.gui.main.components.elements.ComponentEditSelector;

public class EditorPanes
{
	public final GuiSection editor;
	public final ComponentEditSelector selector;
	public final ButtonAddBone addBone;
	public final ButtonAddBox addBox;
	public final ButtonAddMesh addMesh;
	public final ButtonCopy copy;
	public final ButtonRemove remove;

	//add bone | add box | add mesh | copy | remove
	public EditorPanes(GuiElementContainer editor, GuiElementContainer buttons, ComponentEditSelector selector)
	{
		this.editor = new GuiSection(editor, 0, 0);
		this.selector = selector;
		buttons.addElement(addBone = new ButtonAddBone(0, 0));
		buttons.addElement(addBox = new ButtonAddBox(32, 0));
		buttons.addElement(addMesh = new ButtonAddMesh(64, 0));
		buttons.addElement(copy = new ButtonCopy(96, 0));
		buttons.addElement(remove = new ButtonRemove(128, 0));
	}
	
	public void setOffsets(int editorX, int editorY, int buttonsX, int buttonsY)
	{
		editor.setMin(editorX, editorY);
		addBone.setSize(buttonsX, buttonsY, buttonsX + 32, buttonsY + 32);
		addBox.setSize(buttonsX + 32, buttonsY, buttonsX + 64, buttonsY + 32);
		addMesh.setSize(buttonsX + 64, buttonsY, buttonsX + 96, buttonsY + 32);
		copy.setSize(buttonsX + 96, buttonsY, buttonsX + 128, buttonsY + 32);
		remove.setSize(buttonsX + 128, buttonsY, buttonsX + 160, buttonsY + 32);
	}
}