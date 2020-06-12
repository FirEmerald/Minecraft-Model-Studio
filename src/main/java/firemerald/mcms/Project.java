package firemerald.mcms;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.Level;

import firemerald.mcms.api.animation.Animation;
import firemerald.mcms.api.animation.IAnimation;
import firemerald.mcms.api.animation.Pose;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.data.Element;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.api.model.ISkeleton;
import firemerald.mcms.api.model.MultiModel;
import firemerald.mcms.api.model.Skeleton;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.api.util.FileUtil.DataType;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.MiscUtil;

public class Project
{
	private static class BoneView
	{
		final String name;
		final List<BoneView> children = new ArrayList<>();
		
		BoneView(String name)
		{
			this.name = name;
		}
	}
	
	protected File source;
	protected String name;
	protected boolean useBackingSkeleton = true;
	protected int textureWidth = 16, textureHeight = 16;
	protected int viewTextureWidth = 16, viewTextureHeight = 16;
	protected float scale = 1 / 16f;
	protected ISkeleton skeleton;
	protected final List<BoneView> boneView = new ArrayList<>();
	protected final Map<String, IModel> models = new LinkedHashMap<>();
	protected String modelName;
	protected IModel model;
	protected final Map<String, Integer> overrideTextureWidth = new HashMap<>();
	protected final Map<String, Integer> overrideTextureHeight = new HashMap<>();
	protected final Map<String, Texture> textures = new LinkedHashMap<>();
	protected String textureName;
	protected Texture texture;
	protected final Map<String, IAnimation> animations = new LinkedHashMap<>();
	protected String animationName;
	protected IAnimation animation;
	
	public Project(String name, int textureWidth, int textureHeight)
	{
		this.name = name;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		skeleton = useBackingSkeleton ? new Skeleton() : null;
	}
	
	public File getSource()
	{
		return source;
	}
	
	public void setSource(File source)
	{
		this.source = source;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public int getTextureWidth()
	{
		return viewTextureWidth;
	}
	
	public void setTextureWidth(int textureWidth)
	{
		if (!overrideTextureWidth.containsKey(modelName)) viewTextureWidth = textureWidth;
		this.textureWidth = textureWidth;
		this.models.forEach((modelName, model) -> {if (!overrideTextureWidth.containsKey(modelName)) model.updateTex();});
		Main.instance.gui.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public boolean hasModelTextureWidth()
	{
		return this.overrideTextureWidth.containsKey(modelName);
	}
	
	public void setModelTextureWidth(int textureWidth)
	{
		viewTextureWidth = textureWidth;
		this.overrideTextureWidth.put(modelName, textureWidth);
		model.updateTex();
		Main.instance.gui.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public void removeModelTextureWidth()
	{
		viewTextureWidth = textureWidth;
		this.overrideTextureWidth.remove(modelName);
		model.updateTex();
		Main.instance.gui.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public int getTextureHeight()
	{
		return viewTextureHeight;
	}
	
	public void setTextureHeight(int textureHeight)
	{
		if (!overrideTextureHeight.containsKey(modelName)) viewTextureHeight = textureHeight;
		this.textureHeight = textureHeight;
		this.models.forEach((modelName, model) -> {if (!overrideTextureHeight.containsKey(modelName)) model.updateTex();});
		Main.instance.gui.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public boolean hasModelTextureHeight()
	{
		return this.overrideTextureHeight.containsKey(modelName);
	}
	
	public void setModelTextureHeight(int textureHeight)
	{
		viewTextureHeight = textureHeight;
		this.overrideTextureHeight.put(modelName, textureHeight);
		model.updateTex();
		Main.instance.gui.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public void removeModelTextureHeight()
	{
		viewTextureHeight = textureHeight;
		this.overrideTextureHeight.remove(modelName);
		model.updateTex();
		Main.instance.gui.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public void setTextureSize(int textureWidth, int textureHeight)
	{
		if (!overrideTextureWidth.containsKey(modelName)) viewTextureWidth = textureWidth;
		if (!overrideTextureHeight.containsKey(modelName)) viewTextureHeight = textureHeight;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.models.forEach((modelName, model) -> {if (!overrideTextureWidth.containsKey(modelName) || !overrideTextureHeight.containsKey(modelName)) model.updateTex();});
	}
	
	public void setModelTextureSize(int textureWidth, int textureHeight)
	{
		viewTextureWidth = textureWidth;
		viewTextureHeight = textureHeight;
		this.overrideTextureWidth.put(modelName, textureWidth);
		this.overrideTextureHeight.put(modelName, textureHeight);
		model.updateTex();
		Main.instance.gui.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public void removeModelTextureSize()
	{
		viewTextureWidth = textureWidth;
		viewTextureHeight = textureHeight;
		this.overrideTextureWidth.remove(modelName);
		this.overrideTextureHeight.remove(modelName);
		model.updateTex();
		Main.instance.gui.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public float getScale()
	{
		return scale;
	}
	
	public void setScale(float scale)
	{
		this.scale = scale;
	}
	
	public boolean useBackingSkeleton()
	{
		return useBackingSkeleton;
	}
	
	public IRigged<?> getRig()
	{
		return model == null ? useBackingSkeleton ? skeleton : null : model;
	}
	
	public IRigged<?> getCompletestRig()
	{
		return useBackingSkeleton ? skeleton : model == null ? null : model;
	}
	
	public ISkeleton getSkeleton()
	{
		return useBackingSkeleton ? skeleton : null;
	}
	
	public List<String> getAllBoneNames()
	{
		List<String> list = new ArrayList<>();
		if (this.boneView != null) this.boneView.forEach(view -> getAllBoneNames(view, list));
		return list;
	}
	
	public Bone getBone(String name)
	{
		IRigged<?> rig = this.getRig();
		return rig == null ? null : rig.getBone(name);
	}
	
	private void getAllBoneNames(BoneView view, List<String> list)
	{
		list.add(view.name);
		view.children.forEach(child -> getAllBoneNames(child, list));
	}
	
	public List<String> getDisplayBoneNames(Bone selected)
	{
		return getDisplayBoneNames(selected, new ArrayList<>());
	}
	
	public List<String> getDisplayBoneNames(Bone selected, List<String> list)
	{
		final Bone sel = selected;
		List<BoneView> view = this.boneView;
		if (selected != null)
		{
			Stack<String> checking = new Stack<>();
			while (selected != null)
			{
				checking.add(selected.getName());
				selected = selected.parent;
			}
			while (!checking.isEmpty())
			{
				String check = checking.pop();
				boolean flag = false;
				for (BoneView boneView : view) if (boneView.name.equals(check))
				{
					view = boneView.children;
					flag = true;
					break;
				}
				if (!flag) return list;
			}
		}
		if (view == null || view.isEmpty()) return list;
		final List<String> blacklist = new ArrayList<>();
		{
			List<Bone> bones;
			if (sel == null)
			{
				IRigged<?> rig = Main.instance.project.getRig();
				if (rig == null) bones = Collections.emptyList();
				else bones = rig.getRootBones();
			}
			else bones = sel.children;
			for (Bone bone : bones) blacklist.add(bone.getName());
		}
		view.forEach(child -> {
			if (!blacklist.contains(child.name)) list.add(child.name);
		});
		return list;
	}
	
	public Set<String> getModelNames()
	{
		return models.keySet();
	}
	
	public IModel getModel()
	{
		return model;
	}
	
	public String getModelName()
	{
		return modelName;
	}
	
	public void setModelName(String modelName)
	{
		if (this.modelName != null)
		{
			Map<String, IModel> copy = new LinkedHashMap<>(this.models);
			this.models.clear();
			copy.forEach((name, model) -> this.models.put(name.equals(this.modelName) ? modelName : name, model));
			this.modelName = modelName;
			Main.instance.gui.onGuiUpdate(GuiUpdate.MODEL);
		}
	}
	
	private void addBoneView(Bone bone, List<BoneView> parentView)
	{
		BoneView view = null;
		for (BoneView child : parentView) if (child.name.equals(bone.getName()))
		{
			view = child;
			break;
		}
		if (view == null)
		{
			view = new BoneView(bone.getName());
			parentView.add(view);
		}
		final BoneView viewF = view;
		bone.children.forEach(child -> addBoneView(child, viewF.children));
		
	}
	
	private void rebuildBoneView()
	{
		boneView.clear();
		if (useBackingSkeleton) skeleton.getRootBones().forEach(bone -> addBoneView(bone, boneView));
		else models.values().forEach(model -> model.getRootBones().forEach(bone -> addBoneView(bone, boneView)));
	}
	
	public void setModel(String modelName)
	{
		Main.instance.setEditing(null);
		updateSkeletonLocal();
		if (modelName == null)
		{
			this.modelName = null;
			model = null;
			viewTextureWidth = textureWidth;
			viewTextureHeight = textureHeight;
		}
		else
		{
			IModel model = models.get(modelName);
			if (model != null)
			{
				this.modelName = modelName;
				this.model = model;
				if (useBackingSkeleton)
				{
					model.removeNonSkeleton(skeleton);
					model.applySkeletonTransforms(skeleton);
				}
				viewTextureWidth = overrideTextureWidth.containsKey(modelName) ? overrideTextureWidth.get(modelName) : textureWidth;
				viewTextureHeight = overrideTextureHeight.containsKey(modelName) ? overrideTextureHeight.get(modelName) : textureHeight;
			}
		}
		Main.instance.gui.onGuiUpdate(GuiUpdate.MODEL);
	}
	
	public void addModel(String modelName, IModel model)
	{
		Main.instance.setEditing(null);
		if (modelName == null) modelName = MiscUtil.ensureUnique("untitled", models.keySet());
		models.put(modelName, model);
		this.modelName = modelName;
		this.model = model;
		viewTextureWidth = textureWidth;
		viewTextureHeight = textureHeight;
		updateSkeletonLocal();
		Main.instance.gui.onGuiUpdate(GuiUpdate.MODEL);
	}
	
	public void removeModel()
	{
		if (modelName != null)
		{
			String prevModelName = null;
			for (String name : models.keySet())
			{
				if (name.equals(modelName)) break;
				prevModelName = name;
			}
			models.remove(modelName);
			overrideTextureWidth.remove(modelName);
			overrideTextureHeight.remove(modelName);
			this.setModel(prevModelName);
			if (!useBackingSkeleton) rebuildBoneView();
		}
	}
	
	public Set<String> getTextureNames()
	{
		return textures.keySet();
	}
	
	public Texture getTexture()
	{
		return texture;
	}
	
	public String getTextureName()
	{
		return textureName;
	}
	
	public void setTextureName(String textureName)
	{
		if (this.textureName != null)
		{
			Map<String, Texture> copy = new LinkedHashMap<>(this.textures);
			this.textures.clear();
			copy.forEach((name, texture) -> this.textures.put(name.equals(this.textureName) ? textureName : name, texture));
			this.textureName = textureName;
			Main.instance.gui.onGuiUpdate(GuiUpdate.TEXTURE);
		}
	}
	
	public void setTexture(String textureName)
	{
		Main.instance.setEditing(null);
		if (textureName == null)
		{
			this.textureName = null;
			texture = null;
		}
		else
		{
			Texture texture = textures.get(textureName);
			if (texture != null)
			{
				this.textureName = textureName;
				this.texture = texture;
			}
		}
		Main.instance.gui.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public void addTexture(String textureName, Texture texture)
	{
		Main.instance.setEditing(null);
		if (textureName == null) textureName = MiscUtil.ensureUnique("untitled", textures.keySet());
		textures.put(textureName, texture);
		this.textureName = textureName;
		this.texture = texture;
		Main.instance.gui.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public void removeTexture()
	{
		if (textureName != null)
		{
			String prevTextureName = null;
			for (String name : textures.keySet())
			{
				if (name.equals(textureName)) break;
				prevTextureName = name;
			}
			textures.remove(textureName);
			this.setTexture(prevTextureName);
		}
	}
	
	public Set<String> getAnimationNames()
	{
		return animations.keySet();
	}
	
	public IAnimation getAnimation()
	{
		return animation;
	}
	
	public String getAnimationName()
	{
		return animationName;
	}
	
	public void setAnimationName(String animationName)
	{
		if (this.animationName != null)
		{
			Map<String, IAnimation> copy = new LinkedHashMap<>(this.animations);
			this.animations.clear();
			copy.forEach((name, animation) -> this.animations.put(name.equals(this.animationName) ? animationName : name, animation));
			this.animationName = animationName;
			Main.instance.gui.onGuiUpdate(GuiUpdate.ANIMATION);
		}
	}
	
	public void setAnimation(String animationName)
	{
		Main.instance.setEditing(null);
		if (animationName == null)
		{
			this.animationName = null;
			animation = null;
		}
		else
		{
			IAnimation animation = animations.get(animationName);
			if (animation != null)
			{
				this.animationName = animationName;
				this.animation = animation;
			}
		}
		Main.instance.gui.onGuiUpdate(GuiUpdate.ANIMATION);
	}
	
	public void addAnimation(String animationName, IAnimation animation)
	{
		Main.instance.setEditing(null);
		if (animationName == null) animationName = MiscUtil.ensureUnique("untitled", animations.keySet());
		animations.put(animationName, animation);
		this.animationName = animationName;
		this.animation = animation;
		Main.instance.gui.onGuiUpdate(GuiUpdate.ANIMATION);
	}
	
	public void removeAnimation()
	{
		if (animationName != null)
		{
			String prevAnimationName = null;
			for (String name : animations.keySet())
			{
				if (name.equals(animationName)) break;
				prevAnimationName = name;
			}
			animations.remove(animationName);
			this.setAnimation(prevAnimationName);
		}
	}
	
	public void bindTex()
	{
		if (texture != null) texture.bind();
		else Main.instance.textureManager.unbindTexture();
	}
	
	public void unbindTex()
	{
		Main.instance.textureManager.unbindTexture();
	}
	
	public void export()
	{
		File dir = FileUtils.getFolder(name);
		if (dir != null)
		{
			long n = System.nanoTime();
			if (dir.exists()) {} //TODO check for existing files
			else dir.mkdirs();
			DataType type = DataType.XML; //default is XML format
			this.textures.forEach((name, texture) -> {
				 texture.saveTexture(new File(dir, name + ".png"));
			});
			if (this.useBackingSkeleton) //save backing skeleton
			{
				AbstractElement el = type.newElement(skeleton.getElementName());
				this.skeleton.save(el, scale);
				File file = new File(dir, "skeleton.skel");
				try
				{
					type.saveElement(el, file);
				} catch (Exception e) {
					GuiPopupException.onException("Couldn't save skeleton file: " + file, e, Level.WARN);
				}
			}
			this.models.forEach((name, model) -> { //save models
				Writer writer = null;
				File file = new File(dir, name + ".obj");
				try //save model
				{
					writer = new FileWriter(file);
					writer.write(RenderObjectComponents.createObj(model, model.getPose()).toString());
				}
				catch (IOException e)
				{
					GuiPopupException.onException("Couldn't save model file: " + file, e, Level.WARN);
				}
				FileUtil.closeSafe(writer);
				if (!useBackingSkeleton) //save model skeleton
				{
					AbstractElement el = type.newElement("skeleton");
					model.getSkeleton().save(el, Main.instance.project.scale);
					file = new File(dir, name + ".skel");
					try
					{
						type.saveElement(el, file);
					} catch (Exception e) {
						GuiPopupException.onException("Couldn't save skeleton file: " + file, e, Level.WARN);
					}
				}
			});
			this.animations.forEach((name, anim) -> { //save animations
				AbstractElement el = type.newElement(anim.getElementName());
				anim.save(el, scale);
				File file = new File(dir, name + ".anim");
				try
				{
					type.saveElement(el, file); //save animation
				} catch (Exception e) {
					GuiPopupException.onException("Couldn't save animation file: " + file, e, Level.WARN);
				}
			});
			Main.instance.lastNanos += (System.nanoTime() - n);
		}
	}
	
	public boolean save()
	{
		if (getSource() != null) return save(getSource());
		else return saveAs();
	}
	
	public boolean saveAs()
	{
		File out = FileUtils.getSaveFile("mcms;xml;json;bin", getSource() == null ? "" : getSource().toString());
		if (out != null) return save(out);
		else return false;
	}

	public final Stack<Element> undoActions = new Stack<>();
	public final Stack<Element> redoActions = new Stack<>();
	
	public void undo()
	{
		if (!undoActions.isEmpty())
		{
			Element el;
			this.save(el = new Element(""));
			redoActions.push(el);
			Element undo = undoActions.pop();
			this.load(undo);
		}
	}

	public void redo()
	{
		if (!redoActions.isEmpty())
		{
			Element el;
			this.save(el = new Element(""));
			undoActions.push(el);
			Element redo = redoActions.pop();
			this.load(redo);
		}
	}
	
	public void onAction()
	{
		needsSave = true;
		redoActions.clear();
		Element el;
		this.save(el = new Element("root"));
		//TODO value
		final int maxUndoActions = 64;
		while (undoActions.size() >= maxUndoActions) undoActions.remove(0);
		undoActions.push(el);
	}
	
	public void clearActions()
	{
		undoActions.clear();
		redoActions.clear();
	}
	
	private boolean needsSave;
	
	public boolean needsSave()
	{
		return needsSave;
	}
	
	public boolean save(File out)
	{
		FileUtil.DataType dataType = FileUtil.getAppropriateDataType(out.toString());
		AbstractElement root = dataType.newElement("project");
		save(root);
		try
		{
			dataType.saveElement(root, out);
			setSource(out);
			needsSave = false;
			return true;
		}
		catch (Exception e)
		{
			GuiPopupException.onException("Couldn't save project to " + out, e);
			return false;
		}
	}
	
	public void save(AbstractElement root)
	{
		//compile skeleton
		if (useBackingSkeleton)
		{
			if (model == null) models.values().forEach(model -> model.removeNonSkeleton(skeleton));
			else skeleton.applySkeletonTransforms(model.getSkeleton());
			models.values().forEach(model -> model.applySkeletonTransforms(skeleton = model.getSkeleton().applySkeleton(skeleton)));
		}
		root.setString("name", name);
		root.setInt("textureWidth", textureWidth);
		root.setInt("textureHeight", textureHeight);
		root.setFloat("scale", scale);
		root.setBoolean("useBackingSkeleton", useBackingSkeleton);
		if (modelName != null) root.setString("model", modelName);
		if (textureName != null) root.setString("texture", textureName);
		if (animationName != null) root.setString("animation", animationName);
		if (skeleton != null && useBackingSkeleton) skeleton.save(root.addChild(skeleton.getElementName()), 1);
		textures.forEach((name, texture) -> {
			AbstractElement el = root.addChild("texture");
			el.setString("name", name);
			texture.save(el);
		});
		models.forEach((name, model) -> {
			AbstractElement el = root.addChild(model.getElementName());
			el.setString("name", name);
			if (overrideTextureWidth.containsKey(name)) el.setInt("textureWidth", overrideTextureWidth.get(name));
			if (overrideTextureHeight.containsKey(name)) el.setInt("textureHeight", overrideTextureHeight.get(name));
			model.save(el);
		});
		animations.forEach((name, animation) -> {
			AbstractElement el = root.addChild(animation.getElementName());
			el.setString("name", name);
			animation.save(el);
		});
	}
	
	public void load()
	{
		if (getSource() != null) load(getSource());
		else loadFrom();
	}
	
	public void loadFrom()
	{
		File in = FileUtils.getOpenFile("mcms;xml;json;bin", getSource() == null ? "" : getSource().toString());
		if (in != null) load(in);
	}
	
	public void load(File in)
	{
		try
		{
			AbstractElement root = FileUtil.readFile(in);
			load(root);
			setSource(in);
		}
		catch (IOException e)
		{
			GuiPopupException.onException("Couldn't load project " + in, e);
		}
	}
	
	public void makeNew(String name, int textureWidth, int textureHeight, float scale, boolean useBackingSkeleton)
	{
		needsSave = false;
		models.clear();
		textures.clear();
		animations.clear();
		overrideTextureWidth.clear();
		overrideTextureHeight.clear();
		boneView.clear();
		source = null;
		skeleton = useBackingSkeleton ? new Skeleton() : null;
		this.name = name;
		this.viewTextureWidth = this.textureWidth = textureWidth;
		this.viewTextureHeight = this.textureHeight = textureHeight;
		this.scale = scale;
		this.useBackingSkeleton = useBackingSkeleton;
		modelName = null;
		model = null;
		textureName = null;
		texture = null;
		animationName = null;
		animation = null;
		//Main.instance.clearActions();
		clearActions();
		for (int i = 0; i < 64; i++) onAction();
		Main.instance.gui.onGuiUpdate(GuiUpdate.PROJECT);
	}
	
	public void load(AbstractElement root)
	{
		models.clear();
		textures.clear();
		animations.clear();
		overrideTextureWidth.clear();
		overrideTextureHeight.clear();
		name = root.getString("name", "unnamed project");
		textureWidth = root.getInt("textureWidth", 16);
		textureHeight = root.getInt("textureHeight", 16);
		scale = root.getFloat("scale", 1 / 16f);
		useBackingSkeleton = root.getBoolean("useBackingSkeleton", true);
		modelName = root.getString("model", null);
		textureName = root.getString("texture", null);
		animationName = root.getString("animation", null);
		skeleton = null;
		root.getChildren().forEach(child -> {
			switch (child.getName())
			{
			case "skeleton":
			{
				if (!useBackingSkeleton)
				{
					GuiPopupException.onException("Project file with backing skeleton disabled still contains a backing skeleton definition. This will be ignored.");
				}
				else if (skeleton != null)
				{
					GuiPopupException.onException("Project file contains multiple skeleton definitions. This is not allowed, merging skeletons.");
					skeleton = new Skeleton(child).applySkeleton(skeleton);
				}
				else skeleton = new Skeleton(child);
				break;
			}
			case "texture":
			{
				String textureName = MiscUtil.ensureUnique(child.getString("name", "untitled"), textures.keySet());
				Texture texture = Texture.load(child);
				if (texture != null) textures.put(textureName, texture);
				break;
			}
			case "model":
			{
				String modelName = MiscUtil.ensureUnique(child.getString("name", "untitled"), models.keySet());
				MultiModel model = new MultiModel(child);
				if (child.hasAttribute("textureWidth")) try
				{
					overrideTextureWidth.put(modelName, child.getInt("textureWidth"));
				}
				catch (Exception e)
				{
					GuiPopupException.onException("Invalid \"textureWidth\" value: " + child.getString("textureWidth", "null"), e);
				}
				if (child.hasAttribute("textureHeight")) try
				{
					overrideTextureHeight.put(modelName, child.getInt("textureHeight"));
				}
				catch (Exception e)
				{
					GuiPopupException.onException("Invalid \"textureHeight\" value: " + child.getString("textureHeight", "null"), e);
				}
				models.put(modelName, model);
				break;
			}
			case "animation":
			{
				String animationName = MiscUtil.ensureUnique(child.getString("name", "untitled"), animations.keySet());
				Animation animation = new Animation(child);
				animations.put(animationName, animation);
				break;
			}
			case "pose":
			{
				String animationName = MiscUtil.ensureUnique(child.getString("name", "untitled"), animations.keySet());
				Pose animation = new Pose(child);
				animations.put(animationName, animation);
				break;
			}
			default:
			{
				GuiPopupException.onException("Encountered unknown project element " + child.getName());
				break;
			}
			}
		});
		if (skeleton == null && useBackingSkeleton) skeleton = new Skeleton();
		updateSkeletonGlobal();
		if (modelName == null)
		{
			model = null;
			viewTextureWidth = textureWidth;
			viewTextureHeight = textureHeight;
		}
		else if ((model = models.get(modelName)) == null)
		{
			modelName = null;
			viewTextureWidth = textureWidth;
			viewTextureHeight = textureHeight;
		}
		else
		{
			viewTextureWidth = overrideTextureWidth.containsKey(modelName) ? overrideTextureWidth.get(modelName) : textureWidth;
			viewTextureHeight = overrideTextureHeight.containsKey(modelName) ? overrideTextureHeight.get(modelName) : textureHeight;
		}
		if (textureName == null) texture = null;
		else if ((texture = textures.get(textureName)) == null) textureName = null;
		if (animationName == null) animation = null;
		else if ((animation = animations.get(animationName)) == null) animationName = null;
		if (model != null) model.updateTex();
		Main.instance.gui.onGuiUpdate(GuiUpdate.PROJECT);
	}
	
	public void updateSkeletonLocal()
	{
		if (model != null)
		{
			if (useBackingSkeleton)
			{
				skeleton.applySkeletonTransforms(model.getSkeleton());
				model.applySkeletonTransforms(skeleton = model.getSkeleton().applySkeleton(skeleton));
				model.getRootBones().forEach(bone -> addBoneView(bone, boneView));
			}
			else rebuildBoneView();
		}
	}
	
	public void updateSkeletonLocalAlt()
	{
		if (model != null)
		{
			if (useBackingSkeleton)
			{
				model.applySkeletonTransforms(skeleton);
				model.getRootBones().forEach(bone -> addBoneView(bone, boneView));
			}
			else rebuildBoneView();
		}
	}
	
	public void updateSkeletonGlobal()
	{
		if (useBackingSkeleton) models.values().forEach(model -> model.applySkeletonTransforms(skeleton = model.getSkeleton().applySkeleton(skeleton)));
		rebuildBoneView();
	}
}