package firemerald.mcms.model;

import firemerald.mcms.Main;
import firemerald.mcms.gui.GuiElementContainer;
import firemerald.mcms.gui.GuiSection;
import firemerald.mcms.gui.main.components.elements.ButtonAddBone;
import firemerald.mcms.gui.main.components.elements.ButtonAddBox;
import firemerald.mcms.gui.main.components.elements.ButtonAddEffect;
import firemerald.mcms.gui.main.components.elements.ButtonAddFluid;
import firemerald.mcms.gui.main.components.elements.ButtonAddItem;
import firemerald.mcms.gui.main.components.elements.ButtonCopy;
import firemerald.mcms.gui.main.components.elements.ButtonLockModel;
import firemerald.mcms.gui.main.components.elements.ButtonRemove;
import firemerald.mcms.gui.main.components.elements.ComponentEditSelector;
import firemerald.mcms.util.ApplicationState.EnumLayout;

public class EditorPanes
{
	public final GuiSection editor;
	public final ComponentEditSelector selector;
	public final ButtonAddBone addBone;
	public final ButtonAddBox addBox;
	public final ButtonAddItem<?> addItem;
	public final ButtonAddFluid<?> addFluid;
	public final ButtonAddEffect<?> addEffect;
	public final ButtonCopy copy;
	public final ButtonRemove remove;
	public final ButtonLockModel lock;

	//add bone | add item | add box | add mesh | copy | remove
	public EditorPanes(GuiElementContainer editor, GuiElementContainer buttons, ComponentEditSelector selector)
	{
		this.editor = new GuiSection(editor, 0, 0);
		this.selector = selector;
		buttons.addElement(addBone = new ButtonAddBone(0, 0));
		buttons.addElement(addBox = new ButtonAddBox(32, 0));
		buttons.addElement(addItem = new ButtonAddItem<>(64, 0));
		buttons.addElement(addFluid = new ButtonAddFluid<>(96, 0));
		buttons.addElement(addEffect = new ButtonAddEffect<>(128, 0));
		buttons.addElement(copy = new ButtonCopy(160, 0));
		buttons.addElement(remove = new ButtonRemove(192, 0));
		buttons.addElement(lock = new ButtonLockModel(224, 0));
	}
	
	public void setOffsets(int editorX, int editorY, int buttonsX, int buttonsY)
	{
		editor.setMin(editorX, editorY);
		addBone.setSize(buttonsX, buttonsY, buttonsX + 32, buttonsY + 32);
		(Main.instance.state.getLayout() == EnumLayout.LEGACY ? addItem : addBox).setSize(buttonsX + 32, buttonsY, buttonsX + 64, buttonsY + 32);
		addFluid.setSize(buttonsX + 64, buttonsY, buttonsX + 96, buttonsY + 32);
		(Main.instance.state.getLayout() == EnumLayout.LEGACY ? addBox : addItem).setSize(buttonsX + 96, buttonsY, buttonsX + 128, buttonsY + 32);
		addEffect.setSize(buttonsX + 128, buttonsY, buttonsX + 160, buttonsY + 32);
		copy.setSize(buttonsX + 160, buttonsY, buttonsX + 192, buttonsY + 32);
		remove.setSize(buttonsX + 192, buttonsY, buttonsX + 224, buttonsY + 32);
		lock.setSize(buttonsX + 224, buttonsY, buttonsX + 256, buttonsY + 32);
	}
}