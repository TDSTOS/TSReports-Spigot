package de.todesstoss.tsreports.util.inventory

class Paginator {

    private var currentPage = 0
    private var sizePerPage = 0
    private var totalElements = 0
    private var collection: Collection<*>? = null

    constructor(
        sizePerPage: Int,
        totalElements: Int
    ) {
        require(sizePerPage >= 1) { "sizePerPage cannot be less than 1" }
        require(totalElements >= 0) { "totalElements cannot be less than 0" }
        this.sizePerPage = sizePerPage
        this.totalElements = totalElements
    }

    constructor(
        sizePerPage: Int,
        collection: Collection<*>?
    ) {
        require(sizePerPage >= 1) { "sizePerPage cannot be less than 1" }
        this.sizePerPage = sizePerPage
        this.collection = collection
    }

    /**
     * @return the total elements
     * @author RoinujNosde
     */
    fun getTotalElements(): Int {
        return if (collection != null) {
            collection!!.size
        } else totalElements
    }

    /**
     * @return the minimal index based on the current page
     * @author RoinujNosde
     */
    val minIndex: Int
        get() = currentPage * sizePerPage

    /**
     * @return the max index based on the current page
     * @author RoinujNosde
     */
    val maxIndex: Int
        get() = (currentPage + 1) * sizePerPage

    /**
     * Increases the page number if there are elements to display
     *
     * @author RoinujNosde
     */
    fun nextPage(): Boolean
    {
        if (!hasNextPage) {
            return false
        }
        currentPage++
        return true
    }

    /**
     * @return if there is a next page
     */
    val hasNextPage: Boolean
        get() = sizePerPage * (currentPage + 1) <= getTotalElements()

    /**
     * Decreases the page number if current &gt; 0
     *
     * @author RoinujNosde
     */
    fun previousPage(): Boolean
    {
        if (hasPreviousPage) {
            currentPage--
            return true
        }
        return false
    }

    /**
     * @return if there is a previous page
     */
    val hasPreviousPage: Boolean
        get() = currentPage > 0

    /**
     * @param index the index
     * @return if this index will not cause any IndexOutOfBoundsException
     * @author RoinujNosde
     */
    fun isValidIndex(index: Int): Boolean {
        return index in minIndex until maxIndex && index < getTotalElements()
    }

}