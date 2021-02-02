package firemerald.mcms.gui.colors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentPane;
import firemerald.mcms.shader.GuiShader;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.theme.EnumDirection;
import firemerald.mcms.theme.ThemeElement;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.ResourceLocation;
import firemerald.mcms.util.TextureManager;
import firemerald.mcms.util.mesh.GuiMesh;
import firemerald.mcms.window.api.MouseButtons;

public class ComponentColorPicker extends ComponentPane
{
	public static final GuiMesh PANEL = new GuiMesh(0, 48, 240, 283, 0, 0, 1, 1);
	public ThemeElement panel = null;
	public static final GuiMesh TAB_RGB = new GuiMesh(-12, -12, 60, 60, 0, 0, 1, 1);
	public static final GuiMesh ICON_RGB = new GuiMesh(8, 8, 40, 40, 0, 0, 1, 1);
	public static final GuiMesh TAB_HSV = new GuiMesh(36, -12, 108, 60, 0, 0, 1, 1);
	public static final GuiMesh ICON_HSV = new GuiMesh(56, 8, 88, 40, 0, 0, 1, 1);
	public static final GuiMesh TAB_HSL = new GuiMesh(84, -12, 156, 60, 0, 0, 1, 1);
	public static final GuiMesh ICON_HSL = new GuiMesh(104, 8, 136, 40, 0, 0, 1, 1);
	
	private static final class ColorModeTab
	{
		final ComponentPaneColorPicker picker;
		final GuiMesh tabMesh, iconMesh;
		final ResourceLocation iconTex;
		ThemeElement element;
		final boolean connectLeft, connectRight;
		
		ColorModeTab(ComponentPaneColorPicker picker, ResourceLocation iconTex, GuiMesh iconMesh, GuiMesh tabMesh, boolean connectLeft, boolean connectRight)
		{
			this.picker = picker;
			this.iconTex = iconTex;
			this.iconMesh = iconMesh;
			this.tabMesh = tabMesh;
			this.connectLeft = connectLeft;
			this.connectRight = connectRight;
		}
		
		void onGuiUpdate(GuiUpdate reason)
		{
			if (reason == GuiUpdate.THEME)
			{
				if (element != null) element.release();
				element = picker.getTheme().genTab(48, 48, 2, 12, EnumDirection.DOWN, connectLeft, connectRight);
			}
		}
		
		void render(TextureManager texs)
		{
			element.bind();
			tabMesh.render();
			texs.bindTexture(iconTex);
			iconMesh.render();
		}
	}

	@Override
	public void onGuiUpdate(GuiUpdate reason)
	{
		if (reason == GuiUpdate.THEME)
		{
			if (panel != null) panel.release();
			panel = getTheme().genRoundedBox(240, 235, 2, 12);
		}
		for (ColorModeTab tab : tabs) tab.onGuiUpdate(reason);
	}

	public boolean hasChanged = false;
	public ColorModel color = new RGB(1, 1, 1);
	public final ColorModeTab[] tabs;
	public final List<ColorModeTab> orderedTabs = new ArrayList<>();
	public final float maxX;
	public final Consumer<ColorModel> onColor;
	public final ColorHistoryButton[] history = new ColorHistoryButton[16];

	public static final ResourceLocation TEX_RGB = new ResourceLocation(Main.ID, "color_pickers/rgb_icon.png");
	public static final ResourceLocation TEX_HSV = new ResourceLocation(Main.ID, "color_pickers/hsv_icon.png");
	public static final ResourceLocation TEX_HSL = new ResourceLocation(Main.ID, "color_pickers/hsl_icon.png");
	
	public ComponentColorPicker(int x, int y, ColorModel color, Consumer<ColorModel> onColor)
	{
		super(x, y, x + 240, y + 296);
		this.color = color;
		orderedTabs.add(new ColorModeTab(new ComponentPaneRGB(12, 60, 228, 215, color), TEX_RGB, ICON_RGB, TAB_RGB, false, true));
		orderedTabs.add(new ColorModeTab(new ComponentPaneHSV(12, 60, 228, 215, color), TEX_HSV, ICON_HSV, TAB_HSV, true, true));
		orderedTabs.add(new ColorModeTab(new ComponentPaneHSL(12, 60, 228, 215, color), TEX_HSL, ICON_HSL, TAB_HSL, true, true));
		tabs = orderedTabs.toArray(new ColorModeTab[orderedTabs.size()]);
		this.addElement(tabs[0].picker);
		this.addElement(history[0] = new ColorHistoryButton(12, 223, 32, 243, Main.instance.state.getColorHistory(0), this));
		this.addElement(history[1] = new ColorHistoryButton(40, 223, 60, 243, Main.instance.state.getColorHistory(1), this));
		this.addElement(history[2] = new ColorHistoryButton(68, 223, 88, 243, Main.instance.state.getColorHistory(2), this));
		this.addElement(history[3] = new ColorHistoryButton(96, 223, 116, 243, Main.instance.state.getColorHistory(3), this));
		this.addElement(history[4] = new ColorHistoryButton(124, 223, 144, 243, Main.instance.state.getColorHistory(4), this));
		this.addElement(history[5] = new ColorHistoryButton(152, 223, 172, 243, Main.instance.state.getColorHistory(5), this));
		this.addElement(history[6] = new ColorHistoryButton(180, 223, 200, 243, Main.instance.state.getColorHistory(6), this));
		this.addElement(history[7] = new ColorHistoryButton(208, 223, 228, 243, Main.instance.state.getColorHistory(7), this));
		this.addElement(history[8] = new ColorHistoryButton(12, 251, 32, 271, Main.instance.state.getColorHistory(8), this));
		this.addElement(history[9] = new ColorHistoryButton(40, 251, 60, 271, Main.instance.state.getColorHistory(9), this));
		this.addElement(history[10] = new ColorHistoryButton(68, 251, 88, 271, Main.instance.state.getColorHistory(10), this));
		this.addElement(history[11] = new ColorHistoryButton(96, 251, 116, 271, Main.instance.state.getColorHistory(11), this));
		this.addElement(history[12] = new ColorHistoryButton(124, 251, 144, 271, Main.instance.state.getColorHistory(12), this));
		this.addElement(history[13] = new ColorHistoryButton(152, 251, 172, 271, Main.instance.state.getColorHistory(13), this));
		this.addElement(history[14] = new ColorHistoryButton(180, 251, 200, 271, Main.instance.state.getColorHistory(14), this));
		this.addElement(history[15] = new ColorHistoryButton(208, 251, 228, 271, Main.instance.state.getColorHistory(15), this));
		maxX = 48 * tabs.length;
		this.onColor = onColor;
		onGuiUpdate(GuiUpdate.THEME);
	}
	
	public void setCenter(int x, int y)
	{
		this.setSize(x - 120, y - 141, x + 120, y + 142);
	}
	
	@Override
	public void tick(float mx, float my, float deltaTime)
	{
		super.tick(mx, my, deltaTime);
		mx -= x1;
		my -= y1;
		ColorModeTab tab = orderedTabs.get(0);
		if (tab.picker.hasChanged)
		{
			color = tab.picker.getColor();
			tab.picker.hasChanged = false;
			onColor.accept(color);
			hasChanged = true;
		}
	}
	
	public void setColor(ColorModel color)
	{
		ColorModeTab tab = orderedTabs.get(0);
		this.color = color;
		tab.picker.setColor(color);
	}
	
	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		super.onMousePressed(mx, my, button, mods);
		if (button == MouseButtons.LEFT)
		{
			mx -= x1;
			my -= y1;
			if (mx >= 0 && my >= 0 && mx < maxX && my < 48)
			{
				int ind = (int) mx / 48;
				ColorModeTab oldTab = orderedTabs.get(0);
				ColorModeTab newTab = tabs[ind];
				if (oldTab != newTab)
				{
					oldTab.picker.hasChanged = newTab.picker.hasChanged = false;
					ind = orderedTabs.indexOf(newTab);
					orderedTabs.remove(ind);
					orderedTabs.add(0, newTab);
					this.removeElement(oldTab.picker);
					this.addElement(newTab.picker);
					ColorModel col = oldTab.picker.getColor();
					newTab.picker.setColor(col);
				}
			}
		}
	}
	
	@Override
	public void render(float mx, float my, boolean canHover)
	{
		GuiShader s = Main.instance.guiShader;
		GuiShader.MODEL.push();
		GuiShader.MODEL.matrix().translate(x1, y1, 0);
		s.updateModel();
		TextureManager texs = Main.instance.textureManager;
		for (int i = orderedTabs.size() - 1; i > 0; i--) orderedTabs.get(i).render(texs);
		panel.bind();
		//Main.instance.textureManager.unbindTexture();
		PANEL.render();
		orderedTabs.get(0).render(texs);
		GuiShader.MODEL.pop();
		s.updateModel();
		super.render(mx, my, canHover);
	}
}
