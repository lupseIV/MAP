package map.utils.paging;

public class Page<E> {
    private int totalNumberOfElements;
    private Iterable<E> elementsOnPage;

    public Page(Iterable<E> elementsOnPage, int totalNumberOfElements) {
        this.totalNumberOfElements = totalNumberOfElements;
        this.elementsOnPage = elementsOnPage;
    }

    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }
    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }
}
