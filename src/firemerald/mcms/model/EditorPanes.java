package firemerald.mcms.model;

import firemerald.mcms.api.model.IModel;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.components.model.*;
import firemerald.mcms.gui.components.model.selector.ComponentEditSelector;

public class EditorPanes
{
	public IModel base;
	public final GuiElementContainer editor;
	public float editorX, editorY;
	public ComponentEditSelector selector;
	public final ButtonAddBone addBone;
	public final ButtonAddBox addBox;
	public final ButtonAddMesh addMesh;
	public final ButtonCopy copy;
	public final ButtonRemove remove;

	//add bone | add box | add mesh | copy | remove
	public EditorPanes(GuiElementContainer editor, GuiElementContainer buttons)
	{
		this.editor = editor;
		buttons.addElement(addBone = new ButtonAddBone(0, 0, this));
		buttons.addElement(addBox = new ButtonAddBox(32, 0, this));
		buttons.addElement(addMesh = new ButtonAddMesh(64, 0, this));
		buttons.addElement(copy = new ButtonCopy(96, 0, this));
		buttons.addElement(remove = new ButtonRemove(128, 0, this));
	}
	
	public void setOffsets(float editorX, float editorY, float buttonsX, float buttonsY)
	{
		this.editorX = editorX;
		this.editorY = editorY;
		addBone.setSize(buttonsX, buttonsY, buttonsX + 32, buttonsY + 32);
		addBox.setSize(buttonsX + 32, buttonsY, buttonsX + 64, buttonsY + 32);
		addMesh.setSize(buttonsX + 64, buttonsY, buttonsX + 96, buttonsY + 32);
		copy.setSize(buttonsX + 96, buttonsY, buttonsX + 128, buttonsY + 32);
		remove.setSize(buttonsX + 128, buttonsY, buttonsX + 160, buttonsY + 32);
	}
}