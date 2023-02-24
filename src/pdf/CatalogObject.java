package pdf;

/**
 * Representation of catalog object
 *
 */
public class CatalogObject extends PDFObject {

    private PageCollectionObject pages;

    public CatalogObject(PageCollectionObject pageCollectionObject) {
        super("Catalog");
        this.pages = pageCollectionObject;
    }

    @Override
    public void addSpecificAttributes() {
        addAttribute("Pages", pages.getReference());
    }

    PageCollectionObject getPages() {
        return pages;
    }

}