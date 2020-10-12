package firemerald.mcms.gui.main.components.elements;

import java.util.ArrayList;
import java.util.List;

import firemerald.mcms.api.model.IModelEditable;

public class SelectorEntry
{
	public final SelectorEntry parent;
	public final List<SelectorEntry> children = new ArrayList<>();
	public final IModelEditable editable;
	public final int x;
	public int y;
	public boolean expanded;
	
	public SelectorEntry(IModelEditable editable, int x, int y, SelectorEntry parent)
	{
		this.editable = editable;
		this.x = x;
		this.y = y;
		this.expanded = false;
		if ((this.parent = parent) != null) parent.children.add(this);
	}
	
	public void addVisibleToList(List<SelectorEntry> list)
	{
		if (expanded) children.stream().forEachOrdered(child -> {
			list.add(child);
			child.addVisibleToList(list);
		});
	}
	
	public void addChildFromDrag(SelectorEntry child) //TODO
	{
		if (!children.contains(child))
		{
			children.add(child);
			editable.addChild(child.editable);
		}
	}
	
	public void addChildBeforeFromDrag(SelectorEntry child, SelectorEntry beforeThis) //TODO
	{
		if (!children.contains(child))
		{
			int pos = children.indexOf(beforeThis);
			if (pos < 0) pos = 0;
			children.add(pos, child);
			editable.addChildBefore(child.editable, beforeThis.editable);
		}
	}
	
	public void addChildAfterFromDrag(SelectorEntry child, SelectorEntry afterThis) //TODO
	{
		if (!children.contains(child))
		{
			int pos = this.children.indexOf(afterThis) + 1;
			if (pos <= 0) pos = this.children.size();
			children.add(pos, child);
			editable.addChildAfter(child.editable, afterThis.editable);
		}
	}
	
	public boolean isParentOf(SelectorEntry entry)
	{
		return entry.parent != null && (entry.parent == this || this.isParentOf(entry.parent));
	}
	
	public void remove(SelectorEntry child)
	{
		this.children.remove(child);
		this.editable.removeChild(child.editable);
	}
	
	public boolean areAnyChildrenUnfolded()
	{
		return children.parallelStream().anyMatch(child -> child.expanded || child.areAnyChildrenUnfolded());
	}
	
	public boolean areAnyChildrenFolded()
	{
		return children.parallelStream().anyMatch(child -> !child.expanded || child.areAnyChildrenFolded());
	}
	
	public void foldAllChildren()
	{
		children.parallelStream().forEach(child -> {
			child.expanded = false;
			child.foldAllChildren();
		});
	}
	
	public void unfoldAllChildren()
	{
		children.parallelStream().forEach(child -> {
			child.expanded = true;
			child.unfoldAllChildren();
		});
	}
}