package firemerald.mcms.gui.components.model.selector;

import org.lwjgl.glfw.GLFW;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentButton;
import firemerald.mcms.gui.components.IComponent;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IEditable;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.util.Meshes;

public class Selection extends ComponentButton
{
	public final ComponentEditSelector selector;
	public final SelectorEntry entry;
	public final Mesh selection = new Mesh();
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
		float w = 16 + Main.instance.fontMsg.getStringWidth(name = entry.editable.getName());
		x2 = entry.x + 48 + w;
		selection.setMesh(0, 0, w, 16, 0, 0, 0, 1, 1);
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
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
		{
			pMX = mx;
			pMY = my;
		}
	}
	
	@Override
	public void onMouseReleased(float mx, float my, int button, int mods)
	{
		super.onMouseReleased(mx, my, button, mods);
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && dragging)
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
					IEditable oldParent = entry.parent.editable;
					SelectorEntry newParentEntry = (above || below) ? over.entry.parent : over.entry;
					IEditable newParent = newParentEntry.editable;
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
			    		if (main.editing != null) main.editing.onDeselect(editorPanes);
			    		(main.editing = entry.editable).onSelect(editorPanes);
					}
				}
			}
		}
	}
	
	@Override
	public void onDrag(float mx, float my)
	{
		if (!dragging)
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
		if (entry.editable != main.editing)
		{
    		if (main.editing != null) main.editing.onDeselect(editorPanes);
    		(main.editing = entry.editable).onSelect(editorPanes);
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
				IEditable oldParent = entry.parent.editable;
				SelectorEntry newParentEntry = (above || below) ? over.entry.parent : over.entry;
				IEditable newParent = newParentEntry.editable;
				if ((above || below || newParent != oldParent) && entry != over.entry && newParent.canBeChild(entry.editable) && !entry.isParentOf(over.entry))
				{
					Main main = Main.instance;
					main.textureManager.unbindTexture();
					main.shader.setColor(0, 0, 0, 1);
					if (above)
					{
						Main.MODMESH.setMesh(over.x1, over.y1, over.x2, over.y1 + 1, 0, 0, 0, 1, 1);
					}
					else if (below)
					{
						Main.MODMESH.setMesh(over.x1, over.y2 - 1, over.x2, over.y2, 0, 0, 0, 1, 1);
					}
					else
					{
						Main.MODMESH.setMesh(over.x1, over.y1 + 8, over.x2, over.y1 + 9, 0, 0, 0, 1, 1);
					}
					Main.MODMESH.render();
					main.shader.setColor(1, 1, 1, 1);
				}
			}
		}
	}

	@Override
	public void render(ButtonState state)
	{
		Main main = Main.instance;
		main.textureManager.bindTexture(entry.editable.getDisplayIcon());
		Meshes.X16.render();
		main.fontMsg.drawTextLine(name, 16, 0, 0, 0, 0, 1, false, false, false);
		main.textureManager.unbindTexture();
		if (main.editing == entry.editable)
		{
			main.shader.setColor(1, 1, 1, .5f);
			selection.render();
		}
		if (dragging)
		{
			main.shader.setColor(0, 1, 1, .5f);
		}
		else switch (state)
		{
		case DISABLED:
			main.shader.setColor(.5f, .5f, .5f, .5f);
			break;
		case HOVER:
			main.shader.setColor(0, 0, 1, .25f);
			break;
		case PUSH:
			main.shader.setColor(0, 0, 1, .5f);
			break;
		default:
			main.shader.setColor(0, 0, 0, 0);
			break;
		}
		selection.render();
		main.shader.setColor(1, 1, 1, 1);
	}
}