package com.intabia.rabbitex.manager.logic

import org.springframework.stereotype.Component
import com.intabia.rabbitex.logic.RabbitMQFailureLogicHolder

/**
 * Держатель цепочки логик, отрабатывающих при возникновении ошибки.
 */
@Component
class FailureLogicChainHolder {

    private val failureLogicChain = mutableListOf<RabbitMQFailureLogicHolder>()


    /**
     * Метод для получения листа-цепочки логик при ошибке.
     * В данной цепочке важно место каждой логики (очередность).
     * @return - цепочка логик для обработки ошибки.
     */
    fun getFailureLogicsChain(): List<RabbitMQFailureLogicHolder> {
        return failureLogicChain.toList()
    }

    /**
     * Метод для регистрации логики, которые должны отработать при возникновении ошибки, в цепочке логик.
     * @param logic - Логика, которая должна быть встроена в цепочку логик в соответствии с приоритетом.
     * @see RabbitMQFailureLogicHolder
     */
    fun registerFailureLogic(logic: RabbitMQFailureLogicHolder) {
        logic.registerWithPriority()
    }

    private fun RabbitMQFailureLogicHolder.registerWithPriority() {
        var indexBeforeElementWithHigherOrEqualPriority = 0
        failureLogicChain.forEachIndexed { index, rabbitMQFailureLogic ->
            if (rabbitMQFailureLogic.getPriority() >= this.getPriority())
                indexBeforeElementWithHigherOrEqualPriority = index
        }
        failureLogicChain.add(indexBeforeElementWithHigherOrEqualPriority, this)
    }

}