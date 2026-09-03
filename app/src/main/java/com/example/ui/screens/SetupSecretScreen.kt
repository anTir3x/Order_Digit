package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DigitBoxes
import com.example.ui.components.NumberKeypad
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.OrderCyan

@Composable
fun SetupSecretScreen(
    title: String,
    subtitle: String,
    currentInput: String,
    isMasked: Boolean,
    errorMessage: String?,
    onDigitClick: (Char) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onToggleMask: () -> Unit,
    onRandomize: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top bar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("btn_setup_back")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onToggleMask,
                        modifier = Modifier.testTag("btn_toggle_mask")
                    ) {
                        Icon(
                            imageVector = if (isMasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Secret Visibility",
                            tint = OrderCyan
                        )
                    }

                    IconButton(
                        onClick = onRandomize,
                        modifier = Modifier.testTag("btn_randomize_secret")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Random Secret",
                            tint = IndigoPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Icon badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(IndigoPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = IndigoPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                textAlign = TextAlign.Center
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Digit display boxes
            DigitBoxes(
                input = currentInput,
                isMasked = isMasked,
                hasError = errorMessage != null,
                modifier = Modifier.testTag("setup_digit_boxes")
            )

            // Rule hints & Error message
            Spacer(modifier = Modifier.height(10.dp))

            AnimatedVisibility(visible = errorMessage != null) {
                if (errorMessage != null) {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            if (errorMessage == null) {
                Text(
                    text = "Rule: 4 distinct digits • First digit cannot be 0",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }

        // Custom Numeric Keypad
        NumberKeypad(
            currentInput = currentInput,
            onDigitClick = onDigitClick,
            onBackspace = onBackspace,
            onClear = onClear,
            onSubmit = onSubmit,
            submitLabel = "Lock in Secret Code",
            isSubmitEnabled = currentInput.length == 4,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}
