package firemerald.mcms.gui.main.components.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import firemerald.mcms.Main;
import firemerald.mcms.api.animation.Transformation;
import firemerald.mcms.api.model.IEditableParent;
import firemerald.mcms.api.model.IModelEditable;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.gui.IGuiElement;
import firemerald.mcms.gui.components.scrolling.ScrollableComponentPane;
import firemerald.mcms.gui.main.GuiMain;
import firemerald.mcms.model.EditorPanes;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.ResourceLocation;

public class ComponentEditSelector extends ScrollableComponentPane
{
	public final BaseEditable baseEditable;
	protected final SelectorEntry base = new SelectorEntry(baseEditable = new BaseEditable(), -8, 0, null);
	protected final List<IGuiElement> cleanup = new ArrayList<>();
	public final GuiMain gui;
	
	public ComponentEditSelector(int x1, int y1, int x2, int y2, GuiMain gui)
	{
		super(x1, y1, x2, y2);
		setSize(x1, y1, x2, y2);
		this.gui = gui;
		base.expanded = true;
	}
	
	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		super.onGuiUpdate(reason);
		if (reason == GuiUpdate.PROJECT || reason == GuiUpdate.MODEL)
		{
			this.baseEditable.setModel(Main.instance.project.getRig());
			updateBase();
		}
	}
	
	public void updateBase()
	{
		Map<IModelEditable, Boolean> expanded = new HashMap<>();
		iterateSetMap(base, expanded);
		this.base.children.clear();
		int y = 1;
		for (IModelEditable b : baseEditable.getChildren()) y = add(this.base, b, 1, y);
		iterateGetMap(base, expanded);
		updateList();
	}
	
	private void iterateSetMap(SelectorEntry entry, Map<IModelEditable, Boolean> expanded)
	{
		expanded.put(entry.editable, entry.expanded);
		entry.children.forEach(child -> iterateSetMap(child, expanded));
	}
	
	private void iterateGetMap(SelectorEntry entry, Map<IModelEditable, Boolean> expanded)
	{
		Boolean val = expanded.get(entry.editable);
		if (val != null) entry.expanded = val.booleanValue();
		entry.children.forEach(child -> iterateGetMap(child, expanded));
	}
	
	public int add(SelectorEntry parent, IModelEditable add, int x, int y)
	{
		SelectorEntry e = new SelectorEntry(add, x, y, parent);
		e.expanded = true;
		x += 8;
		y += 16;
		for (IModelEditable a : add.getChildren()) y = add(e, a, x, y);
		return y;
	}
	
	public void updateList()
	{
		cleanup.forEach(element -> this.removeElement(element));
		cleanup.clear();
		int height = 0;
		List<SelectorEntry> entries = new ArrayList<>();
		base.addVisibleToList(entries);
		for (SelectorEntry entry : entries)
		{
			entry.y = height;
			height += 16;
			if (entry.editable.hasChildren()) this.addElementCleanup(new Expand(entry, this));
			this.addElementCleanup(new Show(entry));
			this.addElementCleanup(new Selection(entry, this, Main.instance.editorPanes));
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
	
	public static class BaseEditable implements IModelEditable
	{
		public static final Transformation NONE = new Transformation();
		public IEditableParent model;
		
		public void setModel(IEditableParent model)
		{
			this.model = model;
		}
		
		@Override
		public int onSelect(EditorPanes editorPanes, int editorY)
		{
			return editorY;
		}
		
		@Override
		public void onDeselect(EditorPanes editorPanes) {}

		@Override
		public ResourceLocation getDisplayIcon()
		{
			return null;
		}

		@Override
		public String getName()
		{
			return "root";
		}

		@Override
		public String getBoneName()
		{
			return null;
		}

		@Override
		public Collection<? extends IModelEditable> getChildren()
		{
			return model == null ? Collections.emptyList() : model.getChildren();
		}

		@Override
		public boolean hasChildren()
		{
			return model != null && model.hasChildren();
		}

		@Override
		public boolean canBeChild(IModelEditable candidate)
		{
			return model != null && model.canBeChild(candidate);
		}

		@Override
		public void addChild(IModelEditable child)
		{
			if (model != null) model.addChild(child);
		}

		@Override
		public void addChildBefore(IModelEditable child, IModelEditable position)
		{
			if (model != null) model.addChildBefore(child, position);
		}

		@Override
		public void addChildAfter(IModelEditable child, IModelEditable position)
		{
			if (model != null) model.addChildAfter(child, position);
		}

		@Override
		public void removeChild(IModelEditable child)
		{
			if (model != null) model.removeChild(child);
		}
		
		@Override
		public int getChildIndex(IModelEditable child)
		{
			if (model != null) return model.getChildIndex(child);
			else return -1;
		}

		@Override
		public void addChildAt(IModelEditable child, int index)
		{
			if (model != null) model.addChildAt(child, index);
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
		public IModelEditable copy(IEditableParent newParent, IRigged<?, ?> model)
		{
			return null;
		}

		@Override
		public Transformation getDefaultTransformation()
		{
			return NONE;
		}
	}
}