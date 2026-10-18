package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.Attendance
import com.example.data.model.BusinessSettings
import com.example.data.model.Customer
import com.example.data.model.Estimate
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.Transaction
import com.example.data.model.UserProfile
import com.example.data.model.Worker
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AravalliRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    // Secondary constructor resolving firestore_database_id dynamically
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- User Profile ---
    fun observeUserProfile(): Flow<UserProfile?> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val docRef = db.collection("users").document(uid)
        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("AravalliRepo", "Error observing profile: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(UserProfile::class.java))
            } else {
                trySend(null)
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val payload = mapOf(
            "userId" to uid,
            "name" to profile.name,
            "email" to profile.email,
            "phone" to profile.phone,
            "role" to profile.role,
            "businessName" to profile.businessName,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // --- Customers ---
    fun observeCustomers(): Flow<List<Customer>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val col = db.collection("users").document(uid).collection("customers")
        val registration = col.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("AravalliRepo", "Error observing customers: ${error.message}")
                return@addSnapshotListener
            }
            val list = snapshot?.toObjects(Customer::class.java) ?: emptyList()
            trySend(list)
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveCustomer(customer: Customer) {
        val uid = requireUserId()
        val col = db.collection("users").document(uid).collection("customers")
        val docRef = if (customer.id.isNotBlank()) col.document(customer.id) else col.document()
        val payload = mapOf(
            "userId" to uid,
            "name" to customer.name,
            "phone" to customer.phone,
            "whatsapp" to customer.whatsapp,
            "address" to customer.address,
            "villageOrCity" to customer.villageOrCity,
            "projectType" to customer.projectType,
            "budget" to customer.budget,
            "status" to customer.status,
            "notes" to customer.notes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    suspend fun deleteCustomer(customerId: String) {
        val uid = requireUserId()
        db.collection("users").document(uid).collection("customers").document(customerId).delete().await()
    }

    // --- Products ---
    fun observeProducts(): Flow<List<Product>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val col = db.collection("users").document(uid).collection("products")
        val registration = col.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("AravalliRepo", "Error observing products: ${error.message}")
                return@addSnapshotListener
            }
            val list = snapshot?.toObjects(Product::class.java) ?: emptyList()
            trySend(list)
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveProduct(product: Product) {
        val uid = requireUserId()
        val col = db.collection("users").document(uid).collection("products")
        val docRef = if (product.id.isNotBlank()) col.document(product.id) else col.document()
        val payload = mapOf(
            "userId" to uid,
            "name" to product.name,
            "sku" to product.sku,
            "category" to product.category,
            "description" to product.description,
            "material" to product.material,
            "dimensions" to product.dimensions,
            "unit" to product.unit,
            "sellingPrice" to product.sellingPrice,
            "costPrice" to product.costPrice,
            "stockQuantity" to product.stockQuantity,
            "imageUrl" to product.imageUrl,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    suspend fun deleteProduct(productId: String) {
        val uid = requireUserId()
        db.collection("users").document(uid).collection("products").document(productId).delete().await()
    }

    // --- Estimates ---
    fun observeEstimates(): Flow<List<Estimate>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val col = db.collection("users").document(uid).collection("estimates")
        val registration = col.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("AravalliRepo", "Error observing estimates: ${error.message}")
                return@addSnapshotListener
            }
            val list = snapshot?.toObjects(Estimate::class.java) ?: emptyList()
            trySend(list)
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveEstimate(estimate: Estimate): String {
        val uid = requireUserId()
        val col = db.collection("users").document(uid).collection("estimates")
        val docRef = if (estimate.id.isNotBlank()) col.document(estimate.id) else col.document()
        val payload = mapOf(
            "userId" to uid,
            "estimateNumber" to estimate.estimateNumber,
            "customerId" to estimate.customerId,
            "customerName" to estimate.customerName,
            "customerPhone" to estimate.customerPhone,
            "projectType" to estimate.projectType,
            "itemsJson" to estimate.itemsJson,
            "subtotal" to estimate.subtotal,
            "gstPercent" to estimate.gstPercent,
            "gstAmount" to estimate.gstAmount,
            "discount" to estimate.discount,
            "finalTotal" to estimate.finalTotal,
            "advance" to estimate.advance,
            "balance" to estimate.balance,
            "notes" to estimate.notes,
            "status" to estimate.status,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
        return docRef.id
    }

    suspend fun convertEstimateToOrder(estimate: Estimate): String {
        val uid = requireUserId()
        val orderCol = db.collection("users").document(uid).collection("orders")
        val orderDoc = orderCol.document()
        val orderNumber = "ORD-" + System.currentTimeMillis().toString().takeLast(6)
        val payload = mapOf(
            "userId" to uid,
            "orderNumber" to orderNumber,
            "customerId" to estimate.customerId,
            "customerName" to estimate.customerName,
            "customerPhone" to estimate.customerPhone,
            "projectType" to estimate.projectType,
            "estimateId" to estimate.id,
            "totalAmount" to estimate.finalTotal,
            "advancePaid" to estimate.advance,
            "balanceAmount" to (estimate.finalTotal - estimate.advance).coerceAtLeast(0.0),
            "deliveryDate" to "15 Days",
            "assignedWorker" to "Carpenter Team Modasa",
            "status" to "Confirmed",
            "notes" to "Converted from Quote #${estimate.estimateNumber}",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        orderDoc.set(payload).await()

        // Also record advance payment transaction if advance > 0
        if (estimate.advance > 0) {
            saveTransaction(
                Transaction(
                    type = "Income",
                    category = "Customer Advance",
                    amount = estimate.advance,
                    paymentMode = "UPI",
                    reference = orderNumber,
                    date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                    notes = "Advance for order $orderNumber from ${estimate.customerName}"
                )
            )
        }

        // Update estimate status to Converted
        if (estimate.id.isNotBlank()) {
            db.collection("users").document(uid).collection("estimates").document(estimate.id)
                .update("status", "Converted", "updatedAt", FieldValue.serverTimestamp()).await()
        }

        return orderDoc.id
    }

    // --- Orders ---
    fun observeOrders(): Flow<List<Order>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val col = db.collection("users").document(uid).collection("orders")
        val registration = col.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("AravalliRepo", "Error observing orders: ${error.message}")
                return@addSnapshotListener
            }
            val list = snapshot?.toObjects(Order::class.java) ?: emptyList()
            trySend(list)
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveOrder(order: Order) {
        val uid = requireUserId()
        val col = db.collection("users").document(uid).collection("orders")
        val docRef = if (order.id.isNotBlank()) col.document(order.id) else col.document()
        val payload = mapOf(
            "userId" to uid,
            "orderNumber" to order.orderNumber,
            "customerId" to order.customerId,
            "customerName" to order.customerName,
            "customerPhone" to order.customerPhone,
            "projectType" to order.projectType,
            "estimateId" to order.estimateId,
            "totalAmount" to order.totalAmount,
            "advancePaid" to order.advancePaid,
            "balanceAmount" to order.balanceAmount,
            "deliveryDate" to order.deliveryDate,
            "assignedWorker" to order.assignedWorker,
            "status" to order.status,
            "notes" to order.notes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String) {
        val uid = requireUserId()
        db.collection("users").document(uid).collection("orders").document(orderId)
            .update("status", newStatus, "updatedAt", FieldValue.serverTimestamp()).await()
    }

    // --- Workers ---
    fun observeWorkers(): Flow<List<Worker>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val col = db.collection("users").document(uid).collection("workers")
        val registration = col.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("AravalliRepo", "Error observing workers: ${error.message}")
                return@addSnapshotListener
            }
            val list = snapshot?.toObjects(Worker::class.java) ?: emptyList()
            trySend(list)
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveWorker(worker: Worker) {
        val uid = requireUserId()
        val col = db.collection("users").document(uid).collection("workers")
        val docRef = if (worker.id.isNotBlank()) col.document(worker.id) else col.document()
        val payload = mapOf(
            "userId" to uid,
            "name" to worker.name,
            "phone" to worker.phone,
            "role" to worker.role,
            "dailyWage" to worker.dailyWage,
            "status" to worker.status,
            "notes" to worker.notes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    suspend fun deleteWorker(workerId: String) {
        val uid = requireUserId()
        db.collection("users").document(uid).collection("workers").document(workerId).delete().await()
    }

    suspend fun deleteOrder(orderId: String) {
        val uid = requireUserId()
        db.collection("users").document(uid).collection("orders").document(orderId).delete().await()
    }

    suspend fun updateProductPricing(productId: String, sellingPrice: Double, costPrice: Double, stock: Double) {
        val uid = requireUserId()
        db.collection("users").document(uid).collection("products").document(productId).update(
            mapOf(
                "sellingPrice" to sellingPrice,
                "costPrice" to costPrice,
                "stockQuantity" to stock,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    // --- Attendance ---
    fun observeAttendance(): Flow<List<Attendance>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val col = db.collection("users").document(uid).collection("attendance")
        val registration = col.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("AravalliRepo", "Error observing attendance: ${error.message}")
                return@addSnapshotListener
            }
            val list = snapshot?.toObjects(Attendance::class.java) ?: emptyList()
            trySend(list)
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveAttendance(record: Attendance) {
        val uid = requireUserId()
        val col = db.collection("users").document(uid).collection("attendance")
        val docRef = if (record.id.isNotBlank()) col.document(record.id) else col.document()
        val payload = mapOf(
            "userId" to uid,
            "workerId" to record.workerId,
            "workerName" to record.workerName,
            "date" to record.date,
            "status" to record.status,
            "overtimeHours" to record.overtimeHours,
            "advanceGiven" to record.advanceGiven,
            "dailyEarned" to record.dailyEarned,
            "notes" to record.notes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // --- Transactions / Cashbook ---
    fun observeTransactions(): Flow<List<Transaction>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val col = db.collection("users").document(uid).collection("transactions")
        val registration = col.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("AravalliRepo", "Error observing transactions: ${error.message}")
                return@addSnapshotListener
            }
            val list = snapshot?.toObjects(Transaction::class.java) ?: emptyList()
            trySend(list)
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveTransaction(tx: Transaction) {
        val uid = requireUserId()
        val col = db.collection("users").document(uid).collection("transactions")
        val docRef = if (tx.id.isNotBlank()) col.document(tx.id) else col.document()
        val payload = mapOf(
            "userId" to uid,
            "type" to tx.type,
            "category" to tx.category,
            "amount" to tx.amount,
            "paymentMode" to tx.paymentMode,
            "reference" to tx.reference,
            "date" to tx.date,
            "notes" to tx.notes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // --- Settings ---
    fun observeSettings(): Flow<BusinessSettings?> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val col = db.collection("users").document(uid).collection("settings").document("profile")
        val registration = col.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("AravalliRepo", "Error observing settings: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(BusinessSettings::class.java))
            } else {
                trySend(null)
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun saveSettings(settings: BusinessSettings) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("settings").document("profile")
        val payload = mapOf(
            "userId" to uid,
            "businessName" to settings.businessName,
            "phone" to settings.phone,
            "whatsapp" to settings.whatsapp,
            "email" to settings.email,
            "address" to settings.address,
            "gstin" to settings.gstin,
            "defaultGstRate" to settings.defaultGstRate,
            "estimateTerms" to settings.estimateTerms,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    // Seed comprehensive authentic initial products & workers for Modasa workshop
    suspend fun seedDemoDataIfEmpty() {
        val uid = requireUserId()
        val productsCol = db.collection("users").document(uid).collection("products")
        val existingProducts = productsCol.limit(1).get().await()
        if (existingProducts.isEmpty) {
            val initialProducts = listOf(
                Product(
                    name = "SS 304 Glass Spigot Railing",
                    sku = "ASPF-SS-001",
                    category = "Steel Railing",
                    description = "Grade 304 stainless steel with heavy duty bottom spigots, 12mm toughened glass, slotted top handrail. Ideal for balconies and luxury staircases.",
                    material = "SS 304 & 12mm Glass",
                    dimensions = "36 to 42 inch height",
                    unit = "Rft",
                    sellingPrice = 1850.0,
                    costPrice = 1400.0,
                    stockQuantity = 250.0
                ),
                Product(
                    name = "SS 202 Pipe Baluster Staircase Railing",
                    sku = "ASPF-SS-002",
                    category = "Steel Railing",
                    description = "Durable 50mm round top pipe with 3 horizontal safety pipes and designer master pillars.",
                    material = "SS 202 Polished",
                    dimensions = "36 inch height",
                    unit = "Rft",
                    sellingPrice = 850.0,
                    costPrice = 620.0,
                    stockQuantity = 400.0
                ),
                Product(
                    name = "Waterproof PVC Modular Kitchen (L-Shape)",
                    sku = "ASPF-KIT-001",
                    category = "Modular Kitchen",
                    description = "100% termite-proof & waterproof 18mm PVC foam carcass, soft-close hydraulic hinges, acrylic high-gloss shutters, SS 304 wire baskets.",
                    material = "18mm Waterproof PVC",
                    dimensions = "Standard 8x10 ft custom fit",
                    unit = "Rft",
                    sellingPrice = 2400.0,
                    costPrice = 1800.0,
                    stockQuantity = 50.0
                ),
                Product(
                    name = "3-Door Sliding PVC Wardrobe",
                    sku = "ASPF-WDR-001",
                    category = "Wardrobe",
                    description = "Modern sliding door wardrobe with internal drawers, locker, hanger rod and mirror shutter finish.",
                    material = "PVC Board + Aluminium Profile",
                    dimensions = "7 ft H x 6 ft W x 2 ft D",
                    unit = "Sq Ft",
                    sellingPrice = 1100.0,
                    costPrice = 820.0,
                    stockQuantity = 30.0
                ),
                Product(
                    name = "Aluminium Toughened Glass Balcony",
                    sku = "ASPF-BAL-001",
                    category = "Aluminium Glass",
                    description = "Heavy anodized aluminium profile with 10mm toughened safety glass, spider fittings and silicone weather seal.",
                    material = "Aluminium Domal + Glass",
                    dimensions = "Customized per site",
                    unit = "Sq Ft",
                    sellingPrice = 450.0,
                    costPrice = 330.0,
                    stockQuantity = 600.0
                ),
                Product(
                    name = "UPVC Sliding Window 3-Track with Mesh",
                    sku = "ASPF-WIN-001",
                    category = "UPVC Doors Windows",
                    description = "Multi-chamber UPVC window with sound & heat insulation, SS mosquito mesh, multi-point lock.",
                    material = "Reinforced UPVC Profile",
                    dimensions = "5 ft W x 4 ft H",
                    unit = "Sq Ft",
                    sellingPrice = 380.0,
                    costPrice = 270.0,
                    stockQuantity = 120.0
                ),
                Product(
                    name = "Floating TV Unit with Louver Wall Panelling",
                    sku = "ASPF-TV-001",
                    category = "TV Unit",
                    description = "Charcoal PVC louver backdrop with LED profile back-lighting and floating console cabinet for setup box.",
                    material = "PVC Charcoal Panel + MDF",
                    dimensions = "8 ft W x 7 ft H",
                    unit = "Set",
                    sellingPrice = 18500.0,
                    costPrice = 12800.0,
                    stockQuantity = 10.0
                )
            )

            for (p in initialProducts) {
                saveProduct(p)
            }
        }

        val workersCol = db.collection("users").document(uid).collection("workers")
        val existingWorkers = workersCol.limit(1).get().await()
        if (existingWorkers.isEmpty) {
            val initialWorkers = listOf(
                Worker(name = "Kishore Bhai Mistri", phone = "+91 98790 12301", role = "Master Carpenter", dailyWage = 950.0),
                Worker(name = "Prakash Bhai Patel", phone = "+91 98790 12302", role = "SS Railing Fabricator", dailyWage = 900.0),
                Worker(name = "Dinesh Kumar", phone = "+91 98790 12303", role = "Glass & Aluminium Installer", dailyWage = 800.0),
                Worker(name = "Mahesh Helper", phone = "+91 98790 12304", role = "Helper", dailyWage = 550.0)
            )
            for (w in initialWorkers) {
                saveWorker(w)
            }
        }

        val customersCol = db.collection("users").document(uid).collection("customers")
        val existingCust = customersCol.limit(1).get().await()
        if (existingCust.isEmpty) {
            val initialCustomers = listOf(
                Customer(
                    name = "Dr. Jignesh Shah",
                    phone = "+91 94280 55441",
                    whatsapp = "+91 94280 55441",
                    address = "Bungalow No. 12, Malpur Road",
                    villageOrCity = "Modasa",
                    projectType = "SS Railing & Modular Kitchen",
                    budget = 180000.0,
                    status = "Estimate Sent",
                    notes = "Balcony glass railing 45 rft + L-shape kitchen required before Diwali."
                ),
                Customer(
                    name = "Bhavin Bhai Chaudhari",
                    phone = "+91 98981 77662",
                    whatsapp = "+91 98981 77662",
                    address = "Near Sardar Patel Hospital",
                    villageOrCity = "Dhansura",
                    projectType = "PVC Wardrobe & TV Unit",
                    budget = 125000.0,
                    status = "Site Visit",
                    notes = "Site visit done on Meghraj Road office. Measurements recorded."
                )
            )
            for (c in initialCustomers) {
                saveCustomer(c)
            }
        }

        val settingsDoc = db.collection("users").document(uid).collection("settings").document("profile").get().await()
        if (!settingsDoc.exists()) {
            saveSettings(BusinessSettings(userId = uid))
        }
    }

    suspend fun reseedDemoData() {
        val uid = requireUserId()
        val initialProducts = listOf(
            Product(
                name = "SS 304 Glass Spigot Railing",
                sku = "ASPF-SS-001",
                category = "Steel Railing",
                description = "Grade 304 stainless steel with heavy duty bottom spigots, 12mm toughened glass, slotted top handrail. Ideal for balconies and luxury staircases.",
                material = "SS 304 & 12mm Glass",
                dimensions = "36 to 42 inch height",
                unit = "Rft",
                sellingPrice = 1850.0,
                costPrice = 1400.0,
                stockQuantity = 250.0
            ),
            Product(
                name = "SS 202 Pipe Baluster Staircase Railing",
                sku = "ASPF-SS-002",
                category = "Steel Railing",
                description = "Durable 50mm round top pipe with 3 horizontal safety pipes and designer master pillars.",
                material = "SS 202 Polished",
                dimensions = "36 inch height",
                unit = "Rft",
                sellingPrice = 850.0,
                costPrice = 620.0,
                stockQuantity = 400.0
            ),
            Product(
                name = "Waterproof PVC Modular Kitchen (L-Shape)",
                sku = "ASPF-KIT-001",
                category = "Modular Kitchen",
                description = "100% termite-proof & waterproof 18mm PVC foam carcass, soft-close hydraulic hinges, acrylic high-gloss shutters, SS 304 wire baskets.",
                material = "18mm Waterproof PVC",
                dimensions = "Standard 8x10 ft custom fit",
                unit = "Rft",
                sellingPrice = 2400.0,
                costPrice = 1800.0,
                stockQuantity = 50.0
            ),
            Product(
                name = "3-Door Sliding PVC Wardrobe",
                sku = "ASPF-WDR-001",
                category = "Wardrobe",
                description = "Modern sliding door wardrobe with internal drawers, locker, hanger rod and mirror shutter finish.",
                material = "PVC Board + Aluminium Profile",
                dimensions = "7 ft H x 6 ft W x 2 ft D",
                unit = "Sq Ft",
                sellingPrice = 1100.0,
                costPrice = 820.0,
                stockQuantity = 30.0
            ),
            Product(
                name = "Aluminium Toughened Glass Balcony",
                sku = "ASPF-BAL-001",
                category = "Aluminium Glass",
                description = "Heavy anodized aluminium profile with 10mm toughened safety glass, spider fittings and silicone weather seal.",
                material = "Aluminium Domal + Glass",
                dimensions = "Customized per site",
                unit = "Sq Ft",
                sellingPrice = 450.0,
                costPrice = 330.0,
                stockQuantity = 600.0
            ),
            Product(
                name = "UPVC Sliding Window 3-Track with Mesh",
                sku = "ASPF-WIN-001",
                category = "UPVC Doors Windows",
                description = "Multi-chamber UPVC window with sound & heat insulation, SS mosquito mesh, multi-point lock.",
                material = "Reinforced UPVC Profile",
                dimensions = "5 ft W x 4 ft H",
                unit = "Sq Ft",
                sellingPrice = 380.0,
                costPrice = 270.0,
                stockQuantity = 120.0
            ),
            Product(
                name = "Floating TV Unit with Louver Wall Panelling",
                sku = "ASPF-TV-001",
                category = "TV Unit",
                description = "Charcoal PVC louver backdrop with LED profile back-lighting and floating console cabinet for setup box.",
                material = "PVC Charcoal Panel + MDF",
                dimensions = "8 ft W x 7 ft H",
                unit = "Set",
                sellingPrice = 18500.0,
                costPrice = 12800.0,
                stockQuantity = 10.0
            )
        )
        for (p in initialProducts) {
            saveProduct(p)
        }

        val initialWorkers = listOf(
            Worker(name = "Kishore Bhai Mistri", phone = "+91 98790 12301", role = "Master Carpenter", dailyWage = 950.0),
            Worker(name = "Prakash Bhai Patel", phone = "+91 98790 12302", role = "SS Railing Fabricator", dailyWage = 900.0),
            Worker(name = "Dinesh Kumar", phone = "+91 98790 12303", role = "Glass & Aluminium Installer", dailyWage = 800.0),
            Worker(name = "Mahesh Helper", phone = "+91 98790 12304", role = "Helper", dailyWage = 550.0)
        )
        for (w in initialWorkers) {
            saveWorker(w)
        }

        val initialCustomers = listOf(
            Customer(
                name = "Dr. Jignesh Shah",
                phone = "+91 94280 55441",
                whatsapp = "+91 94280 55441",
                address = "Bungalow No. 12, Malpur Road",
                villageOrCity = "Modasa",
                projectType = "SS Railing & Modular Kitchen",
                budget = 180000.0,
                status = "Estimate Sent",
                notes = "Balcony glass railing 45 rft + L-shape kitchen required before Diwali."
            ),
            Customer(
                name = "Bhavin Bhai Chaudhari",
                phone = "+91 98981 77662",
                whatsapp = "+91 98981 77662",
                address = "Near Sardar Patel Hospital",
                villageOrCity = "Dhansura",
                projectType = "PVC Wardrobe & TV Unit",
                budget = 125000.0,
                status = "Site Visit",
                notes = "Site visit done on Meghraj Road office. Measurements recorded."
            )
        )
        for (c in initialCustomers) {
            saveCustomer(c)
        }

        saveSettings(BusinessSettings(userId = uid))
    }
}
