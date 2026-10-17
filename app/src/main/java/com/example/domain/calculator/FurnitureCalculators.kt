package com.example.domain.calculator

import kotlin.math.roundToInt

object FurnitureCalculators {

    data class RailingResult(
        val runningFeet: Double,
        val heightInches: Double,
        val railingType: String,
        val style: String,
        val ratePerRft: Double,
        val baseRailingCost: Double,
        val glassSqFt: Double,
        val glassCost: Double,
        val fabricationCharges: Double,
        val installationCharges: Double,
        val subtotal: Double,
        val gstAmount: Double,
        val grandTotal: Double
    )

    fun calculateSsRailing(
        runningFeet: Double,
        heightInches: Double = 36.0,
        railingType: String = "SS 304", // SS 304 or SS 202
        isGlassRailing: Boolean = true,
        ratePerRft: Double = 1650.0,
        glassRatePerSqFt: Double = 180.0,
        fabricationPercent: Double = 10.0,
        installationPerRft: Double = 150.0,
        gstPercent: Double = 18.0
    ): RailingResult {
        val safeRft = if (runningFeet > 0) runningFeet else 0.0
        val baseCost = safeRft * ratePerRft
        val heightFeet = heightInches / 12.0
        val glassSqFt = if (isGlassRailing) safeRft * (heightFeet - 0.5) else 0.0
        val glassCost = if (isGlassRailing) glassSqFt * glassRatePerSqFt else 0.0
        val fabrication = (baseCost + glassCost) * (fabricationPercent / 100.0)
        val installation = safeRft * installationPerRft
        val subtotal = baseCost + glassCost + fabrication + installation
        val gst = subtotal * (gstPercent / 100.0)
        val grandTotal = subtotal + gst

        return RailingResult(
            runningFeet = safeRft,
            heightInches = heightInches,
            railingType = railingType,
            style = if (isGlassRailing) "SS 304 Glass Spigot" else "SS 304 Pipe Baluster",
            ratePerRft = ratePerRft,
            baseRailingCost = baseCost,
            glassSqFt = (glassSqFt * 100).roundToInt() / 100.0,
            glassCost = (glassCost * 100).roundToInt() / 100.0,
            fabricationCharges = (fabrication * 100).roundToInt() / 100.0,
            installationCharges = (installation * 100).roundToInt() / 100.0,
            subtotal = (subtotal * 100).roundToInt() / 100.0,
            gstAmount = (gst * 100).roundToInt() / 100.0,
            grandTotal = (grandTotal * 100).roundToInt() / 100.0
        )
    }

    data class BalconyResult(
        val widthFeet: Double,
        val heightFeet: Double,
        val areaSqFt: Double,
        val glassType: String,
        val ratePerSqFt: Double,
        val materialCost: Double,
        val hardwareCost: Double,
        val labourCost: Double,
        val subtotal: Double,
        val gstAmount: Double,
        val grandTotal: Double
    )

    fun calculateAluminiumBalcony(
        widthFeet: Double,
        heightFeet: Double,
        glassType: String = "12mm Toughened Glass",
        ratePerSqFt: Double = 420.0,
        hardwareCost: Double = 3500.0,
        labourPerSqFt: Double = 60.0,
        gstPercent: Double = 18.0
    ): BalconyResult {
        val area = (widthFeet * heightFeet).coerceAtLeast(0.0)
        val material = area * ratePerSqFt
        val labour = area * labourPerSqFt
        val subtotal = material + hardwareCost + labour
        val gst = subtotal * (gstPercent / 100.0)
        val grandTotal = subtotal + gst

        return BalconyResult(
            widthFeet = widthFeet,
            heightFeet = heightFeet,
            areaSqFt = (area * 100).roundToInt() / 100.0,
            glassType = glassType,
            ratePerSqFt = ratePerSqFt,
            materialCost = (material * 100).roundToInt() / 100.0,
            hardwareCost = hardwareCost,
            labourCost = (labour * 100).roundToInt() / 100.0,
            subtotal = (subtotal * 100).roundToInt() / 100.0,
            gstAmount = (gst * 100).roundToInt() / 100.0,
            grandTotal = (grandTotal * 100).roundToInt() / 100.0
        )
    }

    data class PvcFurnitureResult(
        val lengthFeet: Double,
        val heightFeet: Double,
        val totalSqFt: Double,
        val furnitureType: String,
        val sheetThickness: String,
        val ratePerSqFt: Double,
        val materialCost: Double,
        val fittingsCost: Double,
        val installationCost: Double,
        val subtotal: Double,
        val grandTotal: Double
    )

    fun calculatePvcFurniture(
        lengthFeet: Double,
        heightFeet: Double,
        furnitureType: String = "PVC Modular Wardrobe",
        sheetThickness: String = "18mm Waterproof PVC Sheet",
        ratePerSqFt: Double = 950.0,
        fittingsCost: Double = 4500.0,
        installationPerSqFt: Double = 80.0,
        discountPercent: Double = 0.0
    ): PvcFurnitureResult {
        val area = (lengthFeet * heightFeet).coerceAtLeast(0.0)
        val material = area * ratePerSqFt
        val installation = area * installationPerSqFt
        val subtotal = material + fittingsCost + installation
        val discount = subtotal * (discountPercent / 100.0)
        val grandTotal = subtotal - discount

        return PvcFurnitureResult(
            lengthFeet = lengthFeet,
            heightFeet = heightFeet,
            totalSqFt = (area * 100).roundToInt() / 100.0,
            furnitureType = furnitureType,
            sheetThickness = sheetThickness,
            ratePerSqFt = ratePerSqFt,
            materialCost = (material * 100).roundToInt() / 100.0,
            fittingsCost = fittingsCost,
            installationCost = (installation * 100).roundToInt() / 100.0,
            subtotal = (subtotal * 100).roundToInt() / 100.0,
            grandTotal = (grandTotal * 100).roundToInt() / 100.0
        )
    }

    data class KitchenResult(
        val runningFeet: Double,
        val layout: String,
        val baseCabinetCost: Double,
        val wallCabinetCost: Double,
        val basketAccessoriesCost: Double,
        val shutterFinish: String,
        val subtotal: Double,
        val grandTotal: Double
    )

    fun calculateModularKitchen(
        runningFeet: Double,
        layout: String = "L-Shape Modular Kitchen",
        hasWallCabinets: Boolean = true,
        baseRatePerRft: Double = 2200.0,
        wallRatePerRft: Double = 1800.0,
        basketAccessoriesCost: Double = 18500.0,
        shutterFinish: String = "High Gloss Acrylic PVC"
    ): KitchenResult {
        val safeRft = runningFeet.coerceAtLeast(0.0)
        val baseCost = safeRft * baseRatePerRft
        val wallCost = if (hasWallCabinets) safeRft * wallRatePerRft else 0.0
        val subtotal = baseCost + wallCost + basketAccessoriesCost
        val grandTotal = subtotal

        return KitchenResult(
            runningFeet = safeRft,
            layout = layout,
            baseCabinetCost = (baseCost * 100).roundToInt() / 100.0,
            wallCabinetCost = (wallCost * 100).roundToInt() / 100.0,
            basketAccessoriesCost = basketAccessoriesCost,
            shutterFinish = shutterFinish,
            subtotal = (subtotal * 100).roundToInt() / 100.0,
            grandTotal = (grandTotal * 100).roundToInt() / 100.0
        )
    }

    data class UnitConversion(
        val mm: Double,
        val cm: Double,
        val inches: Double,
        val feet: Double,
        val sqFeet: Double
    )

    fun convertUnits(inputValue: Double, fromUnit: String, heightValueForArea: Double = 0.0): UnitConversion {
        val valueInMm = when (fromUnit.lowercase()) {
            "mm", "millimetre", "millimeter" -> inputValue
            "cm", "centimetre", "centimeter" -> inputValue * 10.0
            "inch", "inches", "in" -> inputValue * 25.4
            "foot", "feet", "ft" -> inputValue * 304.8
            "meter", "metre", "m" -> inputValue * 1000.0
            else -> inputValue
        }

        val mm = valueInMm
        val cm = valueInMm / 10.0
        val inches = valueInMm / 25.4
        val feet = valueInMm / 304.8
        val sqFeet = if (heightValueForArea > 0) {
            val hFeet = heightValueForArea // assume feet
            feet * hFeet
        } else {
            0.0
        }

        return UnitConversion(
            mm = (mm * 100).roundToInt() / 100.0,
            cm = (cm * 100).roundToInt() / 100.0,
            inches = (inches * 100).roundToInt() / 100.0,
            feet = (feet * 100).roundToInt() / 100.0,
            sqFeet = (sqFeet * 100).roundToInt() / 100.0
        )
    }
}
