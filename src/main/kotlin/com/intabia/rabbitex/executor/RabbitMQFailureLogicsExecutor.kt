package com.intabia.rabbitex.executor

import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component
import com.intabia.rabbitex.dto.LogicResult
import com.intabia.rabbitex.logic.RabbitMQFailureLogicHolder
import com.intabia.rabbitex.manager.logic.FailureLogicChainHolder

@Component
class RabbitMQFailureLogicsExecutor(
    private val failureLogicChainHolder: FailureLogicChainHolder,
) {

    /**
     * Выполнить логики, содержащиеся в [FailureLogicChainHolder]
     */
    fun executeFailureLogics(executionId: String, messageHeaderAccessor: MessageHeaderAccessor): LogicResult {
        return failureLogicChainHolder
            .getFailureLogicsChain()
            .executeAll(executionId, messageHeaderAccessor)
    }

    private fun List<RabbitMQFailureLogicHolder>.executeAll(
        executionId: String,
        messageHeaderAccessor: MessageHeaderAccessor,
    ): LogicResult {
        var failureLogicsExecutionResult: LogicResult = getDefaultResult()
        this
            .filter { it.getIdExecution() == executionId }
            .forEach { failureLogic ->
                failureLogicsExecutionResult = failureLogic.execute(messageHeaderAccessor, failureLogicsExecutionResult)
                if (failureLogicsExecutionResult.needToExecuteNextLogic.not()) return failureLogicsExecutionResult
            }
        return failureLogicsExecutionResult
    }

    private fun RabbitMQFailureLogicHolder.execute(
        messageHeaderAccessor: MessageHeaderAccessor,
        failureLogicCurrentResult: LogicResult,
    ): LogicResult = this.logicExecute().invoke(messageHeaderAccessor, failureLogicCurrentResult)

    private fun getDefaultResult() = LogicResult(false, "", "")
}