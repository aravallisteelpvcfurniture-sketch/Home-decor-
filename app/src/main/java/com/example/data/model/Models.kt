package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "admin", // admin, staff, carpenter, customer
    val businessName: String = "ARAVALLI STEEL PVC FURNITURE",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Customer(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val whatsapp: String = "",
    val address: String = "",
    val villageOrCity: String = "",
    val projectType: String = "SS Railing", // SS Railing, PVC Kitchen, Wardrobe, Balcony, etc.
    val budget: Double = 0.0,
    val status: String = "New Lead", // New Lead, Site Visit, Estimate Sent, Negotiation, Confirmed, Work Started, Completed, Lost
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Product(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val sku: String = "",
    val category: String = "Steel Railing", // Steel Railing, Steel Furniture, PVC Furniture, UPVC Doors Windows, Aluminium Glass, Wardrobe, Modular Kitchen, TV Unit, Interior Design
    val description: String = "",
    val material: String = "",
    val dimensions: String = "",
    val unit: String = "Rft", // Rft, Sq Ft, Pcs, Set, Kg
    val sellingPrice: Double = 0.0,
    val costPrice: Double = 0.0,
    val stockQuantity: Double = 0.0,
    val imageUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Estimate(
    @DocumentId val id: String = "",
    val userId: String = "",
    val estimateNumber: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val projectType: String = "",
    val itemsJson: String = "", // serialized item summaries
    val subtotal: Double = 0.0,
    val gstPercent: Double = 18.0,
    val gstAmount: Double = 0.0,
    val discount: Double = 0.0,
    val finalTotal: Double = 0.0,
    val advance: Double = 0.0,
    val balance: Double = 0.0,
    val notes: String = "",
    val status: String = "Draft", // Draft, Sent, Accepted, Converted, Rejected
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Order(
    @DocumentId val id: String = "",
    val userId: String = "",
    val orderNumber: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val projectType: String = "",
    val estimateId: String = "",
    val totalAmount: Double = 0.0,
    val advancePaid: Double = 0.0,
    val balanceAmount: Double = 0.0,
    val deliveryDate: String = "",
    val assignedWorker: String = "",
    val status: String = "Confirmed", // Confirmed, Material Purchased, Manufacturing, Installation, Completed, Cancelled
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Worker(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val role: String = "Carpenter", // Carpenter, Fabricator, Installer, Helper
    val dailyWage: Double = 800.0,
    val status: String = "Active",
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Attendance(
    @DocumentId val id: String = "",
    val userId: String = "",
    val workerId: String = "",
    val workerName: String = "",
    val date: String = "", // YYYY-MM-DD
    val status: String = "Present", // Present, Half Day, Absent, Overtime, Leave
    val overtimeHours: Double = 0.0,
    val advanceGiven: Double = 0.0,
    val dailyEarned: Double = 0.0,
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Transaction(
    @DocumentId val id: String = "",
    val userId: String = "",
    val type: String = "Income", // Income, Expense
    val category: String = "Customer Advance", // Customer Advance, Customer Balance, Material Purchase, Worker Wage, Worker Advance, Rent, Electricity, Transport, Other
    val amount: Double = 0.0,
    val paymentMode: String = "Cash", // Cash, UPI, Bank Transfer
    val reference: String = "",
    val date: String = "", // YYYY-MM-DD
    val notes: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class BusinessSettings(
    @DocumentId val id: String = "",
    val userId: String = "",
    val businessName: String = "ARAVALLI STEEL PVC FURNITURE",
    val phone: String = "+91 98250 12345",
    val whatsapp: String = "+91 98250 12345",
    val email: String = "aravallisteelpvc@gmail.com",
    val address: String = "Shyam Arcade, 14, Meghraj Road, near Bharwad Hostel, Modasa, Gujarat 383315",
    val gstin: String = "24AABCA1234F1Z5",
    val defaultGstRate: Double = 18.0,
    val estimateTerms: String = "1. 50% advance along with confirmed work order.\n2. 40% on delivery of materials at site.\n3. 10% balance upon final completion and quality check.\n4. Quotation valid for 15 days from date of issue.",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
