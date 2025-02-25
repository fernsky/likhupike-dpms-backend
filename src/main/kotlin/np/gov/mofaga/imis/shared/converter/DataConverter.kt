package np.gov.mofaga.imis.shared.converter

interface DataConverter<I, O> {
    fun convert(input: I): O
}
