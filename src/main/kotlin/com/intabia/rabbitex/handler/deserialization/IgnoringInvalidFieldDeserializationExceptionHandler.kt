package com.intabia.rabbitex.handler.deserialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler
import com.fasterxml.jackson.databind.deser.ValueInstantiator
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver

/**
 * Обработчик ошибок во время десериализации JSON в класс.
 *
 * NOTE: В случае ошибки при десериализации поля присваивает ему значение или null по умолчанию.
 */
class IgnoringInvalidFieldDeserializationExceptionHandler : DeserializationProblemHandler() {
    override fun handleInstantiationProblem(
        ctxt: DeserializationContext?,
        instClass: Class<*>?,
        argument: Any?,
        t: Throwable?,
    ): Any? {
        return null
    }

    override fun handleMissingInstantiator(
        ctxt: DeserializationContext?,
        instClass: Class<*>?,
        valueInsta: ValueInstantiator?,
        p: JsonParser?,
        msg: String?,
    ): Any? {
        return null
    }

    override fun handleMissingTypeId(
        ctxt: DeserializationContext?,
        baseType: JavaType?,
        idResolver: TypeIdResolver?,
        failureMsg: String?,
    ): JavaType? {
        return null
    }

    override fun handleUnexpectedToken(
        ctxt: DeserializationContext?,
        targetType: JavaType?,
        t: JsonToken?,
        p: JsonParser?,
        failureMsg: String?,
    ): Any? {
        return null
    }

    override fun handleUnknownProperty(
        ctxt: DeserializationContext?,
        p: JsonParser?,
        deserializer: JsonDeserializer<*>?,
        beanOrClass: Any?,
        propertyName: String?,
    ): Boolean {
        return false
    }

    override fun handleUnknownTypeId(
        ctxt: DeserializationContext?,
        baseType: JavaType?,
        subTypeId: String?,
        idResolver: TypeIdResolver?,
        failureMsg: String?,
    ): JavaType? {
        return null
    }

    override fun handleWeirdKey(
        ctxt: DeserializationContext?,
        rawKeyType: Class<*>?,
        keyValue: String?,
        failureMsg: String?,
    ): Any? {
        return null
    }

    override fun handleWeirdNativeValue(
        ctxt: DeserializationContext?,
        targetType: JavaType?,
        valueToConvert: Any?,
        p: JsonParser?,
    ): Any? {
        return null
    }

    override fun handleWeirdNumberValue(
        ctxt: DeserializationContext?,
        targetType: Class<*>?,
        valueToConvert: Number?,
        failureMsg: String?,
    ): Any? {
        return null
    }

    override fun handleWeirdStringValue(
        ctxt: DeserializationContext?,
        targetType: Class<*>?,
        valueToConvert: String?,
        failureMsg: String?,
    ): Any? {
        return null
    }
}
