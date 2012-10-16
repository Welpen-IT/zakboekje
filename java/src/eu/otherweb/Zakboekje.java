package eu.otherweb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

public class Zakboekje {

	enum Rotation {
		LEFT("l", 90f), 
		RIGHT("r", 90f), 
		NORMAL("n",0), 
		EMPTY("e",0);
		
		private final String name;
		private final float degrees;

		Rotation(String name, float degrees) {
			this.name = name;
			this.degrees = degrees;
		}

		/**
		 * Parse text into an element of this enumeration.
		 * 
		 * @param takes
		 *            one of the values 'Cash', 'Margin', 'RSP'.
		 */
		public static Rotation valueOfByName(String rotationName) {
			List<Rotation> list = Arrays.asList(Rotation.values());
			for (Rotation item : list) {
				if (item.name.compareToIgnoreCase(rotationName) == 0) {
					return item;
				}
			}

			throw new IllegalArgumentException(
					"Cannot be parsed into an enum element : '" + rotationName + "'");
		}

		public float getDegrees() {
			return degrees;
		}

	}

	
	public class PageFile {
		private Rotation rotation;
		private String fileName;
		
		PageFile(Rotation rotation, String fileName) {
			this.rotation = rotation;
			this.fileName = fileName;
		}

		public void setRotation(Rotation rotation) {
			this.rotation = rotation;
		}

		public Rotation getRotation() {
			return rotation;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileName() {
			return fileName;
		}
		
	}
	
	/**
	 * Main method.
	 * 
	 * @param args
	 *            no arguments needed
	 * @throws DocumentException
	 * @throws IOException
	 */
	public Zakboekje(String[] args) throws IOException, DocumentException {
		
		String in = "../paginas.txt";
		String out = "../zakboekje_A4.pdf";
		
		ArrayList<PageFile> a = getPagesFromFile(in);
		makeBook2xA6onA4(out, SetPageToBook(a));
		
		makeBookA6onA6("../zakboekje.pdf", a);
	}

	/** Read the contents of the given file. */
	private  ArrayList<PageFile> getPagesFromFile(String fileName) throws IOException {
		Scanner scanner = new Scanner(new File(fileName));

		ArrayList<PageFile> pages = new ArrayList<PageFile>();

		try {
			while (scanner.hasNextLine()) {
				String b = scanner.nextLine().trim();
				String[] a = b.split(":");
				Rotation rotation = Rotation.valueOfByName(a[0]);				
				PageFile page = new PageFile(rotation, a[1]);
				pages.add(page);
			}
		} finally {
			scanner.close();
		}

		return pages;
	}
	
	/**
	 * @param pages List of page from 1 to ...
	 * @return List of page order to be print as book.
	 */
	private ArrayList<PageFile> SetPageToBook(ArrayList<PageFile> pages) {
		int a = (pages.size() % 4);
		
		System.out.println("Add pages " + a);
		
		for (int i = 0; i < a; i++) {
			pages.add(new PageFile(Rotation.EMPTY, ""));
		}
		
		if (pages.size() < 4) {
			throw new IllegalArgumentException("No Pages found");
		}
				
		System.out.println(" pages " + pages.size());
		
		int first = 0;
		int last = pages.size() - 1;
		
		ArrayList<PageFile> result = new ArrayList<PageFile>();
		
		while ((last - first) > 0) {
			result.add(pages.get(last--));
			result.add(pages.get(first++));
			if ((last - first) > 0) { 
				result.add(pages.get(first++));
				result.add(pages.get(last--));
			}
		}
		return result;
		
	}
	
	
	/**
	 * Make a A4 pdf to be print so you get 2 A6 books.
	 * @param PdfFileName
	 * @param pages
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void makeBook2xA6onA4(String PdfFileName, ArrayList<PageFile> pages) throws DocumentException, IOException {
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(PdfFileName));
		document.open();
		
		Iterator<PageFile> ipages = pages.iterator();
		
		PageFile leftPage;
		PageFile rightPage;
		while (ipages.hasNext()) {
			leftPage = ipages.next();
			rightPage = ipages.next();
			
			if (leftPage.rotation != Rotation.EMPTY) {
				Image image = getImageFromPdf(writer, leftPage);
				image.setAbsolutePosition(0f, 0f);
				document.add(image);
				image.setAbsolutePosition(0f, 842 / 2);
				document.add(image);
			}
			
			if (rightPage.rotation != Rotation.EMPTY) {
				Image image = getImageFromPdf(writer, rightPage);
				image.setAbsolutePosition(595 / 2, 0f);
				document.add(image);
				image.setAbsolutePosition(595 / 2, 842 / 2);
				document.add(image);
			}
			document.newPage();
		}
		document.close();
	}
	
	public void makeBookA6onA6(String PdfFileName, ArrayList<PageFile> pages) throws DocumentException, IOException {
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(PdfFileName));
		document.setPageSize(new Rectangle( 240.944882f, 283.464567f));
		document.open();

		Iterator<PageFile> ipages = pages.iterator();
		
		PageFile leftPage;
		PageFile rightPage;
		while (ipages.hasNext()) {
			leftPage = ipages.next();
			rightPage = ipages.next();
			
			
			if (leftPage.rotation != Rotation.EMPTY) {
				Image image = getImageFromPdf(writer, leftPage);
				image.setAbsolutePosition(0, -70.8661417f);
				document.add(image);
				
			}
			document.newPage();
			
			if (rightPage.rotation != Rotation.EMPTY) {
				Image image = getImageFromPdf(writer, rightPage);
				image.setAbsolutePosition(-56.6929134f, -70.8661417f);
				document.add(image);
			}
			document.newPage();
		}
		document.close();
	}

	
	public Image getImageFromPdf(PdfWriter writer, PageFile page) throws BadElementException, IOException {
		Image image = Image.getInstance(writer.getImportedPage(new PdfReader(page.getFileName()), 1));
		image.setBorder(Image.BOX);
		image.setRotationDegrees(page.getRotation().getDegrees());
		System.out.println("B " + page.getFileName());
		return image;
	}

	public static void main(String[] args) throws IOException, DocumentException {
		new Zakboekje(args);

	}
	
}
