package com.blogspot.mido_mymall.util

import android.util.Log // للتسجيل في حالة الخطأ
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * يحاول تحويل سلسلة نصية تمثل رقمًا (قد تحتوي على فواصل عشرية أو آلاف مختلفة) إلى Double.
 * يتعامل مع الفاصلة العشرية العربية (،) والإنجليزية (.) والفاصلة (,) كفاصل عشري محتمل.
 * يزيل فواصل الآلاف الشائعة.
 *
 * @param value السلسلة النصية المراد تحويلها.
 * @return قيمة Double المقابلة، أو 0.0 إذا كان الإدخال فارغًا أو غير صالح.
 */
fun safeParseDouble(value: String?): Double {
    if (value.isNullOrBlank()) {
        return 0.0
    }
    try {
        // استبدال الفواصل العربية بالإنجليزية
        var cleanValue = value.replace('،', '.').replace('٬', ',')

        // إزالة فواصل الآلاف (,) مع الحفاظ على الفاصلة العشرية الأخيرة
        val parts = cleanValue.split(".")
        if (parts.size > 1) {
            // إذا كان هناك فاصلة عشرية
            val integerPart = parts.dropLast(1).joinToString("").replace(",", "")
            cleanValue = "$integerPart.${parts.last()}"
        } else {
            // إذا لم يكن هناك فاصلة عشرية، أزل كل الفواصل
            cleanValue = cleanValue.replace(",", "")
        }

        return cleanValue.toDouble()
    } catch (e: NumberFormatException) {
        Log.e("SafeParseDouble", "فشل في تحويل السلسلة إلى Double: '$value'", e)
        return 0.0
    } catch (e: Exception) {
        Log.e("SafeParseDouble", "خطأ غير متوقع عند تحويل السلسلة: '$value'", e)
        return 0.0
    }
}