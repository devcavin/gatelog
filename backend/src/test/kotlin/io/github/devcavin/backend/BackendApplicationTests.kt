package io.github.devcavin.backend

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BackendApplicationTests {
    private val logger = LoggerFactory.getLogger(BackendApplicationTests::class.java)

    @Test
    fun contextLoads() {
        logger.info("Application context loaded successfully")
    }

}
