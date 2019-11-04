package easydone.feature.login

import kotlinx.coroutines.flow.Flow


interface TokenProvider {

    fun observeToken(): Flow<String>

}
