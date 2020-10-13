package firemerald.mcms.gui.popups.hotkeys;

import java.util.ArrayList;
import java.util.List;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentLabel;
import firemerald.mcms.gui.components.StandardButton;
import firemerald.mcms.gui.components.scrolling.ScrollableComponentPaneVertical;
import firemerald.mcms.util.IntReference;
import firemerald.mcms.util.Quadruple;
import firemerald.mcms.util.hotkey.Action;
import firemerald.mcms.util.hotkey.HotKey;

public class ComponentPaneHotkeys extends ScrollableComponentPaneVertical
{
	public final List<Quadruple<ComponentLabel, ComponentKeyGetter, StandardButton, StandardButton>> items = new ArrayList<>();
	
	public ComponentPaneHotkeys(int x1, int y1, int x2, int y2, int border)
	{
		super(x1, y1, x2, y2, border);
		final int mx4 = (x2 - x1);
		final int mx3 = mx4 - 48;
		final int mx2 = mx3 - 48;
		final int mx1 = mx2 - 256;
		final IntReference y = new IntReference(0);
		Action.ACTIONS.values().forEach(action -> {
			HotKey curKey = Main.instance.state.hotkeys.get(action);
			ComponentLabel label = new ComponentLabel(0, y.val, mx1, y.val + 16, Main.instance.fontMsg, action.displayName);
			StandardButton clear = new StandardButton(mx2, y.val, mx3, y.val + 16, "clear", () -> {});
			StandardButton reset = new StandardButton(mx3, y.val, mx4, y.val + 16, "reset", () -> {});
			reset.enabled = action.def == null ? curKey != null : !action.def.equals(curKey);
			clear.enabled = curKey != null;
			ComponentKeyGetter getter = new ComponentKeyGetter(mx1, y.val, mx2, y.val + 16, curKey, hotkey -> {
				reset.enabled = action.def == null ? hotkey != null : !action.def.equals(hotkey);
				clear.enabled = hotkey != null;
				Main.instance.state.hotkeys.put(action, hotkey);
				Main.instance.state.saveState();
			});
			reset.onRelease = () -> {
				Main.instance.state.hotkeys.put(action, action.def);
				getter.hotKey = action.def;
				getter.getString();
				reset.enabled = false;
				clear.enabled = action.def != null;
				Main.instance.state.saveState();
			};
			clear.onRelease = () -> {
				Main.instance.state.hotkeys.put(action, null);
				getter.hotKey = null;
				getter.getString();
				clear.enabled = false;
				reset.enabled = action.def != null;
				Main.instance.state.saveState();
			};
			this.addElement(label);
			this.addElement(getter);
			this.addElement(clear);
			this.addElement(reset);
			items.add(new Quadruple<>(label, getter, clear, reset));
			y.val += 16;
		});
		updateComponentSize();
		updateScrollSize();
	}
	
	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		if (items != null)
		{
			final int mx4 = (x2 - x1);
			final int mx3 = mx4 - 48;
			final int mx2 = mx3 - 48;
			final int mx1 = mx2 - 256;
			final IntReference y = new IntReference(0);
			items.forEach(item -> {
				item.left.setSize(0, y.val, mx1, y.val + 16);
				item.middleLeft.setSize(mx1, y.val, mx2, y.val + 16);
				item.middleRight.setSize(mx2, y.val, mx3, y.val + 16);
				item.right.setSize(mx3, y.val, mx4, y.val + 16);
				y.val += 16;
			});
			updateComponentSize();
			updateScrollSize();
		}
	}
}