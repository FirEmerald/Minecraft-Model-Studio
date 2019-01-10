package firemerald.mcms.gui.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.data.BinaryFormat;
import firemerald.mcms.api.data.Element;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.math.Vec3;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.MultiModel;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.gui.GuiScreen;
import firemerald.mcms.gui.components.model.ComponentModelViewer;
import firemerald.mcms.gui.components.model.selector.ComponentEditSelector;
import firemerald.mcms.gui.components.scrolling.*;
import firemerald.mcms.model.ComponentBox;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IEditable;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.util.FileUtils;

public class GuiModel extends GuiScreen
{
	public final ComponentEditSelector selector;
	public final ScrollBar selectorScrollBar;
	public final ScrollUp selectorScrollUp;
	public final ScrollDown selectorScrollDown;
	public final ScrollBarH selectorScrollBarH;
	public final ScrollLeft selectorScrollLeft;
	public final ScrollRight selectorScrollRight;
	public final ComponentModelViewer modelViewer;
	public final EditorPanes editorPanes;
	
	public GuiModel(Texture tex)
	{
		RenderObjectComponents test1, test2, test3, test4;
		ComponentBox box;
		MultiModel model = new MultiModel();
		test1 = new RenderObjectComponents("test1", new Transformation(new Vec3(0, -1.5f, 0)));
		box = new ComponentBox(test1, "box1");
		box.lengthX(1);
		box.lengthY(1);
		box.lengthZ(1);
		box.offX(-.5f);
		box.offY(-.5f);
		box.offZ(-.5f);
		box.setTexSizeU(64);
		box.setTexSizeV(32);
		test2 = new RenderObjectComponents("test2", new Transformation(new Vec3(0, 1, 0)), test1);
		box = new ComponentBox(test2, "box2");
		box.lengthX(1);
		box.lengthY(1);
		box.lengthZ(1);
		box.offX(-.5f);
		box.offY(-.5f);
		box.offZ(-.5f);
		box.setTexSizeU(64);
		box.setTexSizeV(32);
		test3 = new RenderObjectComponents("test3", new Transformation(new Vec3(0, 1, 0)), test2);
		box = new ComponentBox(test3, "box3");
		box.lengthX(1);
		box.lengthY(1);
		box.lengthZ(1);
		box.offX(-.5f);
		box.offY(-.5f);
		box.offZ(-.5f);
		box.setTexSizeU(64);
		box.setTexSizeV(32);
		test4 = new RenderObjectComponents("test4", new Transformation(new Vec3(0, 1, 0)), test3);
		box = new ComponentBox(test4, "box4");
		box.lengthX(1);
		box.lengthY(1);
		box.lengthZ(1);
		box.offX(-.5f);
		box.offY(-.5f);
		box.offZ(-.5f);
		box.setTexSizeU(64);
		box.setTexSizeV(32);
		model.setBase(test1);
		FileUtils.saveTextFile(RenderObjectComponents.createObj(model).optimize().toString(), new File("testing.obj"), Charset.defaultCharset());

		AbstractElement root, modelEl;
		
		Document doc = FileUtil.createXML();
		org.w3c.dom.Element rootEl = doc.createElement("MCMS");
		doc.appendChild(rootEl);
		root = new Element("MCMS");
		modelEl = root.addChild("model");
		modelEl.setString("name", "testing");
		for (Bone bone : model.base) bone.addToXML(modelEl);
		try
		{
			root.toElement().saveXML(new File("testing.mcms"));
		}
		catch (IOException | TransformerException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			root.toElement().saveBinary(new File("testing.mcms.bin"), BinaryFormat.UTF_8);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			root = FileUtil.readFile(new File("testing.mcms"));
			for (AbstractElement child : root.getChildren()) if (child.getName().equals("model"))
			{
				modelEl = child;
				model.loadFromXML(modelEl);
				break;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		/*
		Document doc = XMLUtil.createXML();
		org.w3c.dom.Element rootEl = doc.createElement("MCMS");
		doc.appendChild(rootEl);
		root = new W3CElement(rootEl);
		modelEl = root.addChild("model");
		modelEl.setAttribute("name", "testing");
		for (Bone bone : model.base) bone.addToXML(modelEl);
		try
		{
			XMLUtil.saveXML(doc, new File("testing2.mcms"));
		}
		catch (TransformerException | IOException e)
		{
			e.printStackTrace();
		}
		*/
		editorPanes = new EditorPanes(this, this);
		editorPanes.base = model;
		this.guiElements.add(selector = new ComponentEditSelector(0, 32, 300 - 16, 300, editorPanes));
		this.guiElements.add(selectorScrollBar = new ScrollBar(300 - 16, 48, 300, 300 - 16, selector));
		this.guiElements.add(selectorScrollUp = new ScrollUp(300 - 16, 32, 300, 48, selector));
		this.guiElements.add(selectorScrollDown = new ScrollDown(300 - 16, 300 - 16, 300, 300, selector));
		selector.setScrollBar(selectorScrollBar);
		this.guiElements.add(selectorScrollBarH = new ScrollBarH(300 - 16, 48, 300, 300 - 16, selector));
		this.guiElements.add(selectorScrollLeft = new ScrollLeft(300 - 16, 32, 300, 48, selector));
		this.guiElements.add(selectorScrollRight = new ScrollRight(300 - 16, 300 - 16, 300, 300, selector));
		selector.setScrollBarH(selectorScrollBarH);
		this.guiElements.add(modelViewer = new ComponentModelViewer(0, 0, 300, 300, editorPanes, tex));
		modelViewer.model = model;
		selector.setModel(model);
		/*
		this.guiElements.add(new Test(10, 0, 10, 10, 0));
		this.guiElements.add(new Test(20, 10, 10, 10, 1));
		this.guiElements.add(new Test(10, 20, 10, 10, 2));
		this.guiElements.add(new Test(0, 10, 10, 10, 3));

		this.guiElements.add(new Test(51, 0, 21, 21, 0));
		this.guiElements.add(new Test(72, 21, 21, 21, 1));
		this.guiElements.add(new Test(51, 42, 21, 21, 2));
		this.guiElements.add(new Test(30, 21, 21, 21, 3));
		*/
	}
	/*
	public static class Test implements IGuiElement
	{
		static final int W = 10, H = 10, X = 10, Y = 0;
		final Mesh m;
		final DirectionButtonFormat id;
		
		public Test(int x, int y, int w, int h, int dir)
		{
			this.m = new Mesh(x, y, x + w, y + h, 0, 0, 0, 1, 1);
			this.id = new DirectionButtonFormat(w, h, 1, dir);
		}
		
		@Override
		public void tick(float mx, float my, float deltaTime) {}

		@Override
		public void render(float mx, float my, boolean canHover) 
		{
			Main.instance.theme.bindScrollButton(id);
			m.render();
		}
	}
	*/
	@Override
	public void setSize(int w, int h)
	{
		final float selectorW = 300, selectorH = 300;
		final float btnH = 32;
		final float scrollS = 16;
		selector.setSize(w - selectorW, h - selectorH + btnH, w - scrollS, h - scrollS);
		selectorScrollBar.setSize(w - scrollS, h - selectorH + btnH + scrollS, w, h - scrollS * 2);
		selectorScrollUp.setSize(w - scrollS, h - selectorH + btnH, w, h - selectorH + btnH + scrollS);
		selectorScrollDown.setSize(w - scrollS, h - scrollS * 2, w, h - scrollS);
		selectorScrollBarH.setSize(w - selectorW + scrollS, h - scrollS, w - scrollS * 2, h);
		selectorScrollLeft.setSize(w - selectorW, h - scrollS, w - selectorW + scrollS, h);
		selectorScrollRight.setSize(w - scrollS * 2, h - scrollS, w - scrollS, h);
		modelViewer.setSize(300, 0, w - selectorW, h);
		editorPanes.setOffsets(0, 0, w - selectorW, h - selectorH);
		IEditable editing;
		if ((editing = Main.instance.editing) != null)
		{
			editing.onDeselect(editorPanes);
			editing.onSelect(editorPanes);
		}
	}
}