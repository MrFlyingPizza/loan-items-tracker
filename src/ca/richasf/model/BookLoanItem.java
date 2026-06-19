package ca.richasf.model;

public final class BookLoanItem extends LoanItem {

    private int pageCount;

    public BookLoanItem() {
        super();
    }

    /**
     * Get the page count.
     * @return The page count.
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * Set the page count.
     * @param pageCount The new page count.
     */
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
