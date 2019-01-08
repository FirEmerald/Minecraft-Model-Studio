package firemerald.mcms.gui.colors;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import firemerald.mcms.Main;
import firemerald.mcms.gui.components.ComponentPane;
import firemerald.mcms.model.Mesh;
import firemerald.mcms.shader.Shader;
import firemerald.mcms.texture.ColorModel;
import firemerald.mcms.texture.RGB;
import firemerald.mcms.util.TextureManager;

public class ComponentColorPicker extends ComponentPane
{
	public static final Mesh PANEL = new Mesh(0, 48, 240, 240, 0, 0, 0, 1, 1);
	public static final Mesh TAB_RGB = new Mesh(0, 0, 64, 64, 0, 0, 0, 1, 1);
	public static final Mesh ICON_RGB = new Mesh(8, 8, 40, 40, 0, 0, 0, 1, 1);
	public static final Mesh TAB_HSV = new Mesh(32, 0, 112, 64, 0, 0, 0, 1, 1);
	public static final Mesh ICON_HSV = new Mesh(56, 8, 88, 40, 0, 0, 0, 1, 1);
	public static final Mesh TAB_HSL = new Mesh(80, 0, 160, 64, 0, 0, 0, 1, 1);
	public static final Mesh ICON_HSL = new Mesh(104, 8, 136, 40, 0, 0, 0, 1, 1);
	
	private static final class ColorModeTab
	{
		final ComponentPaneColorPicker picker;
		final Mesh tabMesh, iconMesh;
		final String tabTex, iconTex;
		
		ColorModeTab(ComponentPaneColorPicker picker, Mesh tabMesh, String tabTex, Mesh iconMesh, String iconTex)
		{
			this.picker = picker;
			this.tabMesh = tabMesh;
			this.tabTex = tabTex;
			this.iconMesh = iconMesh;
			this.iconTex = iconTex;
		}
		
		void render(TextureManager texs)
		{
			texs.bindTexture(tabTex);
			tabMesh.render();
			texs.bindTexture(iconTex);
			iconMesh.render();
		}
	}

	public boolean hasChanged = false;
	public ColorModel color = new RGB(1, 1, 1);
	public final ColorModeTab[] tabs;
	public final List<ColorModeTab> orderedTabs = new ArrayList<>();
	public final float maxX;
	
	public ComponentColorPicker(float x, float y)
	{
		super(x, y, x + 240, y + 240);
		orderedTabs.add(new ColorModeTab(new ComponentPaneRGB(12, 60, 228, 215, color), TAB_RGB, "tab_48x48_l.png", ICON_RGB, "color_pickers/rgb_icon.png"));
		orderedTabs.add(new ColorModeTab(new ComponentPaneHSV(12, 60, 228, 215, color), TAB_HSV, "tab_48x48_m.png", ICON_HSV, "color_pickers/hsv_icon.png"));
		orderedTabs.add(new ColorModeTab(new ComponentPaneHSL(12, 60, 228, 215, color), TAB_HSL, "tab_48x48_m.png", ICON_HSL, "color_pickers/hsl_icon.png"));
		tabs = orderedTabs.toArray(new ColorModeTab[orderedTabs.size()]);
		this.guiElements.add(tabs[0].picker);
		maxX = 48 * tabs.length;
	}
	
	public void setPosition(float x, float y)
	{
		this.setSize(x, y, x + 240, y + 240);
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
			hasChanged = true;
		}
	}
	
	@Override
	public void onMousePressed(float mx, float my, int button, int mods)
	{
		super.onMousePressed(mx, my, button, mods);
		if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
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
					this.guiElements.remove(oldTab.picker);
					this.guiElements.add(newTab.picker);
					ColorModel col = oldTab.picker.getColor();
					newTab.picker.setColor(col);
				}
			}
		}
	}
	
	@Override
	public void render(float mx, float my, boolean canHover)
	{
		Shader s = Main.instance.shader;
		Shader.MODEL.push();
		Shader.MODEL.matrix().translate(x1, y1, 0);
		s.updateModel();
		TextureManager texs = Main.instance.textureManager;
		for (int i = orderedTabs.size() - 1; i > 0; i--) orderedTabs.get(i).render(texs);
		texs.bindTexture("pane_240x192.png");
		PANEL.render();
		orderedTabs.get(0).render(texs);
		Shader.MODEL.pop();
		s.updateModel();
		super.render(mx, my, canHover);
	}
}
