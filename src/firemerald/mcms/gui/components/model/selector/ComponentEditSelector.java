package firemerald.mcms.gui.components.model.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import firemerald.mcms.api.model.IModel;
import firemerald.mcms.api.model.MultiModel;
import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.gui.components.scrolling.ScrollableComponentPane;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.model.IEditable;
import firemerald.mcms.model.IEditableParent;

public class ComponentEditSelector extends ScrollableComponentPane
{
	public final BaseEditable baseEditable;
	protected final SelectorEntry base = new SelectorEntry(baseEditable = new BaseEditable(), -8, 0, null);
	protected final List<IGuiElement> cleanup = new ArrayList<>();
	public final EditorPanes editorPanes;
	
	public ComponentEditSelector(float x1, float y1, float x2, float y2, EditorPanes editorPanes)
	{
		super(x1, y1, x2, y2);
		(this.editorPanes = editorPanes).selector = this;
		setSize(x1, y1, x2, y2);
		base.expanded = true;
	}
	
	public void setModel(MultiModel model)
	{
		this.baseEditable.setModel(model);
		updateBase();
	}
	
	public void updateBase()
	{
		this.base.children.clear();
		float y = 1;
		for (IEditable b : baseEditable.getChildren()) y = add(this.base, b, 1, y);
		updateList();
	}
	
	public float add(SelectorEntry parent, IEditable add, float x, float y)
	{
		SelectorEntry e = new SelectorEntry(add, x, y, parent);
		e.expanded = true;
		x += 8;
		y += 16;
		for (IEditable a : add.getChildren()) y = add(e, a, x, y);
		return y;
	}
	
	public void updateList()
	{
		cleanup.forEach(element -> this.removeElement(element));
		cleanup.clear();
		height = 0;
		List<SelectorEntry> entries = new ArrayList<>();
		base.addVisibleToList(entries);
		for (SelectorEntry entry : entries)
		{
			entry.y = height;
			height += 16;
			if (entry.editable.hasChildren()) this.addElementCleanup(new Expand(entry, this));
			this.addElementCleanup(new Show(entry));
			this.addElementCleanup(new Selection(entry, this, editorPanes));
		}
		updateComponentSize();
		updateScrollSize();
	}
	
	public void addElementCleanup(IGuiElement element)
	{
		super.addElement(element);
		cleanup.add(element);
	}
	
	public boolean isParentOf(SelectorEntry a, SelectorEntry b)
	{
		return b.parent != null && (b.parent == a || isParentOf(a, b.parent));
	}
	
	public static class BaseEditable implements IEditable
	{
		public IEditableParent model;
		
		public void setModel(IEditableParent model)
		{
			this.model = model;
		}
		
		@Override
		public void onSelect(EditorPanes editorPanes) {}
		
		@Override
		public void onDeselect(EditorPanes editorPanes) {}

		@Override
		public String getDisplayIcon()
		{
			return null;
		}

		@Override
		public String getName()
		{
			return "root";
		}

		@Override
		public Collection<? extends IEditable> getChildren()
		{
			return model.getChildren();
		}

		@Override
		public boolean hasChildren()
		{
			return model.hasChildren();
		}

		@Override
		public boolean canBeChild(IEditable candidate)
		{
			return model.canBeChild(candidate);
		}

		@Override
		public void addChild(IEditable child)
		{
			model.addChild(child);
		}

		@Override
		public void addChildBefore(IEditable child, IEditable position)
		{
			model.addChildBefore(child, position);
		}

		@Override
		public void addChildAfter(IEditable child, IEditable position)
		{
			model.addChildAfter(child, position);
		}

		@Override
		public void removeChild(IEditable child)
		{
			model.removeChild(child);
		}

		@Override
		public void movedTo(IEditableParent oldParent, IEditableParent newParent) {}

		@Override
		public boolean isVisible()
		{
			return true;
		}

		@Override
		public void setVisible(boolean visible) {}

		@Override
		public IEditable copy(IEditableParent newParent, IModel model)
		{
			return null;
		}
	}
}