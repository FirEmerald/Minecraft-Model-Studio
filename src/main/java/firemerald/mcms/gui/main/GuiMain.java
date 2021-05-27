package firemerald.mcms.gui.main;

import java.io.File;
import java.io.IOException;

import org.joml.Vector3f;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.gui.GuiScreen;
import firemerald.mcms.gui.main.components.*;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.model.ComponentBox;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.ProjectModel;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.texture.FileTexture;
import firemerald.mcms.texture.space.Material;
import firemerald.mcms.util.ApplicationState.EnumLayout;
import firemerald.mcms.util.GuiUpdate;

public class GuiMain extends GuiScreen
{
	public final ComponentTitleBar titleBar;
	public final ComponentEditorPanel editorPanel;
	public final ComponentToolsPanel toolsPanel;
	public final ComponentModelView modelView;
	public final ComponentTexturePanel texturePanel;
	public final ComponentElementsPanel elementsPanel;
	public final ComponentAnimationBar animationBar;
	
	public GuiMain()
	{
		this.addElement(titleBar = new ComponentTitleBar(0, 0, 1280, 16, this));
		this.addElement(editorPanel = new ComponentEditorPanel(0, 16, 300, 340, this));
		this.addElement(toolsPanel = new ComponentToolsPanel(0, 340, 300, 640, this));
		this.addElement(modelView = new ComponentModelView(300, 16, 1006, 64, this));
		this.addElement(texturePanel = new ComponentTexturePanel(1006, 16, 1280, 306, this));
		this.addElement(elementsPanel = new ComponentElementsPanel(1006, 306, 1280, 640, this));
		this.addElement(animationBar = new ComponentAnimationBar(0, 640, 1280, 720, this));
		Main.instance.editorPanes = new EditorPanes(editorPanel, elementsPanel, elementsPanel.selector);

		Project project = Main.instance.project;
		try
		{
			Material tex = new Material(new FileTexture(new File("texture.png")));
			project.addTexture("test", tex);
			project.setTextureSize(tex.getDiffuse().w, tex.getDiffuse().h);
		}
		catch (IOException e1)
		{
			GuiPopupException.onException("Couldn't load default texture texture.png", e1);
		}

		RenderObjectComponents.Actual test1, test2, test3, test4;
		ComponentBox box;
		ProjectModel model = new ProjectModel();
		test1 = new RenderObjectComponents.Actual("test1", new Transformation(new Vector3f(0, -24f, 0)), null);
		box = new ComponentBox(test1, "box1");
		box.lengthX(16);
		box.lengthY(16);
		box.lengthZ(16);
		box.offX(-8);
		box.offY(-8);
		box.offZ(-8);
		/*
		box.setTexSizeU(64);
		box.setTexSizeV(32);
		*/
		test2 = new RenderObjectComponents.Actual("test2", new Transformation(new Vector3f(0, 16, 0)), test1);
		box = new ComponentBox(test2, "box2");
		box.lengthX(16);
		box.lengthY(16);
		box.lengthZ(16);
		box.offX(-8);
		box.offY(-8);
		box.offZ(-8);
		/*
		box.setTexSizeU(64);
		box.setTexSizeV(32);
		*/
		test3 = new RenderObjectComponents.Actual("test3", new Transformation(new Vector3f(0, 16, 0)), test2);
		box = new ComponentBox(test3, "box3");
		box.lengthX(16);
		box.lengthY(16);
		box.lengthZ(16);
		box.offX(-8);
		box.offY(-8);
		box.offZ(-8);
		/*
		box.setTexSizeU(64);
		box.setTexSizeV(32);
		*/
		test4 = new RenderObjectComponents.Actual("test4", new Transformation(new Vector3f(0, 16, 0)), test3);
		box = new ComponentBox(test4, "box4");
		box.lengthX(16);
		box.lengthY(16);
		box.lengthZ(16);
		box.offX(-8);
		box.offY(-8);
		box.offZ(-8);
		/*
		box.setTexSizeU(64);
		box.setTexSizeV(32);
		*/
		model.setBase(test1);
		project.addModel("test", model);
		project.clearActions();
		project.clearNeedsSave();
		this.onGuiUpdate(GuiUpdate.PROJECT);
	}
	
	@Override
	public void setSize(int w, int h)
	{
		super.setSize(w, h);
		titleBar.setSize(0, 0, w, 16);
		editorPanel.setSize(0, 16, 300, h - 312);
		toolsPanel.setSize(0, h - 312, 300, h - 112);
		modelView.setSize(300, 16, w - 274, h - 112);
		texturePanel.setSize(w - 274, 16, w, 322);
		elementsPanel.setSize(w - 274, 322, w, h - 112);
		animationBar.setSize(0, h - 112, w, h);
		Main.instance.editorPanes.setOffsets(0, 0, 0, Main.instance.state.getLayout() == EnumLayout.LEGACY ? 0 : 16);
	}
}