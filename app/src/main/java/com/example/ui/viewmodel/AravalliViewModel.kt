package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Attendance
import com.example.data.model.BusinessSettings
import com.example.data.model.Customer
import com.example.data.model.Estimate
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.Transaction
import com.example.data.model.UserProfile
import com.example.data.model.Worker
import com.example.data.repository.AravalliRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AravalliViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AravalliRepository(application)

    val userProfile: StateFlow<UserProfile?> = repository.observeUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val customers: StateFlow<List<Customer>> = repository.observeCustomers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<Product>> = repository.observeProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val estimates: StateFlow<List<Estimate>> = repository.observeEstimates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<Order>> = repository.observeOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workers: StateFlow<List<Worker>> = repository.observeWorkers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendance: StateFlow<List<Attendance>> = repository.observeAttendance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<Transaction>> = repository.observeTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<BusinessSettings?> = repository.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Language state: "hi" (Hindi - default), "en" (English), "gu" (Gujarati)
    private val _currentLanguage = MutableStateFlow("hi")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.seedDemoDataIfEmpty()
            } catch (e: Exception) {
                // Ignore if unauthenticated or network unavailable
            }
        }
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun showMessage(msg: String) {
        _statusMessage.value = msg
    }

    // Customer operations
    fun addOrUpdateCustomer(customer: Customer, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.saveCustomer(customer)
                _statusMessage.value = "ग्राहक विवरण सुरक्षित किया गया (Saved Customer)"
                onDone()
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    fun removeCustomer(customerId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCustomer(customerId)
                _statusMessage.value = "ग्राहक हटाया गया (Deleted)"
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    // Product operations
    fun addOrUpdateProduct(product: Product, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.saveProduct(product)
                _statusMessage.value = "उत्पाद सुरक्षित किया गया (Product Saved)"
                onDone()
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    fun removeProduct(productId: String) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(productId)
                _statusMessage.value = "उत्पाद हटाया गया (Deleted)"
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    // Estimate operations
    fun saveEstimate(estimate: Estimate, onDone: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val id = repository.saveEstimate(estimate)
                _statusMessage.value = "कोटेशन #${estimate.estimateNumber} सुरक्षित हुआ"
                onDone(id)
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    fun convertEstimateToOrder(estimate: Estimate, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.convertEstimateToOrder(estimate)
                _statusMessage.value = "कोटेशन सफलतापूर्वक ऑर्डर में बदल गया!"
                onDone()
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    // Order operations
    fun addOrUpdateOrder(order: Order, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.saveOrder(order)
                _statusMessage.value = "ऑर्डर सुरक्षित किया गया (Order Saved)"
                onDone()
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    fun updateOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            try {
                repository.updateOrderStatus(orderId, status)
                _statusMessage.value = "ऑर्डर स्थिति अपडेट की गई: $status"
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            try {
                repository.deleteOrder(orderId)
                _statusMessage.value = "ऑर्डर हटाया गया (Order Deleted)"
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    // Worker operations
    fun addOrUpdateWorker(worker: Worker, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.saveWorker(worker)
                _statusMessage.value = "कारीगर प्रोफ़ाइल सुरक्षित की गई"
                onDone()
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    fun deleteWorker(workerId: String) {
        viewModelScope.launch {
            try {
                repository.deleteWorker(workerId)
                _statusMessage.value = "कारीगर हटाया गया (Worker Removed)"
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    fun updateProductPricing(productId: String, sellingPrice: Double, costPrice: Double, stock: Double) {
        viewModelScope.launch {
            try {
                repository.updateProductPricing(productId, sellingPrice, costPrice, stock)
                _statusMessage.value = "उत्पाद मूल्य व स्टॉक अपडेट हुआ"
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    fun reseedDemoData(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.reseedDemoData()
                _statusMessage.value = "डेमो डेटा सफलतापूर्वक री-लोड हो गया!"
                onDone()
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    fun recordAttendance(record: Attendance, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.saveAttendance(record)
                _statusMessage.value = "हाजिरी दर्ज की गई (${record.workerName}: ${record.status})"
                onDone()
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    // Finance / Transaction operations
    fun addTransaction(tx: Transaction, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.saveTransaction(tx)
                _statusMessage.value = "खाता प्रविष्टि जोड़ी गई (₹${tx.amount})"
                onDone()
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    // Settings
    fun saveSettings(businessSettings: BusinessSettings, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.saveSettings(businessSettings)
                _statusMessage.value = "व्यापार सेटिंग्स अपडेट हो गई"
                onDone()
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    fun updateBusinessGst(rate: Double) {
        viewModelScope.launch {
            try {
                val current = settings.value ?: BusinessSettings()
                repository.saveSettings(current.copy(defaultGstRate = rate))
                _statusMessage.value = "जीएसटी दर अपडेट: ${rate.toInt()}%"
            } catch (e: Exception) {
                _statusMessage.value = "त्रुटि: ${e.message}"
            }
        }
    }

    // Financial KPI Helper calculations
    fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getTodaySales(): Double {
        val today = getTodayDate()
        return transactions.value
            .filter { it.type == "Income" && it.date == today }
            .sumOf { it.amount }
    }

    fun getMonthlySales(): Double {
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        return transactions.value
            .filter { it.type == "Income" && it.date.startsWith(currentMonth) }
            .sumOf { it.amount }
    }

    fun getTotalExpenses(): Double {
        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        return transactions.value
            .filter { it.type == "Expense" && it.date.startsWith(currentMonth) }
            .sumOf { it.amount }
    }

    fun getOutstandingReceivables(): Double {
        return orders.value
            .filter { it.status != "Completed" && it.status != "Cancelled" }
            .sumOf { it.balanceAmount }
    }
}
