package org.shmyandeks.staff.api.error

import org.shmyandeks.staff.api.model.ApiError

object ApiErrors {
    fun body(status: Int, fields: List<String> = emptyList()): ApiError {
        val (code, message) = when (status) {
            400 -> "INVALID_REQUEST" to "Некорректный запрос"
            401 -> "UNAUTHORIZED" to "Требуется вход"
            403 -> "FORBIDDEN" to "Доступ запрещен"
            404 -> "NOT_FOUND" to "Объект не найден"
            405 -> "METHOD_NOT_ALLOWED" to "Метод не поддерживается"
            406 -> "NOT_ACCEPTABLE" to "Запрошенный формат ответа не поддерживается"
            409 -> "CONFLICT" to "Операция конфликтует с текущим состоянием"
            415 -> "UNSUPPORTED_MEDIA_TYPE" to "Неподдерживаемый тип содержимого"
            501 -> "NOT_IMPLEMENTED" to "Операция пока не реализована"
            else -> "INTERNAL_ERROR" to "Внутренняя ошибка сервера"
        }
        return ApiError(code, message, fields.distinct().sorted().ifEmpty { null })
    }
}
