package easydone.core.domain

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import easydone.core.database.MyDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Test

class DomainRepositoryTest {

    private val database: MyDatabase = mock()

    lateinit var repository: DomainRepository

    @Test
    fun `Get task should call database`() = runBlockingTest {
        val id = "id"
        buildRepository()

        repository.getTask(id)

        verify(database).getTask(id)
    }

    private fun buildRepository() {
        repository = DomainRepository(database)
    }

}
