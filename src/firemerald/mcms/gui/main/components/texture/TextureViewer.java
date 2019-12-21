package firemerald.mcms.gui.main.components.texture;

import firemerald.mcms.Main;
import firemerald.mcms.Project;
import firemerald.mcms.gui.components.Component;
import firemerald.mcms.gui.components.scrolling.*;
import firemerald.mcms.model.IModelEditable;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.EditorMode;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.RenderUtil;
import firemerald.mcms.util.mesh.Mesh;
import firemerald.mcms.window.api.Cursor;
import firemerald.mcms.window.api.Modifier;

public class TextureViewer extends Component implements IScrollable, IScrollableHorizontal
{
	public int height = 0;
	protected float scroll = 0;
	protected float scrollSize = 0;
	public int width = 0;
	protected float scrollH = 0;
	protected float scrollSizeH = 0;
	public final Mesh border = new Mesh(), inside = new Mesh();
	public ThemeElement rect;
	public float h = 0, w = 0;
	public ScrollBar scrollBar = null;
	public ScrollBarH scrollBarH = null;
	private float scale = 4;
	private float scaleFac = 2;
	
	public TextureViewer(int x1, int y1, int x2, int y2)
	{
		super(x1, y1, x2, y2);
		setSize(x1, y1, x2, y2);
	}
	
	public void setScrollBar(ScrollBar scrollBar)
	{
		this.scrollBar = scrollBar;
		scrollBar.setMaxScroll();
	}
	
	public void setScrollBarH(ScrollBarH scrollBar)
	{
		this.scrollBarH = scrollBar;
		scrollBar.setMaxScroll();
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		h = y2 - y1;
		w = x2 - x1;
		updateScrollSize();
		border.setMesh(x1, y1, x2, y2, 0, 0, 0, 1, 1);
		inside.setMesh(x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0, 0, 0, 1, 1);
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (rect != null) rect.release();
			rect = getTheme().genBox(x2 - x1, y2 - y1, 1);
		}
		else if (reason == GuiUpdate.PROJECT || reason == GuiUpdate.TEXTURE)
		{
			updateComponentSize();
		}
	}
	
	public float getW()
	{
		return (Main.instance.project.getTexture() == null ? Main.instance.project.getTextureWidth() : Main.instance.project.getTexture().w) * scale;
	}
	
	public float getH()
	{
		return (Main.instance.project.getTexture() == null ? Main.instance.project.getTextureHeight() : Main.instance.project.getTexture().h) * scale;
	}
	
	public void updateComponentSize()
	{
		width = (int) getW() + 2;
		height = (int) getH() + 2;
		updateScrollSize();
	}
	
	public void updateScrollSize()
	{
		scrollSize = height - h;
		if (scrollSize < 0) scroll = scrollSize = 0;
		else if (scroll > scrollSize) scroll = scrollSize;
		if (scrollBar != null) scrollBar.setMaxScroll();

		scrollSizeH = width - w;
		if (scrollSizeH < 0) scrollH = scrollSizeH = 0;
		else if (scrollH > scrollSizeH) scrollH = scrollSizeH;
		if (scrollBarH != null) scrollBarH.setMaxScroll();
	}
	
	public double getTexU(float mx)
	{
		return (mx - x1 + scrollH) / getW();
	}
	
	public double getTexV(float my)
	{
		return (my - y1 + scroll) / getH();
	}
	
	@Override
	public boolean canScrollH(float mx, float my)
	{
		return scrollSizeH > 0 || Modifier.CONTROL.isDown(Main.instance.window);
	}
	
	@Override
	public boolean canScrollV(float mx, float my)
	{
		return scrollSize > 0 && !Modifier.CONTROL.isDown(Main.instance.window);
	}

	@Override
	public void onMouseScroll(float mx, float my, float scrollX, float scrollY)
	{
		if (Modifier.CONTROL.isDown(Main.instance.window))
		{
			scaleFac += scrollY * .125f;
			if (scaleFac < -2) scaleFac = -2;
			else if (scaleFac > 4) scaleFac = 4;
			float prevScale = scale;
			scale = (float) Math.pow(2, scaleFac);
			scroll = (my - y1 + scroll) * (scale / prevScale) - (my - y1);
			if (scroll < 0) scroll = 0;
			else if (scroll > scrollSize) scroll = scrollSize;
			scrollH = (mx - x1 + scrollH) * (scale / prevScale) - (mx - x1);
			if (scrollH < 0) scrollH = 0;
			else if (scrollH > scrollSizeH) scrollH = scrollSizeH;
			updateComponentSize();
		}
		else
		{
			scrollH -= scrollX * 4;
			if (scrollH < 0) scrollH = 0;
			else if (scrollH > scrollSizeH) scrollH = scrollSizeH;
			scroll -= scrollY * 4;
			if (scroll < 0) scroll = 0;
			else if (scroll > scrollSize) scroll = scrollSize;
			//TODO if (mouseDown) onDrag(mx, my); //Because it's kinda a drag XD
		}
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		if (Main.instance.project.getTexture() != null && Main.instance.getEditorMode() == EditorMode.TEXTURE) Main.instance.tool.drawOnOverlay(Main.instance.getOverlay(), getTexU(mx), getTexV(my));
	}

	@Override
	public void render(float mx, float my, boolean canHover)
	{
		Main main = Main.instance;
		Project project = main.project;
		float tw = getW(), th = getH();
		Texture tex = project.getTexture();
		rect.bind();
		border.render();
		main.textureManager.unbindTexture();
		main.shader.setColor(1, 1, 1, 1);
		RenderUtil.pushStencil();
		RenderUtil.startStencil(false);
		inside.render();
		RenderUtil.endStencil();
		main.shader.setTexSection(scrollH / tw, scroll / th, (scrollH + w - 2) / tw, (scroll + h - 2) / th);
		project.bindTex();
		//System.out.println(main.project.getTexture());
		if (tex != null && main.getEditorMode() == EditorMode.TEXTURE) main.shader.setOverlayTexture(main.getOverlay());
		main.shader.setClipOutside(true);
		inside.render();
		main.shader.setClipOutside(false);
    	main.shader.setTexIdentity();
    	project.unbindTex();
		if (tex != null && main.getEditorMode() == EditorMode.TEXTURE) main.shader.setOverlayTexture(null);
    	if (main.trace != null && main.trace.hit instanceof IModelEditable)((IModelEditable) main.trace.hit).drawOnTexture(x1 - scrollH, y1 - scroll, tw, th);
    	else if (main.getEditingModel() != null) main.getEditingModel().drawOnTexture(x1 - scrollH, y1 - scroll, tw, th);
		//TODO rendering
		RenderUtil.popStencil();
	}
	
	@Override
	public Cursor getCursor(float mx, float my) //TODO
	{
		return super.getCursor(mx + scrollH, my + scroll);
	}

	@Override
	public float getMaxScroll()
	{
		return scrollSize;
	}

	@Override
	public float getScroll()
	{
		return scroll;
	}

	@Override
	public void setScroll(float scroll)
	{
		this.scroll = scroll;
	}

	@Override
	public float getMaxScrollH()
	{
		return scrollSizeH;
	}

	@Override
	public float getScrollH()
	{
		return scrollH;
	}

	@Override
	public void setScrollH(float scrollH)
	{
		this.scrollH = scrollH;
	}
	
	private double prevU, prevV;

	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		if (Main.instance.project.getTexture() != null && Main.instance.getEditorMode() == EditorMode.TEXTURE) Main.instance.tool.onMouseClick(Main.instance.project.getTexture(), prevU = getTexU(mx), prevV = getTexV(my), button);
	}

	@Override
	public void onMouseReleased(float mx, float my, int button, int mods)
	{
		if (Main.instance.project.getTexture() != null && Main.instance.getEditorMode() == EditorMode.TEXTURE) Main.instance.tool.onMouseRelease(Main.instance.project.getTexture(), prevU = getTexU(mx), prevV = getTexV(my), button);
	}

	@Override
	public void onDrag(float mx, float my, int button)
	{
		if (Main.instance.project.getTexture() != null && Main.instance.getEditorMode() == EditorMode.TEXTURE) Main.instance.tool.onMouseDrag(Main.instance.project.getTexture(), prevU, prevV, prevU = getTexU(mx), prevV = getTexV(my), button);
	}
}