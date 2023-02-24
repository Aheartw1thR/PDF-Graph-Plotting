package pdf;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of entire PDF file.
 *
 */
public class PDF {
	
	List<PDFObject> objects = new ArrayList<>();

	private static final String VERSION = "%PDF-1.1\n";
	private CatalogObject catalogObject;

	private int objectCount = 0;

	public PDF(CatalogObject catalogObject) {
		this.catalogObject = catalogObject;
	}

	public String build() {
		populateObjects();
		populateObjectNumbersAndOffsets();
		StringBuilder pdf = new StringBuilder();
		Xref xref = new Xref();
		String xrefSection = xref.generate(objects);
		pdf.append(VERSION);
		for (PDFObject object : objects) {
			pdf.append(object.build());
		}
		pdf.append(xrefSection);
		pdf.append("trailer\n  << /Root " + catalogObject.getObjectNumber() + " "
				+ catalogObject.getGeneration() + " R" + "\n   /Size " + (objectCount + 1) + "\n  >>\n"
				+ "%%EOF");
		return pdf.toString();
	}

	private void populateObjects() {
		objects.add(catalogObject);
		objects.add(catalogObject.getPages());
		for (PageObject page : catalogObject.getPages().getPages()) {
			objects.add(page);
			if (page.getContent() != null) {
				objects.add(page.getContent());
			}
		}
	}
	private void populateObjectNumbersAndOffsets() {
		int offset = VERSION.length();
		for (PDFObject object : objects) {
			object.setObjectNumber(++objectCount);
			object.setOffset(offset);
			offset += object.build().length();
		}
	}

}















