package me.dio.credit.application.system.dto.request

import jakarta.validation.constraints.NotNull
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import java.math.BigDecimal

data class CreditUpdateDto (@field:NotNull(message = "Invalid input") val creditValue: BigDecimal ) {
//    fun toEntity(credit: Credit): Credit {
//        credit.creditValue = this.creditValue
//        return credit
//    }

    fun toEntity(credit: Credit): Credit {
        credit.creditValue = this.creditValue
        return credit
    }
}