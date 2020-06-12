package firemerald.mcms.gui.popups.model;

import java.util.ArrayList;
import java.util.List;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.FluidRenderEffect;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.gui.GuiPopup;
import firemerald.mcms.gui.components.ComponentFloatingLabel;
import firemerald.mcms.gui.components.DropdownButton;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.text.ComponentIncrementFloat;
import firemerald.mcms.gui.components.text.ComponentIncrementInt;
import firemerald.mcms.gui.components.text.ComponentText;
import firemerald.mcms.gui.components.text.ComponentTextFloat;
import firemerald.mcms.gui.components.text.ComponentTextInt;
import firemerald.mcms.gui.decoration.DecoPane;

public class GuiPopupFluid extends GuiPopup
{
	public final Bone parent;
	public final DecoPane pane;
	public final ComponentText name;
	public final DropdownButton nameOptions;
	public final ComponentFloatingLabel indexLabel;
	public final ComponentTextInt index;
	public final ComponentIncrementInt indexUp, indexDown;
	public final ComponentFloatingLabel sizeLabel;
	public final ComponentTextFloat sizeX;
	public final ComponentIncrementFloat sizeXUp, sizeXDown;
	public final ComponentTextFloat sizeY;
	public final ComponentIncrementFloat sizeYUp, sizeYDown;
	public final ComponentTextFloat sizeZ;
	public final ComponentIncrementFloat sizeZUp, sizeZDown;
	public final ComponentFloatingLabel marginLabel;
	public final ComponentTextFloat margin;
	public final ComponentIncrementFloat marginUp, marginDown;
	public final StandardButton ok, cancel;
	
	public GuiPopupFluid(Bone parent)
	{
		this.parent = parent;
		Project project = Main.instance.project;
		final int cw = 180;
		final int ch = 140;
		final int cx = 20;
		final int cy = 20;
		this.addElement(pane = new DecoPane(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20, 2, 16));
		int y = cy;
		final String[] names;
		{
			List<String> possible = new ArrayList<>();
			possible.add("new fluid");
			project.getDisplayBoneNames(parent, possible);
			names = possible.toArray(new String[possible.size()]);
		}
		this.addElement(name = new ComponentText(cx, y, cx + cw - 20, y + 20, Main.instance.fontMsg, names[0], text -> names[0] = text));
		this.addElement(nameOptions = new DropdownButton(cx + cw - 20, y, cx + cw, y + 20, name, names, (ind, val) -> name.setText(val)));
		y += 20;
		this.addElement(indexLabel = new ComponentFloatingLabel(cx, y, cx + 33, y + 20, Main.instance.fontMsg, "index"));
		this.addElement(index = new ComponentTextInt(cx + 33, y, cx + cw - 10, y + 20, Main.instance.fontMsg, 0, 0, Integer.MAX_VALUE));
		this.addElement(indexUp = new ComponentIncrementInt(cx + cw - 10, y, index, 1));
		this.addElement(indexDown = new ComponentIncrementInt(cx + cw - 10, y + 10, index, -1));
		y += 20;
		this.addElement(sizeLabel = new ComponentFloatingLabel(cx, y, cx + cw, y + 20, Main.instance.fontMsg, "size"));
		y += 20;
		this.addElement(sizeX = new ComponentTextFloat(cx, y, cx + (cw / 3) - 10, y + 20, Main.instance.fontMsg, 1, 0, Float.POSITIVE_INFINITY));
		this.addElement(sizeXUp = new ComponentIncrementFloat(cx + (cw / 3) - 10, y, sizeX, 1f));
		this.addElement(sizeXDown = new ComponentIncrementFloat(cx + (cw / 3) - 10, y + 10, sizeX, -1f));
		this.addElement(sizeY = new ComponentTextFloat(cx + (cw / 3), y, cx + (cw * 2 / 3) - 10, y + 20, Main.instance.fontMsg, 1, 0, Float.POSITIVE_INFINITY));
		this.addElement(sizeYUp = new ComponentIncrementFloat(cx + (cw * 2 / 3) - 10, y, sizeY, 1f));
		this.addElement(sizeYDown = new ComponentIncrementFloat(cx + (cw * 2 / 3) - 10, y + 10, sizeY, -1f));
		this.addElement(sizeZ = new ComponentTextFloat(cx + (cw * 2 / 3), y, cx + cw - 10, y + 20, Main.instance.fontMsg, 1, 0, Float.POSITIVE_INFINITY));
		this.addElement(sizeZUp = new ComponentIncrementFloat(cx + cw - 10, y, sizeZ, 1f));
		this.addElement(sizeZDown = new ComponentIncrementFloat(cx + cw - 10, y + 10, sizeZ, -1f));
		y += 20;
		this.addElement(marginLabel = new ComponentFloatingLabel(cx, y, cx + 46, y + 20, Main.instance.fontMsg, "margin"));
		this.addElement(margin = new ComponentTextFloat(cx + 46, y, cx + cw - 10, y + 20, Main.instance.fontMsg, 0.00390625f, 0, Float.POSITIVE_INFINITY));
		this.addElement(marginUp = new ComponentIncrementFloat(cx + cw - 10, y, margin, 0.00390625f));
		this.addElement(marginDown = new ComponentIncrementFloat(cx + cw - 10, y + 10, margin, -0.00390625f));
		
		this.addElement(ok = new StandardButton(cx, cy + ch - 20, cx + 80, cy + ch, 1, 4, "add", this::apply));
		this.addElement(cancel = new StandardButton(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch, 1, 4, "cancel", this::deactivate));
	}

	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		final int cw = 180;
		final int ch = 140;
		final int cx = (w - cw) / 2;
		final int cy = (h - ch) / 2;
		pane.setSize(cx - 20, cy - 20, cx + cw + 20, cy + ch + 20);
		int y = cy;
		name.setSize(cx, y, cx + cw - 20, y + 20);
		nameOptions.setSize(cx + cw - 20, y, cx + cw, y + 20);
		y += 20;
		indexLabel.setSize(cx, y, cx + 37, y + 20);
		index.setSize(cx + 37, y, cx + cw - 10, y + 20);
		indexUp.setPosition(cx + cw - 10, y);
		indexDown.setPosition(cx + cw - 10, y + 10);
		y += 20;
		sizeLabel.setSize(cx, y, cx + cw, y + 20);
		y += 20;
		sizeX.setSize(cx, y, cx + (cw / 3) - 10, y + 20);
		sizeXUp.setPosition(cx + (cw / 3) - 10, y);
		sizeXDown.setPosition(cx + (cw / 3) - 10, y + 10);
		sizeY.setSize(cx + (cw / 3), y, cx + (cw * 2 / 3) - 10, y + 20);
		sizeYUp.setPosition(cx + (cw * 2 / 3) - 10, y);
		sizeYDown.setPosition(cx + (cw * 2 / 3) - 10, y + 10);
		sizeZ.setSize(cx + (cw * 2 / 3), y, cx + cw - 10, y + 20);
		sizeZUp.setPosition(cx + cw - 10, y);
		sizeZDown.setPosition(cx + cw - 10, y + 10);
		y += 20;
		marginLabel.setSize(cx, y, cx + 46, y + 20);
		margin.setSize(cx + 46, y, cx + cw - 10, y + 20);
		marginUp.setPosition(cx + cw - 10, y);
		marginDown.setPosition(cx + cw - 10, y + 10);
		
		ok.setSize(cx, cy + ch - 20, cx + 46, cy + ch);
		cancel.setSize(cx + cw - 80, cy + ch - 20, cx + cw, cy + ch);
	}
	
	@Override
	public void doRender(float mx, float my, boolean canHover)
	{
		Main main = Main.instance;
		main.textureManager.unbindTexture();
		main.shader.setColor(0, 0, 0, .5f);
		main.screen.render();
		main.shader.setColor(1, 1, 1, 1);
	}
	
	public void apply()
	{
		Main.instance.project.onAction();
		deactivate();
		IRigged<?> rigged = Main.instance.project.getRig();
		FluidRenderEffect newBone = new FluidRenderEffect(name.getText(), parent, new Transformation(), index.getVal(), sizeX.getVal(), sizeY.getVal(), sizeZ.getVal(), margin.getVal());
		if (parent != null) rigged.updateBonesList();
		else rigged.addChild(newBone);
		Main main = Main.instance;
		main.project.updateSkeletonLocalAlt();
		main.setEditing(newBone);
		Main.instance.editorPanes.selector.updateBase();
	}
}