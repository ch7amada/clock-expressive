package org.fossify.clock.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.fossify.clock.R

// Google Sans Flex (variable, OFL) — one Font per weight we use, all softened with ROND 100.
val GoogleSansFlex: FontFamily = FontFamily(
    Font(R.font.google_sans_flex, variationSettings = FontVariation.Settings(FontVariation.weight(400), FontVariation.Setting("ROND", 100f))),
    Font(R.font.google_sans_flex, variationSettings = FontVariation.Settings(FontVariation.weight(500), FontVariation.Setting("ROND", 100f))),
    Font(R.font.google_sans_flex, variationSettings = FontVariation.Settings(FontVariation.weight(600), FontVariation.Setting("ROND", 100f))),
    Font(R.font.google_sans_flex, variationSettings = FontVariation.Settings(FontVariation.weight(700), FontVariation.Setting("ROND", 100f))),
    Font(R.font.google_sans_flex, variationSettings = FontVariation.Settings(FontVariation.weight(800), FontVariation.Setting("ROND", 100f))),
)

// GoogleSansFlex base: applies the family to all 30 styles, incl. the *Emphasized ones.
private val FlexBase = Typography(fontFamily = GoogleSansFlex)

// Emphasized display/headline styles are bold, emphasized title/label styles semibold.
val ClockTypography = FlexBase.copy(
    displayLargeEmphasized = FlexBase.displayLargeEmphasized.copy(fontWeight = FontWeight.Bold),
    displayMediumEmphasized = FlexBase.displayMediumEmphasized.copy(fontWeight = FontWeight.Bold),
    displaySmallEmphasized = FlexBase.displaySmallEmphasized.copy(fontWeight = FontWeight.Bold),
    headlineLargeEmphasized = FlexBase.headlineLargeEmphasized.copy(fontWeight = FontWeight.Bold),
    headlineMediumEmphasized = FlexBase.headlineMediumEmphasized.copy(fontWeight = FontWeight.Bold),
    headlineSmallEmphasized = FlexBase.headlineSmallEmphasized.copy(fontWeight = FontWeight.Bold),
    titleLargeEmphasized = FlexBase.titleLargeEmphasized.copy(fontWeight = FontWeight.SemiBold),
    titleMediumEmphasized = FlexBase.titleMediumEmphasized.copy(fontWeight = FontWeight.SemiBold),
    titleSmallEmphasized = FlexBase.titleSmallEmphasized.copy(fontWeight = FontWeight.SemiBold),
    labelLargeEmphasized = FlexBase.labelLargeEmphasized.copy(fontWeight = FontWeight.SemiBold),
    labelMediumEmphasized = FlexBase.labelMediumEmphasized.copy(fontWeight = FontWeight.SemiBold),
    labelSmallEmphasized = FlexBase.labelSmallEmphasized.copy(fontWeight = FontWeight.SemiBold),
)

// Hero styles for big time displays: each carries its own single-font GoogleSansFlex family
// with explicit variable-font axes and tabular digits.
object ClockTextStyles {
    fun heroWith(weight: Int, width: Float = 100f, sizeSp: Int = 96): TextStyle = TextStyle(
        fontFamily = FontFamily(
            Font(
                R.font.google_sans_flex,
                variationSettings = FontVariation.Settings(
                    FontVariation.weight(weight),
                    FontVariation.width(width),
                    FontVariation.Setting("ROND", 100f),
                ),
            ),
        ),
        fontSize = sizeSp.sp,
        lineHeight = sizeSp.sp,
        fontFeatureSettings = "tnum",
    )

    val heroClock: TextStyle = heroWith(weight = 400, width = 100f, sizeSp = 96)

    val heroClockCompact: TextStyle = heroWith(weight = 400, width = 85f, sizeSp = 72)

    val timer: TextStyle = heroWith(weight = 300, width = 100f, sizeSp = 64)

    val listTime: TextStyle = heroWith(weight = 500, width = 100f, sizeSp = 48)
}
