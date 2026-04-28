package com.rohan.fablefit.ui.model

// -----------------------------
// 🧾 PLACE ORDER RESPONSE
// -----------------------------
data class OrderPlaceResponse(
    val message: String,
    val order_id: String
)


// -----------------------------
// 🔍 TRACK ORDER RESPONSE
// -----------------------------
data class OrderTrackResponse(
    val order_id: String,
    val status: String,
    val delivery_date: String,
    val total: Double
)


// -----------------------------
// ❌ CANCEL ORDER RESPONSE
// -----------------------------
data class OrderCancelResponse(
    val message: String,
    val status: String
)


// -----------------------------
// 📦 ORDER ITEM (for list)
// -----------------------------
data class OrderItem(
    val product: String,
    val quantity: Int,
    val price: Double
)


// -----------------------------
// 📦 SINGLE ORDER (for list)
// -----------------------------
data class Order(
    val order_id: String,
    val status: String,
    val total: Double,
    val delivery_date: String,
    val items: List<OrderItem>
)


// -----------------------------
// 📦 ORDER LIST RESPONSE
// -----------------------------
data class OrderListResponse(
    val count: Int,
    val orders: List<Order>
)