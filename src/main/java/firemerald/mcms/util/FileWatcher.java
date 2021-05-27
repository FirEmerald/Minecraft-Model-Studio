package firemerald.mcms.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
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
	private final Map<File, DirWatcher> watchers = new HashMap<>();
	
	public FileWatcher() throws IOException
	{
		watcher = FileSystems.getDefault().newWatchService();
	}
	
	public void addWatcher(File file, Consumer<WatchEvent<?>> consumer, Kind<?>... kinds) throws IOException
	{
		file = file.getAbsoluteFile();
		if (file.isDirectory())
		{
			DirWatcher watcher = watchers.get(file);
			if (watcher == null) watchers.put(file, watcher = new DirWatcher(file.toPath().register(this.watcher, ALL_KINDS), file));
			watcher.addConsumer(consumer, kinds);
		}
		else
		{
			File dir = file.getParentFile();
			DirWatcher watcher = watchers.get(dir);
			if (watcher == null) watchers.put(dir, watcher = new DirWatcher(dir.toPath().register(this.watcher, ALL_KINDS), dir));
			watcher.addConsumer(file, consumer, kinds);
		}
	}
	
	public void addWatcher(File file, Consumer<WatchEvent<?>> consumer) throws IOException
	{
		addWatcher(file, consumer, ALL_KINDS);
	}
	
	public void removeWatcher(File file, Consumer<WatchEvent<?>> consumer, Kind<?>... kinds)
	{
		file = file.getAbsoluteFile();
		if (file.isDirectory())
		{
			DirWatcher watcher = watchers.get(file);
			if (watcher != null && watcher.removeConsumer(consumer, kinds))
			{
				watcher.watchKey.cancel();
				watchers.remove(file);
			}
		}
		else
		{
			File dir = file.getParentFile();
			DirWatcher watcher = watchers.get(dir);
			if (watcher != null && watcher.removeConsumer(file, consumer, kinds))
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
	
	public void poll()
	{
		watchers.values().forEach(watcher -> watcher.poll());
	}
	
	private static class DirWatcher
	{
		private final File dir;
		private final Map<File, Map<Kind<?>, List<Consumer<WatchEvent<?>>>>> consumers = new HashMap<>();
		Map<Kind<?>, List<Consumer<WatchEvent<?>>>> dirConsumers = new HashMap<>();
		private final WatchKey watchKey;
		
		public DirWatcher(WatchKey watchKey, File dir)
		{
			this.watchKey = watchKey;
			this.dir = dir;
		}
		
		public void addConsumer(File file, Consumer<WatchEvent<?>> consumer, Kind<?>... kinds)
		{
			file = file.getAbsoluteFile();
			Map<Kind<?>, List<Consumer<WatchEvent<?>>>> map = consumers.get(file);
			if (map == null) consumers.put(file, map = new HashMap<>());
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
		
		public boolean removeConsumer(File file, Consumer<WatchEvent<?>> consumer, Kind<?>... kinds)
		{
			file = file.getAbsoluteFile();
			Map<Kind<?>, List<Consumer<WatchEvent<?>>>> map = consumers.get(file);
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
				if (map.isEmpty()) consumers.remove(file);
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
    			Map<Kind<?>, List<Consumer<WatchEvent<?>>>> map = consumers.get(new File(dir, event.context().toString()));
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