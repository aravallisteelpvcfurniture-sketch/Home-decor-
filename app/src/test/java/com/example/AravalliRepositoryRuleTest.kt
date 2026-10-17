package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.Customer
import com.example.data.model.Product
import com.example.data.repository.AravalliRepository
import com.example.domain.calculator.FurnitureCalculators
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AravalliRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun authenticatedUser_canSaveAndObserveCustomer() = runTest {
        val uid = signInTestUser("harish_patel@example.com")
        val repository = AravalliRepository(firestore, auth)

        val customer = Customer(
            name = "Ramesh Bhai",
            phone = "+91 98980 11223",
            whatsapp = "+91 98980 11223",
            villageOrCity = "Modasa",
            projectType = "SS Railing",
            budget = 75000.0,
            status = "New Lead",
            notes = "Balcony measurement"
        )

        repository.saveCustomer(customer)
        val customers = repository.observeCustomers().first()
        assertTrue(customers.isNotEmpty())
        assertEquals("Ramesh Bhai", customers.first().name)
    }

    @Test(expected = IllegalStateException::class)
    fun unauthenticatedUser_cannotSaveCustomer() = runTest {
        auth.signOut()
        val repository = AravalliRepository(firestore, auth)

        val customer = Customer(
            name = "Unauthenticated Test",
            phone = "1234567890"
        )
        repository.saveCustomer(customer)
    }

    @Test
    fun calculator_ssRailingComputesCorrectTotals() {
        val result = FurnitureCalculators.calculateSsRailing(
            runningFeet = 40.0,
            ratePerRft = 1500.0,
            isGlassRailing = false,
            fabricationPercent = 10.0,
            installationPerRft = 100.0,
            gstPercent = 18.0
        )
        // Base: 40 * 1500 = 60,000
        // Glass: 0
        // Fabrication: 10% of 60,000 = 6,000
        // Installation: 40 * 100 = 4,000
        // Subtotal: 70,000
        // GST 18%: 12,600
        // Grand Total: 82,600
        assertEquals(40.0, result.runningFeet, 0.01)
        assertEquals(60000.0, result.baseRailingCost, 0.01)
        assertEquals(70000.0, result.subtotal, 0.01)
        assertEquals(12600.0, result.gstAmount, 0.01)
        assertEquals(82600.0, result.grandTotal, 0.01)
    }

    @Test
    fun calculator_modularKitchenComputesCorrectTotals() {
        val result = FurnitureCalculators.calculateModularKitchen(
            runningFeet = 10.0,
            hasWallCabinets = true,
            baseRatePerRft = 2000.0,
            wallRatePerRft = 1500.0,
            basketAccessoriesCost = 15000.0
        )
        // Base: 10 * 2000 = 20,000
        // Wall: 10 * 1500 = 15,000
        // Accessories: 15,000
        // Total: 50,000
        assertEquals(50000.0, result.grandTotal, 0.01)
    }
}
