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
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;

import firemerald.mcms.api.animation.Animation;
import firemerald.mcms.api.animation.AnimationState;
import firemerald.mcms.api.animation.IAnimation;
import firemerald.mcms.api.animation.Pose;
import firemerald.mcms.api.data.AbstractElement;
import firemerald.mcms.api.model.Bone;
import firemerald.mcms.api.model.IModel;
import firemerald.mcms.api.model.IRigged;
import firemerald.mcms.api.model.Skeleton;
import firemerald.mcms.api.model.effects.EffectsData;
import firemerald.mcms.api.util.FileUtil;
import firemerald.mcms.api.util.FileUtil.DataType;
import firemerald.mcms.gui.popups.GuiPopupException;
import firemerald.mcms.gui.popups.GuiPopupUnsavedChanges;
import firemerald.mcms.model.ProjectModel;
import firemerald.mcms.model.RenderObjectComponents;
import firemerald.mcms.texture.Texture;
import firemerald.mcms.util.EnumPlaybackMode;
import firemerald.mcms.util.FileUtils;
import firemerald.mcms.util.GuiUpdate;
import firemerald.mcms.util.IntReference;
import firemerald.mcms.util.MiscUtil;
import firemerald.mcms.util.Pair;
import firemerald.mcms.util.Reference;
import firemerald.mcms.util.history.ActionHistory;
import firemerald.mcms.util.history.IHistoryAction;

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
	protected Skeleton skeleton;
	protected final List<BoneView> boneView = new ArrayList<>();
	protected final Map<String, IModel<?, ? extends RenderObjectComponents<?>>> models = new LinkedHashMap<>();
	protected String modelName;
	protected IModel<?, ? extends RenderObjectComponents<?>> model;
	protected final Map<String, Integer> overrideTextureWidth = new HashMap<>();
	protected final Map<String, Integer> overrideTextureHeight = new HashMap<>();
	protected final Map<String, Texture> textures = new LinkedHashMap<>();
	protected String textureName;
	protected Texture texture;
	protected final Map<String, Pair<IAnimation, ExtendedAnimationState>> animations = new LinkedHashMap<>();
	protected String animationName;
	protected IAnimation animation;
	protected ExtendedAnimationState animationState;
	public final Map<IModel<?, ? extends RenderObjectComponents<?>>, Texture> lockedModels = new HashMap<>();
	
	public static class ExtendedAnimationState extends AnimationState
	{
		public EnumPlaybackMode animMode = EnumPlaybackMode.PAUSED;
		public boolean animLoop = false;
		public float scale = 1;
		public boolean locked = false;
		
		public ExtendedAnimationState(IAnimation animation)
		{
			super(() -> animation);
		}

		public void tick(float deltaTime)
		{
			if (animMode.step != 0)
			{
				time += deltaTime * animMode.step * scale;
				float length = anim.get().getLength();
				if (animLoop)
				{
					time %= length;
					if (time < 0) time += length;
				}
				else if (time >= length)
				{
					time = length;
					animMode = EnumPlaybackMode.PAUSED;
				}
				else if (time <= 0)
				{
					time = 0;
					animMode = EnumPlaybackMode.PAUSED;
				}
			}
		}
	}
	
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
	public int getProjectTextureWidth()
	{
		return textureWidth;
	}
	
	public void setTextureWidth(int textureWidth)
	{
		if (!overrideTextureWidth.containsKey(modelName)) viewTextureWidth = textureWidth;
		this.textureWidth = textureWidth;
		this.models.forEach((modelName, model) -> {if (!overrideTextureWidth.containsKey(modelName)) model.updateTex();});
		Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
	}

	public boolean hasModelTextureWidth()
	{
		return this.overrideTextureWidth.containsKey(modelName);
	}
	
	public Integer getModelTextureWidth()
	{
		return this.overrideTextureWidth.get(modelName);
	}
	
	public void setModelTextureWidth(int textureWidth)
	{
		viewTextureWidth = textureWidth;
		this.overrideTextureWidth.put(modelName, textureWidth);
		model.updateTex();
		Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public void removeModelTextureWidth()
	{
		viewTextureWidth = textureWidth;
		this.overrideTextureWidth.remove(modelName);
		model.updateTex();
		Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public void setModelTextureWidth(String modelName, int textureWidth)
	{
		if (modelName.equals(this.modelName)) setModelTextureWidth(textureWidth);
		else
		{
			this.overrideTextureWidth.put(modelName, textureWidth);
			models.get(modelName).updateTex();
			Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
		}
	}
	
	public void removeModelTextureWidth(String modelName)
	{
		if (modelName.equals(this.modelName)) removeModelTextureWidth();
		else
		{
			this.overrideTextureWidth.remove(modelName);
			models.get(modelName).updateTex();
			Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
		}
	}

	public int getTextureHeight()
	{
		return viewTextureHeight;
	}
	
	public int getProjectTextureHeight()
	{
		return textureHeight;
	}
	
	public void setTextureHeight(int textureHeight)
	{
		if (!overrideTextureHeight.containsKey(modelName)) viewTextureHeight = textureHeight;
		this.textureHeight = textureHeight;
		this.models.forEach((modelName, model) -> {if (!overrideTextureHeight.containsKey(modelName)) model.updateTex();});
		Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
	}

	public boolean hasModelTextureHeight()
	{
		return this.overrideTextureHeight.containsKey(modelName);
	}

	public Integer getModelTextureHeight()
	{
		return this.overrideTextureHeight.get(modelName);
	}
	
	public void setModelTextureHeight(int textureHeight)
	{
		viewTextureHeight = textureHeight;
		this.overrideTextureHeight.put(modelName, textureHeight);
		model.updateTex();
		Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public void removeModelTextureHeight()
	{
		viewTextureHeight = textureHeight;
		this.overrideTextureHeight.remove(modelName);
		model.updateTex();
		Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public void setModelTextureHeight(String modelName, int textureWidth)
	{
		if (modelName.equals(this.modelName)) setModelTextureHeight(textureWidth);
		else
		{
			this.overrideTextureHeight.put(modelName, textureWidth);
			models.get(modelName).updateTex();
			Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
		}
	}
	
	public void removeModelTextureHeight(String modelName)
	{
		if (modelName.equals(this.modelName)) removeModelTextureHeight();
		else
		{
			this.overrideTextureHeight.remove(modelName);
			models.get(modelName).updateTex();
			Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
		}
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
		Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
	}
	
	public void removeModelTextureSize()
	{
		viewTextureWidth = textureWidth;
		viewTextureHeight = textureHeight;
		this.overrideTextureWidth.remove(modelName);
		this.overrideTextureHeight.remove(modelName);
		model.updateTex();
		Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
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
	
	public void setBackingSkeleton(boolean use)
	{
		if (!use)
		{
			this.useBackingSkeleton = false;
			this.skeleton = null;
			this.rebuildBoneView();
		}
		else
		{
			this.useBackingSkeleton = true;
			this.skeleton = new Skeleton();
			this.models.values().forEach(model -> {
				skeleton.applySkeletonTransforms(model.getSkeleton());
				model.applySkeletonTransforms(skeleton = model.getSkeleton().applySkeleton(skeleton));
				model.getRootBones().forEach(bone -> addBoneView(bone, boneView));
			});
			this.models.values().forEach(model -> model.applySkeletonTransforms(skeleton));
			this.rebuildBoneView();
		}
		Main.instance.onGuiUpdate(GuiUpdate.PROJECT);
	}
	
	public IRigged<?, ?> getRig()
	{
		return model == null ? useBackingSkeleton ? skeleton : null : model;
	}
	
	public IRigged<?, ?> getCompletestRig()
	{
		return useBackingSkeleton ? skeleton : model == null ? null : model;
	}
	
	public Skeleton getSkeleton()
	{
		return useBackingSkeleton ? skeleton : null;
	}
	
	public List<String> getAllBoneNames()
	{
		List<String> list = new ArrayList<>();
		if (this.boneView != null) this.boneView.forEach(view -> getAllBoneNames(view, list));
		return list;
	}
	
	public Bone<?> getBone(String name)
	{
		IRigged<?, ?> rig = this.getRig();
		return rig == null ? null : rig.getBone(name);
	}
	
	private void getAllBoneNames(BoneView view, List<String> list)
	{
		list.add(view.name);
		view.children.forEach(child -> getAllBoneNames(child, list));
	}
	
	public List<String> getDisplayBoneNames(Bone<?> selected)
	{
		return getDisplayBoneNames(selected, new ArrayList<>());
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getDisplayBoneNames(Bone<?> selected, List<String> list)
	{
		final Bone<?> sel = selected;
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
			List<Bone<?>> bones;
			if (sel == null)
			{
				IRigged<?, ?> rig = Main.instance.project.getRig();
				if (rig == null) bones = Collections.emptyList();
				else bones = (List<Bone<?>>) rig.getRootBones();
			}
			else bones = (List<Bone<?>>) sel.children;
			for (Bone<?> bone : bones) blacklist.add(bone.getName());
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
	
	public IModel<?, ? extends RenderObjectComponents<?>> getModel()
	{
		return model;
	}
	
	public String getModelName()
	{
		return modelName;
	}

	public void setModelName(String modelName)
	{
		if (this.modelName != null && !this.modelName.equals(modelName))
		{
			this.setNeedsSave();
			Map<String, IModel<?, ? extends RenderObjectComponents<?>>> copy = new LinkedHashMap<>(this.models);
			this.models.clear();
			copy.forEach((name, model) -> this.models.put(name.equals(this.modelName) ? modelName : name, model));
			this.modelName = modelName;
			Main.instance.onGuiUpdate(GuiUpdate.MODEL);
		}
	}

	public void setModelName(String oldName, String modelName)
	{
		if (!oldName.equals(modelName))
		{
			if (oldName.equals(this.modelName)) setModelName(modelName);
			else
			{
				this.setNeedsSave();
				Map<String, IModel<?, ? extends RenderObjectComponents<?>>> copy = new LinkedHashMap<>(this.models);
				this.models.clear();
				copy.forEach((name, model) -> this.models.put(name.equals(oldName) ? modelName : name, model));
				if (this.modelName.equals(oldName)) this.modelName = modelName;
				Main.instance.onGuiUpdate(GuiUpdate.MODEL);
			}
		}
	}
	
	private void addBoneView(Bone<?> bone, List<BoneView> parentView)
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
		if (modelName == null ? this.modelName != null : !modelName.equals(this.modelName))
		{
			this.setNeedsSave();
			Main.instance.setEditing(null);
			updateSkeletonLocal();
			if (modelName == null)
			{
				this.modelName = null;
				model = null;
				viewTextureWidth = textureWidth;
				viewTextureHeight = textureHeight;
				Main.instance.onGuiUpdate(GuiUpdate.MODEL);
			}
			else
			{
				IModel<?, ? extends RenderObjectComponents<?>> model = models.get(modelName);
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
					Main.instance.onGuiUpdate(GuiUpdate.MODEL);
					if (lockedModels.containsKey(model)) this.setTexture(lockedModels.get(model));
				}
			}
		}
	}
	
	public String getModelName(IModel<?, ? extends RenderObjectComponents<?>> model)
	{
		if (model == null) return null;
		else try
		{
			return models.entrySet().stream().filter(entry -> entry.getValue() == model).map(entry -> entry.getKey()).findFirst().get();
		}
		catch (NoSuchElementException e)
		{
			return null;
		}
	}

	public void setModel(IModel<?, ? extends RenderObjectComponents<?>> model)
	{
		if (model != this.model)
		{
			this.setNeedsSave();
			Main.instance.setEditing(null);
			updateSkeletonLocal();
			if (model == null)
			{
				modelName = null;
				this.model = null;
				viewTextureWidth = textureWidth;
				viewTextureHeight = textureHeight;
				Main.instance.onGuiUpdate(GuiUpdate.MODEL);
			}
			else try
			{
				String modelName = this.models.entrySet().stream().filter(entry -> entry.getValue() == model).map(entry -> entry.getKey()).findFirst().get();
				if (modelName != null)
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
					Main.instance.onGuiUpdate(GuiUpdate.MODEL);
					if (lockedModels.containsKey(model)) this.setTexture(lockedModels.get(model));
				}
			}
			catch (NoSuchElementException e) {}
		}
	}
	
	public void addModel(String modelName, IModel<?, ? extends RenderObjectComponents<?>> model)
	{
		if (modelName == null) modelName = MiscUtil.ensureUnique("untitled", models.keySet());
		if (!modelName.equals(this.modelName) || model != this.model)
		{
			this.setNeedsSave();
			Main.instance.setEditing(null);
			models.put(modelName, model);
			this.modelName = modelName;
			this.model = model;
			viewTextureWidth = textureWidth;
			viewTextureHeight = textureHeight;
			updateSkeletonLocal();
			Main.instance.onGuiUpdate(GuiUpdate.MODEL);
			if (lockedModels.containsKey(model)) this.setTexture(lockedModels.get(model));
		}
	}
	
	public void removeModel()
	{
		if (modelName != null)
		{
			this.setNeedsSave();
			String prevModelName = null;
			for (String name : models.keySet())
			{
				if (name.equals(modelName)) break;
				prevModelName = name;
			}
			lockedModels.remove(models.remove(modelName));
			overrideTextureWidth.remove(modelName);
			overrideTextureHeight.remove(modelName);
			this.setModel(prevModelName);
			if (!useBackingSkeleton) rebuildBoneView();
		}
	}
	
	public void removeModel(String modelName)
	{
		if (modelName == this.modelName) removeModel();
		else if (modelName != null)
		{
			this.setNeedsSave();
			lockedModels.remove(models.remove(modelName));
			Main.instance.onGuiUpdate(GuiUpdate.MODEL);
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
	
	public Texture getTexture(String name)
	{
		return textures.get(name);
	}
	
	public String getTextureName()
	{
		return textureName;
	}
	
	public void setTextureName(String textureName)
	{
		if (this.textureName != null && !this.textureName.equals(textureName))
		{
			this.setNeedsSave();
			Map<String, Texture> copy = new LinkedHashMap<>(this.textures);
			this.textures.clear();
			copy.forEach((name, texture) -> this.textures.put(name.equals(this.textureName) ? textureName : name, texture));
			this.textureName = textureName;
			Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
		}
	}
	
	public void setTextureName(String oldTextureName, String textureName)
	{
		if (!oldTextureName.equals(textureName))
		{
			if (this.textureName.equals(oldTextureName)) setTextureName(textureName);
			else
			{
				this.setNeedsSave();
				Map<String, Texture> copy = new LinkedHashMap<>(this.textures);
				this.textures.clear();
				copy.forEach((name, texture) -> this.textures.put(name.equals(oldTextureName) ? textureName : name, texture));
				Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
			}
		}
	}

	public void setTexture(String textureName)
	{
		if (textureName == null ? this.textureName != null : !textureName.equals(this.textureName))
		{
			this.setNeedsSave();
			Main.instance.setEditing(null);
			if (textureName == null)
			{
				this.textureName = null;
				texture = null;
				if (lockedModels.containsKey(model)) lockedModels.put(model, null);
				Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
			}
			else
			{
				Texture texture = textures.get(textureName);
				if (texture != null)
				{
					this.textureName = textureName;
					this.texture = texture;
					if (lockedModels.containsKey(model)) lockedModels.put(model, texture);
					Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
				}
			}
		}
	}

	public String getTextureName(Texture texture)
	{
		if (texture == null) return null;
		else try
		{
			return textures.entrySet().stream().filter(entry -> entry.getValue() == texture).map(entry -> entry.getKey()).findFirst().get();
		}
		catch (NoSuchElementException e)
		{
			return null;
		}
	}
	
	public void setTexture(Texture texture)
	{
		if (texture != this.texture)
		{
			this.setNeedsSave();
			Main.instance.setEditing(null);
			if (texture == null)
			{
				textureName = null;
				this.texture = null;
				if (lockedModels.containsKey(model)) lockedModels.put(model, null);
				Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
			}
			else try
			{
				String textureName = textures.entrySet().stream().filter(entry -> entry.getValue() == texture).map(entry -> entry.getKey()).findFirst().get();
				if (textureName != null)
				{
					this.textureName = textureName;
					this.texture = texture;
					if (lockedModels.containsKey(model)) lockedModels.put(model, texture);
					Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
				}
			}
			catch (NoSuchElementException e) {}
		}
	}
	
	public void addTexture(String textureName, Texture texture)
	{
		if (textureName == null) textureName = MiscUtil.ensureUnique("untitled", textures.keySet());
		if (!textureName.equals(this.textureName) || texture != this.texture)
		{
			this.setNeedsSave();
			Main.instance.setEditing(null);
			textures.put(textureName, texture);
			this.textureName = textureName;
			this.texture = texture;
			if (lockedModels.containsKey(model)) lockedModels.put(model, texture);
			Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
		}
	}
	
	public void removeTexture()
	{
		if (textureName != null)
		{
			this.setNeedsSave();
			String prevTextureName = null;
			for (String name : textures.keySet())
			{
				if (name.equals(textureName)) break;
				prevTextureName = name;
			}
			Texture tex = textures.remove(textureName);
			if (lockedModels.get(model) == tex) lockedModels.put(model, null);
			this.setTexture(prevTextureName);
		}
	}
	
	public void removeTexture(String textureName)
	{
		if (textureName == this.textureName) removeTexture();
		else if (textureName != null)
		{
			this.setNeedsSave();
			Texture tex = textures.remove(textureName);
			if (lockedModels.get(model) == tex) lockedModels.put(model, null);
			Main.instance.onGuiUpdate(GuiUpdate.TEXTURE);
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
	
	public ExtendedAnimationState getAnimationState()
	{
		return animationState;
	}
	
	public void tickAnims(float deltaTime)
	{
		this.animations.values().stream().map(pair -> pair.right).forEach(state -> state.tick(deltaTime));
	}
	
	public ExtendedAnimationState[] getStates()
	{
		Set<ExtendedAnimationState> active = this.animations.values().stream().map(pair -> pair.right).filter(state -> state.locked || state == this.animationState).collect(Collectors.toSet());
		ExtendedAnimationState[] array = new ExtendedAnimationState[active.size()];
		IntReference index = new IntReference(0);
		active.forEach(state -> array[index.val++] = state);
		return array;
	}
	
	public String getAnimationName()
	{
		return animationName;
	}
	
	public void setAnimationName(String animationName)
	{
		if (this.animationName != null && !this.animationName.equals(animationName))
		{
			this.setNeedsSave();
			Map<String, Pair<IAnimation, ExtendedAnimationState>> copy = new LinkedHashMap<>(this.animations);
			this.animations.clear();
			copy.forEach((name, animation) -> this.animations.put(name.equals(this.animationName) ? animationName : name, animation));
			this.animationName = animationName;
			Main.instance.onGuiUpdate(GuiUpdate.ANIMATION);
		}
	}
	
	public void setAnimationName(String oldName, String animationName)
	{
		if (!oldName.equals(animationName))
		{
			if (oldName.equals(this.animationName)) setAnimationName(animationName);
			else
			{
				this.setNeedsSave();
				Map<String, Pair<IAnimation, ExtendedAnimationState>> copy = new LinkedHashMap<>(this.animations);
				this.animations.clear();
				copy.forEach((name, animation) -> this.animations.put(name.equals(oldName) ? animationName : name, animation));
				Main.instance.onGuiUpdate(GuiUpdate.ANIMATION);
			}
		}
	}
	
	public void setAnimation(String animationName)
	{
		if (animationName == null ? this.animationName != null : !animationName.equals(this.animationName))
		{
			this.setNeedsSave();
			Main.instance.setEditing(null);
			if (animationName == null)
			{
				this.animationName = null;
				animation = null;
			}
			else
			{
				Pair<IAnimation, ExtendedAnimationState> animation = animations.get(animationName);
				if (animation != null)
				{
					if (this.animationState != null && !this.animationState.locked)
					{
						this.animationState.time = 0;
						this.animationState.animLoop = false;
						this.animationState.animMode = EnumPlaybackMode.PAUSED;
					}
					this.animationName = animationName;
					this.animation = animation.left;
					this.animationState = animation.right;
				}
			}
			Main.instance.onGuiUpdate(GuiUpdate.ANIMATION);
		}
	}
	
	public void addAnimation(String animationName, IAnimation animation)
	{
		if (animationName == null) animationName = MiscUtil.ensureUnique("untitled", animations.keySet());
		if (!animationName.equals(this.animationName) || animation != this.animation)
		{
			this.setNeedsSave();
			Main.instance.setEditing(null);
			if (this.animationState != null && !this.animationState.locked)
			{
				this.animationState.time = 0;
				this.animationState.animLoop = false;
				this.animationState.animMode = EnumPlaybackMode.PAUSED;
			}
			this.animationName = animationName;
			this.animation = animation;
			this.animationState = new ExtendedAnimationState(animation);
			animations.put(animationName, new Pair<>(animation, animationState));
			Main.instance.onGuiUpdate(GuiUpdate.ANIMATION);
		}
	}
	
	public void removeAnimation()
	{
		if (animationName != null)
		{
			this.setNeedsSave();
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
	
	public void removeAnimation(String animationName)
	{
		if (animationName == this.animationName) removeAnimation();
		else if (animationName != null)
		{
			this.setNeedsSave();
			animations.remove(animationName);
			Main.instance.onGuiUpdate(GuiUpdate.ANIMATION);
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
					file = new File(dir, models.size() > 1 ? name + ".skel" : "skeleton.skel"); //if there's only 1 model use skeleton.skel
					try
					{
						type.saveElement(el, file);
					} catch (Exception e) {
						GuiPopupException.onException("Couldn't save skeleton file: " + file, e, Level.WARN);
					}
				}
				EffectsData efx = new EffectsData(model);
				if (!efx.effects.isEmpty())
				{
					AbstractElement el = type.newElement("effects");
					new EffectsData(model).save(el, Main.instance.project.scale);
					file = new File(dir, models.size() > 1 ? name + ".bfx" : "effects.bfx");
					try
					{
						type.saveElement(el, file);
					} catch (Exception e) {
						GuiPopupException.onException("Couldn't save model effects file: " + file, e, Level.WARN);
					}
				}
			});
			this.animations.forEach((name, anim) -> { //save animations
				AbstractElement el = type.newElement(anim.left.getElementName());
				anim.left.save(el, scale);
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

	private ActionHistory currentHistory = new ActionHistory();
	private final Stack<ActionHistory> undoStack = new Stack<>();
	
	public void pushUndoStack(ActionHistory stack)
	{
		undoStack.push(currentHistory);
		currentHistory = stack;
	}
	
	public void popUndoStack()
	{
		if (undoStack.isEmpty()) throw new IllegalStateException("Undo stack underflow");
		currentHistory = undoStack.pop();
	}
	
	public boolean canUndo()
	{
		return currentHistory.canUndo();
	}
	
	public boolean canRedo()
	{
		return currentHistory.canRedo();
	}
	
	public void undo()
	{
		currentHistory.undo();
	}

	public void redo()
	{
		currentHistory.redo();
	}
	
	public void onAction(IHistoryAction<?> undoThisAction)
	{
		if (!Main.instance.gui.onAction(undoThisAction))
		{
			this.setNeedsSave();
			currentHistory.onAction(undoThisAction);
		}
	}
	
	public void clearActions()
	{
		currentHistory.clearActions();
	}
	
	private boolean needsSave;
	
	public boolean needsSave()
	{
		return needsSave;
	}
	
	public void setNeedsSave()
	{
		needsSave = true;
	}
	
	public void clearNeedsSave()
	{
		needsSave = false;
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
			clearNeedsSave();
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
			AbstractElement el = root.addChild(animation.left.getElementName());
			el.setString("name", name);
			animation.left.save(el);
		});
		if (!lockedModels.isEmpty())
		{
			AbstractElement el = root.addChild("LockedModels");
			lockedModels.forEach((model, texture) -> {
				AbstractElement e = el.addChild("model");
				e.setString("name", this.getModelName(model));
				if (texture != null) e.setString("texture", this.getTextureName(texture));
			});
		}
		Reference<AbstractElement> refEl = new Reference<>(null);
		animations.forEach((name, animation) -> {
			if (animation.right.locked)
			{
				AbstractElement el = refEl.val;
				if (el == null) el = refEl.val = root.addChild("LockedAnimations");
				AbstractElement e = el.addChild("anim");
				e.setString("name", name);
				e.setFloat("time", animation.right.time);
				e.setFloat("scale", animation.right.scale);
				e.setBoolean("loop", animation.right.animLoop);
				e.setEnum("mode", animation.right.animMode);
			}
		});
	}
	
	public void load()
	{
		if (getSource() != null) load(getSource());
		else loadFrom();
	}
	
	public void loadFrom()
	{
		if (this.needsSave())
		{
			new GuiPopupUnsavedChanges(() -> {
				File in = FileUtils.getOpenFile("mcms;xml;json;bin", getSource() == null ? "" : getSource().toString());
				if (in != null) load(in);
			}).activate();
		}
		else
		{
			File in = FileUtils.getOpenFile("mcms;xml;json;bin", getSource() == null ? "" : getSource().toString());
			if (in != null) load(in);
		}
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
		clearNeedsSave();
		models.clear();
		textures.clear();
		lockedModels.clear();
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
		Main.instance.onGuiUpdate(GuiUpdate.PROJECT);
	}
	
	static class AnimationLockData
	{
		final float time, scale;
		final boolean loop;
		final EnumPlaybackMode mode;
		
		AnimationLockData(float time, float scale, boolean loop, EnumPlaybackMode mode)
		{
			this.time = time;
			this.scale = scale;
			this.loop = loop;
			this.mode = mode;
		}
	}
	
	public void load(AbstractElement root)
	{
		clearNeedsSave();
		this.currentHistory.clearActions();
		models.clear();
		textures.clear();
		lockedModels.clear();
		animations.clear();
		overrideTextureWidth.clear();
		overrideTextureHeight.clear();
		name = root.getString("name", "unnamed project");
		textureWidth = root.getInt("textureWidth", 16);
		textureHeight = root.getInt("textureHeight", 16);
		scale = root.getFloat("scale", 1 / 16f);
		useBackingSkeleton = root.getBoolean("useBackingSkeleton", true);
		String modelName = root.getString("model", null);
		String textureName = root.getString("texture", null);
		String animationName = root.getString("animation", null);
		skeleton = null;
		final Map<String, String> lockedModelsData = new HashMap<>();
		final Map<String, AnimationLockData> lockedAnimationsData = new HashMap<>();
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
				String texName = MiscUtil.ensureUnique(child.getString("name", "untitled"), textures.keySet());
				Texture texture = Texture.load(child);
				if (texture != null) textures.put(texName, texture);
				break;
			}
			case "model":
			{
				String modName = MiscUtil.ensureUnique(child.getString("name", "untitled"), models.keySet());
				ProjectModel model = new ProjectModel(child);
				if (child.hasAttribute("textureWidth")) try
				{
					overrideTextureWidth.put(modName, child.getInt("textureWidth"));
				}
				catch (Exception e)
				{
					GuiPopupException.onException("Invalid \"textureWidth\" value: " + child.getString("textureWidth", "null"), e);
				}
				if (child.hasAttribute("textureHeight")) try
				{
					overrideTextureHeight.put(modName, child.getInt("textureHeight"));
				}
				catch (Exception e)
				{
					GuiPopupException.onException("Invalid \"textureHeight\" value: " + child.getString("textureHeight", "null"), e);
				}
				models.put(modName, model);
				break;
			}
			case "animation":
			{
				String animName = MiscUtil.ensureUnique(child.getString("name", "untitled"), animations.keySet());
				Animation animation = new Animation(child);
				animations.put(animName, new Pair<>(animation, new ExtendedAnimationState(animation)));
				break;
			}
			case "pose":
			{
				String animName = MiscUtil.ensureUnique(child.getString("name", "untitled"), animations.keySet());
				Pose animation = new Pose(child);
				animations.put(animName, new Pair<>(animation, new ExtendedAnimationState(animation)));
				break;
			}
			case "LockedModels":
			{
				child.getChildren().forEach(el -> lockedModelsData.put(el.getString("name", null), el.getString("texture", null)));
				break;
			}
			case "LockedAnimations":
			{
				child.getChildren().forEach(el -> lockedAnimationsData.put(el.getString("name", null), new AnimationLockData(el.getFloat("time", 0), el.getFloat("scale", 1), el.getBoolean("loop", false), el.getEnum("mode", EnumPlaybackMode.values(), EnumPlaybackMode.PAUSED))));
				break;
			}
			default:
			{
				GuiPopupException.onException("Encountered unknown project element " + child.getName());
				break;
			}
			}
		});
		lockedModelsData.forEach((modName, texName) -> {
			if (modName != null)
			{
				IModel<?, ? extends RenderObjectComponents<?>> model = models.get(modName);
				if (model != null)
				{
					Texture tex;
					if (texName == null) tex = null;
					else
					{
						tex = textures.get(texName);
						if (tex == null) Main.LOGGER.warn("Could not restore locked model's texture " + texName + " as it did not exist in the project file");
					}
					this.lockedModels.put(model, tex);
					this.modelName = modName;
					this.model = model;
					this.viewTextureWidth = overrideTextureWidth.containsKey(modName) ? overrideTextureWidth.get(modName) : textureWidth;
					this.viewTextureHeight = overrideTextureHeight.containsKey(modName) ? overrideTextureHeight.get(modName) : textureHeight;
					model.updateTex();
				}
				else Main.LOGGER.warn("Could not restore locked model " + modName + " as it did not exist in the project file");
			}
		});
		lockedAnimationsData.forEach((animName, data) -> {
			if (animName != null)
			{
				Pair<IAnimation, ExtendedAnimationState> animation = animations.get(animName);
				if (animation != null)
				{
					animation.right.time = data.time;
					animation.right.scale = data.scale;
					animation.right.animLoop = data.loop;
					animation.right.animMode = data.mode;
					animation.right.locked = true;
				}
				else Main.LOGGER.warn("Could not restore locked animation " + animName + " as it did not exist in the project file");
			}
		});
		this.modelName = modelName;
		this.textureName = textureName;
		this.animationName = animationName;
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
		else
		{
			Pair<IAnimation, ExtendedAnimationState> pair = animations.get(animationName);
			if ((animation = pair.left) == null)
			{
				animationState = null;
				animationName = null;
			}
			else animationState = pair.right;
		}
		this.models.values().forEach(IModel::updateTex);
		Main.instance.onGuiUpdate(GuiUpdate.PROJECT);
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