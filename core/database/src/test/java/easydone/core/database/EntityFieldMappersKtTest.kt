package easydone.core.database

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import easydone.core.database.model.DbTaskType
import org.junit.Test
import java.time.LocalDate

class EntityFieldMappersKtTest {

    @Test
    fun `Type mapper`() {
        val mapper = TypeMapper
        assertThat(mapper.toValue(mapper.toString(null))).isNull()
        assertThat(mapper.toValue(mapper.toString(DbTaskType.INBOX))).isEqualTo(DbTaskType.INBOX)
    }

    @Test
    fun `Boolean mapper`() {
        val mapper = BooleanMapper
        assertThat(mapper.toValue(mapper.toString(null))).isNull()
        assertThat(mapper.toValue(mapper.toString(true))).isEqualTo(true)
    }

    @Test
    fun `String mapper`() {
        val mapper = StringMapper
        assertThat(mapper.toValue(mapper.toString(null))).isNull()
        assertThat(mapper.toValue(mapper.toString(""))).isNull()
        assertThat(mapper.toValue(mapper.toString("some string"))).isEqualTo("some string")
    }

    @Test
    fun `Date mapper`() {
        val mapper = DateMapper
        //date without seconds
        val date = DateColumnAdapter.decode(DateColumnAdapter.encode(LocalDate.now()))
        assertThat(mapper.toValue(mapper.toString(null))).isNull()
        assertThat(mapper.toValue(mapper.toString(date))).isEqualTo(date)
    }
}
