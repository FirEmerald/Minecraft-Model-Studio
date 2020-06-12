package firemerald.mcms.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FileWatcher
{
	public static final Kind<?>[] ALL_KINDS = {StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY};
	
	private final WatchService watcher;
	private final Map<Path, DirWatcher> watchers = new HashMap<>();
	
	public FileWatcher() throws IOException
	{
		watcher = FileSystems.getDefault().newWatchService();
	}
	
	public void addWatcher(File file, Consumer<WatchEvent<?>> consumer, Kind<?>... kinds) throws IOException
	{
		file = file.getAbsoluteFile();
		if (file.isDirectory()) addWatcher(file.toPath(), consumer, kinds);
		else
		{
			Path rel = file.toPath().toAbsolutePath();
			Path dir = rel.getParent().resolve("./");
			DirWatcher watcher = watchers.get(dir);
			if (watcher == null) watchers.put(dir, watcher = new DirWatcher(dir.register(this.watcher, ALL_KINDS), dir));
			watcher.addConsumer(rel, consumer, kinds);
		}
	}
	
	public void addWatcher(File file, Consumer<WatchEvent<?>> consumer) throws IOException
	{
		addWatcher(file, consumer, ALL_KINDS);
	}
	
	public void addWatcher(Path dir, Consumer<WatchEvent<?>> consumer, Kind<?>... kinds) throws IOException
	{
		dir = dir.toAbsolutePath();
		DirWatcher watcher = watchers.get(dir);
		if (watcher == null) watchers.put(dir, watcher = new DirWatcher(dir.register(this.watcher, ALL_KINDS), dir));
		watcher.addConsumer(consumer, kinds);
	}
	
	public void addWatcher(Path dir, Consumer<WatchEvent<?>> consumer) throws IOException
	{
		addWatcher(dir, consumer, ALL_KINDS);
	}
	
	public void removeWatcher(File file, Consumer<WatchEvent<?>> consumer, Kind<?>... kinds)
	{
		file = file.getAbsoluteFile();
		if (file.isDirectory()) removeWatcher(file.toPath(), consumer, kinds);
		else
		{
			Path rel = file.toPath().toAbsolutePath();
			Path dir = rel.getParent().resolve("./");
			DirWatcher watcher = watchers.get(dir);
			if (watcher != null && watcher.removeConsumer(rel, consumer, kinds))
			{
				watcher.watchKey.cancel();
				watchers.remove(dir);
			}
		}
	}
	
	public void removeWatcher(File file, Consumer<WatchEvent<?>> consumer) 
	{
		removeWatcher(file, consumer, ALL_KINDS);
	}
	
	public void removeWatcher(Path dir, Consumer<WatchEvent<?>> consumer, Kind<?>... kinds)
	{
		dir = dir.toAbsolutePath();
		DirWatcher watcher = watchers.get(dir);
		if (watcher != null && watcher.removeConsumer(consumer, kinds))
		{
			watcher.watchKey.cancel();
			watchers.remove(dir);
		}
	}
	
	public void removeWatcher(Path dir, Consumer<WatchEvent<?>> consumer)
	{
		removeWatcher(dir, consumer, ALL_KINDS);
	}
	
	public void poll()
	{
		watchers.values().forEach(watcher -> watcher.poll());
	}
	
	private static class DirWatcher
	{
		private final Path dir;
		private final Map<Path, Map<Kind<?>, List<Consumer<WatchEvent<?>>>>> consumers = new HashMap<>();
		Map<Kind<?>, List<Consumer<WatchEvent<?>>>> dirConsumers = new HashMap<>();
		private final WatchKey watchKey;
		
		public DirWatcher(WatchKey watchKey, Path dir)
		{
			this.watchKey = watchKey;
			this.dir = dir;
		}
		
		public void addConsumer(Path path, Consumer<WatchEvent<?>> consumer, Kind<?>... kinds)
		{
			path = path.toAbsolutePath();
			Map<Kind<?>, List<Consumer<WatchEvent<?>>>> map = consumers.get(path);
			if (map == null) consumers.put(path, map = new HashMap<>());
			for (Kind<?> kind : kinds)
			{
				List<Consumer<WatchEvent<?>>> list = map.get(kind);
				if (list == null) map.put(kind, list = new ArrayList<>());
				list.add(consumer);
			}
		}
		
		public void addConsumer(Consumer<WatchEvent<?>> consumer, Kind<?>... kinds)
		{
			for (Kind<?> kind : kinds)
			{
				List<Consumer<WatchEvent<?>>> list = dirConsumers.get(kind);
				if (list == null) dirConsumers.put(kind, list = new ArrayList<>());
				list.add(consumer);
			}
		}
		
		public boolean removeConsumer(Path path, Consumer<WatchEvent<?>> consumer, Kind<?>... kinds)
		{
			path = path.toAbsolutePath();
			Map<Kind<?>, List<Consumer<WatchEvent<?>>>> map = consumers.get(path);
			if (map != null)
			{
				for (Kind<?> kind : kinds)
				{
					List<Consumer<WatchEvent<?>>> list = map.get(kind);
					if (list != null)
					{
						list.remove(consumer);
						if (list.isEmpty()) map.remove(kind);
					}
				}
				if (map.isEmpty()) consumers.remove(path);
			}
			return dirConsumers.isEmpty() && consumers.isEmpty();
		}
		
		public boolean removeConsumer(Consumer<WatchEvent<?>> consumer, Kind<?>... kinds)
		{
			for (Kind<?> kind : kinds)
			{
				List<Consumer<WatchEvent<?>>> list = dirConsumers.get(kind);
				if (list != null)
				{
					list.remove(consumer);
					if (list.isEmpty()) dirConsumers.remove(kind);
				}
			}
			return dirConsumers.isEmpty() && consumers.isEmpty();
		}
		
		public void poll()
		{
    		for (WatchEvent<?> event: watchKey.pollEvents())
	    	{
    			List<Consumer<WatchEvent<?>>> list = dirConsumers.get(event.kind());
				if (list != null) list.forEach(consumer -> consumer.accept(event));
    			Map<Kind<?>, List<Consumer<WatchEvent<?>>>> map = consumers.get(((Path) event.context()).toAbsolutePath());
    			if (map != null)
    			{
    				list = map.get(event.kind());
    				if (list != null) list.forEach(consumer -> consumer.accept(event));
    			}
	    	}
    		watchKey.reset();
		}
		
		@Override
		public String toString()
		{
			return "[" + dir + ", " + consumers.toString() + ", " + dirConsumers.toString() + "]";
		}
	}
}