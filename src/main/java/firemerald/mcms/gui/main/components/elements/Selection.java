package firemerald.mcms.gui.main.components.elements;

import firemerald.mcms.Main;
import firemerald.mcms.api.model.IModelEditable;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.gui.components.IComponent;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.util.mesh.GuiMesh;
import firemerald.mcms.util.mesh.Meshes;
import firemerald.mcms.window.api.MouseButtons;

public class Selection extends ComponentButton
{
	public final ComponentEditSelector selector;
	public final SelectorEntry entry;
	public final GuiMesh selection = new GuiMesh();
	private String name = "";
	private boolean dragging = false;
	private float pMX, pMY;
	public final EditorPanes editorPanes;
	
	public Selection(SelectorEntry entry, ComponentEditSelector selector, EditorPanes editorPanes)
	{
		super(entry.x + 32, entry.y, entry.x + 48, entry.y + 16);
		this.editorPanes = editorPanes;
		this.entry = entry;
		updateName();
		this.selector = selector;
	}
	
	public void updateName()
	{
		int w = 16 + Main.instance.fontMsg.getStringWidth(name = entry.editable.getName());
		x2 = entry.x + 48 + w;
		selection.setMesh(0, 0, w, 16, 0, 0, 1, 1);
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		super.tick(mx, my, deltaTime);
		if (!name.equals(entry.editable.getName())) updateName();
	}
	
	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		super.onMousePressed(mx, my, button, mods);
		if (button == MouseButtons.LEFT)
		{
			pMX = mx;
			pMY = my;
		}
	}
	
	@Override
	public void onMouseReleased(float mx, float my, int button, int mods) //TODO action
	{
		super.onMouseReleased(mx, my, button, mods);
		if (button == MouseButtons.LEFT && dragging)
		{
			dragging = false;
			if (!contains(mx, my))
			{
				IComponent hover = this.selector.getHovered(mx - this.selector.getScrollH(), my - this.selector.getScroll());
				if (hover instanceof Selection)
				{
					Selection over = (Selection) hover;
					boolean above = false, below = false;
					if (my <= over.y1 + 4) above = true;
					else if (my > over.y2 - 4) below = true;
					IModelEditable oldParent = entry.parent.editable;
					SelectorEntry newParentEntry = (above || below) ? over.entry.parent : over.entry;
					IModelEditable newParent = newParentEntry.editable;
					if ((above || below || newParent != oldParent) && entry != over.entry && newParent.canBeChild(entry.editable) && !entry.isParentOf(over.entry))
					{
						entry.parent.remove(entry);
						if (above) newParentEntry.addChildBeforeFromDrag(entry, over.entry);
						else if (below) newParentEntry.addChildAfterFromDrag(entry, over.entry);
						else
						{
							newParentEntry.addChildFromDrag(entry);
							if (!newParentEntry.expanded)
							{
								entry.expanded = true;
								selector.updateList();
							}
						}
						if (oldParent != newParent) entry.editable.movedTo(oldParent, newParent);
						selector.updateBase();
						Main main = Main.instance;
			    		main.setEditing(entry.editable);
					}
				}
			}
		}
	}
	
	@Override
	public void onDrag(float mx, float my, int button)
	{
		if (button == MouseButtons.LEFT && !dragging)
		{
			float dX = pMX - mx;
			float dY = pMY - my;
			if (dX * dX + dY * dY >= 8 * 8) dragging = true;
		}
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

	@Override
	public void onRelease()
	{
		Main main = Main.instance;
		if (entry.editable != main.getEditing())
		{
    		main.setEditing(entry.editable);
		}
	}
	
	@Override
	public void render(float mx, float my, boolean canHover)
	{
		super.render(mx, my, canHover);
		if (canHover && dragging && !contains(mx, my))
		{
			IComponent hover = this.selector.getHovered(mx - this.selector.getScrollH(), my - this.selector.getScroll());
			if (hover instanceof Selection)
			{
				Selection over = (Selection) hover;
				boolean above = false, below = false;
				if (my <= over.y1 + 4) above = true;
				else if (my > over.y2 - 4) below = true;
				IModelEditable oldParent = entry.parent.editable;
				SelectorEntry newParentEntry = (above || below) ? over.entry.parent : over.entry;
				IModelEditable newParent = newParentEntry.editable;
				if ((above || below || newParent != oldParent) && entry != over.entry && newParent.canBeChild(entry.editable) && !entry.isParentOf(over.entry))
				{
					Main main = Main.instance;
					main.textureManager.unbindTexture();
					main.guiShader.setColor(0, 0, 0, 1);
					if (above)
					{
						Main.guiTempMesh.setMesh(over.x1, over.y1, over.x2, over.y1 + 1, 0, 0, 1, 1);
					}
					else if (below)
					{
						Main.guiTempMesh.setMesh(over.x1, over.y2 - 1, over.x2, over.y2, 0, 0, 1, 1);
					}
					else
					{
						Main.guiTempMesh.setMesh(over.x1, over.y1 + 8, over.x2, over.y1 + 9, 0, 0, 1, 1);
					}
					Main.guiTempMesh.render();
					main.guiShader.setColor(1, 1, 1, 1);
				}
			}
		}
	}

	@Override
	public void render(ButtonState state)
	{
		Main main = Main.instance;
		GuiShader s = main.guiShader;
		GuiShader.MODEL.push();
		GuiShader.MODEL.matrix().translate(x1, y1, 0);
		s.updateModel();
		main.textureManager.bindTexture(entry.editable.getDisplayIcon());
		Meshes.X16.render();
		main.fontMsg.drawTextLine(name, 16, 0, 0, 0, 0, 1, false, false, false);
		main.textureManager.unbindTexture();
		if (main.getEditingModel() == entry.editable)
		{
			main.guiShader.setColor(1, 1, 1, .5f);
			selection.render();
		}
		if (dragging)
		{
			main.guiShader.setColor(0, 1, 1, .5f);
		}
		else switch (state)
		{
		case DISABLED:
			main.guiShader.setColor(.5f, .5f, .5f, .5f);
			break;
		case HOVER:
			main.guiShader.setColor(0, 0, 1, .25f);
			break;
		case PUSH:
			main.guiShader.setColor(0, 0, 1, .5f);
			break;
		default:
			main.guiShader.setColor(0, 0, 0, 0);
			break;
		}
		selection.render();
		main.guiShader.setColor(1, 1, 1, 1);
		GuiShader.MODEL.pop();
		s.updateModel();
	}
}