package com.group_7.library_management.ui.splash


import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group_7.library_management.R
import com.group_7.library_management.ui.theme.LibrarySpacing
import com.group_7.library_management.ui.theme.PrimaryBlue

@Composable
fun SplashScreen(
    onNext: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        onNext()
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary) // hoặc MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(LibrarySpacing.ExtraLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(painterResource(R.drawable.logo)
                , contentDescription = "logo"
                ,modifier= Modifier.height(150.dp).clip(MaterialTheme.shapes.extraLarge))
            Spacer(modifier= Modifier.height(LibrarySpacing.Large))
            Text(
                text = "Library Management",
                style = MaterialTheme.typography.headlineLarge,
                color= MaterialTheme.colorScheme.onPrimary
                )
            Spacer(modifier = Modifier.height(LibrarySpacing.Small))
            Text(
                text = "Tìm kiếm và mượn sách dễ dàng",
                style = MaterialTheme.typography.bodyLarge,
                color= MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Spacer(modifier= Modifier.height(LibrarySpacing.Large))
            CircularProgressIndicator(color=MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
        }
    }
}
