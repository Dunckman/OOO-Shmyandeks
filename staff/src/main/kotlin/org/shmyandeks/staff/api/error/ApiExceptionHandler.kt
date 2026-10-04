package org.shmyandeks.staff.api.error

import org.shmyandeks.staff.api.model.ApiError
import org.shmyandeks.staff.api.model.UpdateItemRequest
import org.shmyandeks.staff.service.FeatureNotImplementedException
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import tools.jackson.core.JacksonException
import tools.jackson.databind.exc.UnrecognizedPropertyException

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception): ResponseEntity<ApiError> {
        val jsonError = (ex as? HttpMessageNotReadableException)?.cause as? JacksonException
        val immutableNumber = jsonError is UnrecognizedPropertyException &&
            jsonError.referringClass == UpdateItemRequest::class.java && jsonError.propertyName == "inventoryNumber"

        val status = when {
            immutableNumber -> 409
            ex is FeatureNotImplementedException -> 501
            ex is AuthenticationException -> 401
            ex is AccessDeniedException -> 403
            ex is HttpMessageNotReadableException || ex is MethodArgumentTypeMismatchException -> 400
            ex is ErrorResponse -> ex.statusCode.value()
            else -> 500
        }
        val fields = when (ex) {
            is HttpMessageNotReadableException -> jsonError?.path?.mapNotNull { it.propertyName }
                .orEmpty().ifEmpty { listOf("body") }
            is MethodArgumentNotValidException -> ex.bindingResult.fieldErrors.map { it.field }
            is HandlerMethodValidationException -> ex.parameterValidationResults.mapNotNull {
                it.methodParameter.parameterName
            }
            is MethodArgumentTypeMismatchException -> listOf(ex.name)
            else -> emptyList()
        }
        val body = if (immutableNumber) {
            ApiError("IMMUTABLE_INVENTORY_NUMBER", "Инвентарный номер нельзя изменять", listOf("inventoryNumber"))
        } else {
            ApiErrors.body(status, fields)
        }
        val headers = if (ex is ErrorResponse) ex.headers else HttpHeaders.EMPTY
        return ResponseEntity.status(status).headers(headers).contentType(MediaType.APPLICATION_JSON).body(body)
    }
}
