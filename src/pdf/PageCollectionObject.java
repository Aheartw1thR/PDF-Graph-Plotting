package pdf;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of pages object
 *
 */
public class PageCollectionObject extends PDFObject {

    private List<PageObject> pages = new ArrayList<PageObject>();

    public PageCollectionObject() {
        super("Pages");
    }

    public void addPages(PageObject... pageObjects) {
        for (PageObject pageObject : pageObjects) {
            addPage(pageObject);
        }
    }

    public void addPage(PageObject pageObject) {
        this.pages.add(pageObject);
        pageObject.addAttribute("Parent", getReference());
    }

    @Override
    public void addSpecificAttributes() {
        addAttribute("Count", Integer.valueOf(pages.size()));
        PDFObjectReference[] refArr = new PDFObjectReference[pages.size()];
        for (int i = 0; i < pages.size(); i++) {
            refArr[i] = pages.get(i).getReference();
        }
        addAttribute("Kids", refArr);
    }

    List<PageObject> getPages() {
        return pages;
    }

}
