package com.intabia.rabbitex

import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component
import com.intabia.rabbitex.logic.RabbitMQFailureLogicHolder
import com.intabia.rabbitex.manager.logic.FailureLogicChainHolder

/**
 * Post processor для бинов, являющихся [RabbitMQFailureLogicHolder].
 * Необходим для регистрации логик в [FailureLogicChainHolder]
 */
@Component
class RabbitMQFailureLogicBeanPostProcessor(
    private val failureLogicChainHolder: FailureLogicChainHolder,
) : BeanPostProcessor {
    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        return bean
            .also { candidate -> if (candidate is RabbitMQFailureLogicHolder) failureLogicChainHolder.registerFailureLogic(candidate) }

    }
}
