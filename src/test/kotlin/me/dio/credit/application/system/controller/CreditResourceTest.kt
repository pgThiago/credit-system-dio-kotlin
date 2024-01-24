package me.dio.credit.application.system.controller
import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.request.CreditDto
import me.dio.credit.application.system.dto.request.CreditUpdateDto
import me.dio.credit.application.system.dto.request.CustomerDto
import me.dio.credit.application.system.repository.CreditRepository
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {
    @Autowired
    private lateinit var creditRepository: CreditRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
        const val CUSTOMERURL: String = "/api/customers"
    }

    fun createCustomer() {
        val customerDto: CustomerDto = builderCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        println("============================================")
        println(valueAsString)
        mockMvc.perform(
                MockMvcRequestBuilders.post(CUSTOMERURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valueAsString)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Cami"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Cavalcante"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("camila@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("000000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua da Cami, 123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andReturn()

        // Extrair o CustomerDto da resposta para usá-lo em outros testes se necessário
//        return objectMapper.readValue(result.response.contentAsString, CustomerDto::class.java)
    }

    @BeforeEach
    fun setup() {
        creditRepository.deleteAll()
        createCustomer()
    }

    @AfterEach
    fun tearDown() = creditRepository.deleteAll()

    @Test
    fun `should create a credit and return 201 status`() {
        //given
        val creditDto: CreditDto = builderCreditDto()
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valueAsString)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(100.2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallment").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value("camila@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(1000))
                .andDo(MockMvcResultHandlers.print())
    }


    private fun builderCreditDto(
            creditValue: BigDecimal = BigDecimal.valueOf(100.2),
            dayFirstOfInstallment: LocalDate = LocalDate.parse("2024-01-25"),
            numberOfInstallments: Int = 1,
            customerId: Long = 1
    ) = CreditDto(
            creditValue = creditValue,
            dayFirstOfInstallment = dayFirstOfInstallment,
            numberOfInstallments = numberOfInstallments,
            customerId = customerId
    )

    private fun builderCreditUpdateDto(
            creditValue: BigDecimal = BigDecimal.valueOf(200)
    ): CreditUpdateDto = CreditUpdateDto (
        creditValue = creditValue
    )

    private fun builderCustomerDto(
            firstName: String = "Cami",
            lastName: String = "Cavalcante",
            cpf: String = "28475934625",
            email: String = "camila@email.com",
            income: BigDecimal = BigDecimal.valueOf(1000.0),
            password: String = "1234",
            zipCode: String = "000000",
            street: String = "Rua da Cami, 123",
    ) = CustomerDto(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            income = income,
            password = password,
            zipCode = zipCode,
            street = street
    )
}