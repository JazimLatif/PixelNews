package com.jazim.pixelnews.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun CoinListItemView(
    modifier: Modifier,
    name: String,
    symbol: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                // Visualising the space at the start which some coin names have
                text = name.replaceFirst("^\\s".toRegex(), "␣"),
                // A few coins have very long names, so put an ellipse when they're too long
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = symbol,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
    HorizontalDivider(modifier = modifier.padding(vertical = 8.dp))
}
