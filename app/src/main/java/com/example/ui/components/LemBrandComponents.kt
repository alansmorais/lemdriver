package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AmberHighlight
import com.example.ui.theme.AmberVibrant
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.SurfaceWhite

/**
 * High-fidelity Litoral em Movimento Brand Logo with Emblem and Luxury Typography
 * Matching the exact official company logo (Spin 7L, Palm Tree, Waves, Sun Arch)
 */
@Composable
fun LemBrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    showTypography: Boolean = true,
    isDarkTheme: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Circular Emblem Badge
        Box(
            modifier = Modifier
                .size(size)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(SurfaceWhite),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_lem_logo),
                contentDescription = "Logo Litoral em Movimento",
                modifier = Modifier.size(size)
            )
        }

        if (showTypography) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "LITORAL",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp,
                        color = if (isDarkTheme) SurfaceWhite else Slate900,
                        fontSize = 20.sp
                    )
                )

                // Gold divider line with "EM MOVIMENTO"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(1.dp)
                            .background(AmberHighlight)
                    )
                    Text(
                        text = "EM MOVIMENTO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = AmberHighlight,
                            fontSize = 10.sp
                        )
                    )
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(1.dp)
                            .background(AmberHighlight)
                    )
                }

                Text(
                    text = "CONFORTO • SEGURANÇA • PONTUALIDADE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isDarkTheme) Slate300 else Slate500,
                        letterSpacing = 1.sp,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Developer Credit Footer for AlanSMSolutions.com
 */
@Composable
fun DeveloperCreditFooter(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false
) {
    val context = LocalContext.current
    val url = "https://alansmsolutions.com"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Safe handling
                }
            }
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .testTag("footer_developer_credit"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isDarkTheme) SurfaceWhite.copy(alpha = 0.08f) else Slate900.copy(alpha = 0.04f))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = null,
                tint = if (isDarkTheme) AmberHighlight else AmberVibrant,
                modifier = Modifier.size(13.dp)
            )

            Text(
                text = "Desenvolvido por ",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = if (isDarkTheme) Slate300 else Slate500
                )
            )

            Text(
                text = "AlanSMSolutions.com",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) SurfaceWhite else Slate900
                )
            )

            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = "Abrir site",
                tint = if (isDarkTheme) Slate300 else Slate500,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
