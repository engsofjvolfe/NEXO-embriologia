package org.nexo.motor.core.connectivity

import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class NordicUartServiceTest {

    @Test
    fun `SERVICE_UUID bate com o valor fixado em PD-CON-02`() {
        assertEquals(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e"), NordicUartService.SERVICE_UUID)
    }

    @Test
    fun `RX_CHARACTERISTIC_UUID bate com o valor fixado em PD-CON-02`() {
        assertEquals(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"), NordicUartService.RX_CHARACTERISTIC_UUID)
    }

    @Test
    fun `TX_CHARACTERISTIC_UUID bate com o valor fixado em PD-CON-02`() {
        assertEquals(UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e"), NordicUartService.TX_CHARACTERISTIC_UUID)
    }

    @Test
    fun `os tres UUIDs sao distintos entre si`() {
        val uuids = setOf(
            NordicUartService.SERVICE_UUID,
            NordicUartService.RX_CHARACTERISTIC_UUID,
            NordicUartService.TX_CHARACTERISTIC_UUID,
        )
        assertEquals(3, uuids.size)
    }
}
