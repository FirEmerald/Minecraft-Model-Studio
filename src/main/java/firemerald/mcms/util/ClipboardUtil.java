package firemerald.mcms.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import firemerald.mcms.gui.popups.GuiPopupException;

public class ClipboardUtil
{
	public static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();
	//Plain favor
	private static final DataFlavor PLAIN_FLAVOR = new DataFlavor("text/plain", "Plain Flavor");
	//RTF flavor
	private static final DataFlavor RTF_FLAVOR = new DataFlavor("text/rtf", "Rich Formatted Text");
	
	public static void setString(String str)
	{
		StringSelection selection = new StringSelection(str);
		CLIPBOARD.setContents(selection, selection);
	}
	
	/** returns null if not a string! **/
	public static String getString()
	{
		try
		{
			return (String) CLIPBOARD.getData(DataFlavor.stringFlavor);
		}
		catch (UnsupportedFlavorException e)
		{
			return null;
		}
		catch (IOException e)
		{
			GuiPopupException.onException("Couldn't grab text from clipboard", e);
			return null;
		} 
	}
	
	public static void setImage(Image img)
	{
		ImageSelection selection = new ImageSelection(img);
		CLIPBOARD.setContents(selection, selection);
	}
	
	public static Image getImage()
	{
		try
		{
			return (Image) CLIPBOARD.getData(DataFlavor.imageFlavor);
		}
		catch (UnsupportedFlavorException e)
		{
			return null;
		}
		catch (IOException e)
		{
			GuiPopupException.onException("Couldn't grab image data from clipboard", e);
			return null;
		}
	}
	
	public static void setRTF(String rtf)
	{
		RTFSelection selection = new RTFSelection(rtf);
		CLIPBOARD.setContents(selection, selection);
	}
	
	/** returns null if not an RTF string! **/
	public static String getRTF()
	{
		try
		{
			return toString((ByteArrayInputStream) CLIPBOARD.getData(RTF_FLAVOR));
		}
		catch (UnsupportedFlavorException e)
		{
			return null;
		}
		catch (IOException e)
		{
			GuiPopupException.onException("Could't grab rich text from clipboard", e);
			return null;
		} 
	}
	
	/** returns null if not a string! **/
	public static String getRTForPlain()
	{
		try
		{
			return toString((ByteArrayInputStream) CLIPBOARD.getData(RTF_FLAVOR));
		}
		catch (UnsupportedFlavorException e)
		{
			try
			{
				return (String) CLIPBOARD.getData(DataFlavor.stringFlavor);
			}
			catch (UnsupportedFlavorException e1)
			{
				return null;
			}
			catch (IOException e1)
			{
				GuiPopupException.onException("Couldn't grab text from clipboard", e);
				return null;
			}
		}
		catch (IOException e)
		{
			GuiPopupException.onException("Couldn't grab rich text from clipboard", e);
			return null;
		}
	}
	
	public static String toString(ByteArrayInputStream in)
	{
		byte[] data = new byte[in.available()];
		try
		{
			in.read(data);
		}
		catch (IOException e)
		{
			GuiPopupException.onException("Couldn't convert binary data to a String", e);
		}
		return new String(data);
	}
	
	public static String rtfToPlain(String rtf) //TODO
	{
		return rtf;
	}
	
	public static class ImageSelection implements Transferable, ClipboardOwner
	{
		private final Image img;
		
		public ImageSelection(Image img)
		{
			this.img = img;
		}
		
		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
		{
			if (isDataFlavorSupported(flavor)) return img;
			else throw new UnsupportedFlavorException(flavor);
		}
		
		@Override
		public boolean isDataFlavorSupported (DataFlavor flavor)
		{
			return flavor == DataFlavor.imageFlavor;
		}
		
		@Override
		public DataFlavor[] getTransferDataFlavors ()
		{
			return new DataFlavor[] {DataFlavor.imageFlavor};
		}
		
		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {}
	}
	
	public static class RTFSelection implements Transferable, ClipboardOwner
	{
	    private final Object data[];
	    private final DataFlavor flavors[] = {RTF_FLAVOR, PLAIN_FLAVOR};

	    public RTFSelection(String text)
	    {
	        data = new Object[] {new ByteArrayInputStream(text.getBytes()), new ByteArrayInputStream(rtfToPlain(text).getBytes())};
	    }

	    @Override
		public Object getTransferData (DataFlavor df) throws UnsupportedFlavorException, IOException
	    {
	    	String mimeType = df.getMimeType();
	        if (mimeType.contains("text/rtf"))  return data[0];
	        else if (mimeType.contains("text/plain")) return data[1];
	        else throw new UnsupportedFlavorException(df);
	    }

	    @Override
		public boolean isDataFlavorSupported (DataFlavor df)
	    {
	    	String mimeType = df.getMimeType();
	        return mimeType.contains("text/rtf") || mimeType.contains("text/plain");
	    }

	    @Override
		public DataFlavor[] getTransferDataFlavors()
	    {
	        return flavors;
	    }

		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {}
	 }
}