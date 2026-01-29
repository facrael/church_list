package com.religiousmarket.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.religiousmarket.app.ui.theme.*

@Composable
fun ProfileScreen(
    onDonateClick: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    val colors = LocalAppColors.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Profile header
        item {
            ProfileHeader()
        }

        // Quick stats
        item {
            QuickStats()
        }

        // Menu sections
        item {
            SectionTitle("Мои данные")
        }

        item {
            MenuItem(
                icon = Icons.Outlined.FavoriteBorder,
                title = "Избранное",
                subtitle = "Сохранённые храмы",
                onClick = { }
            )
        }

        item {
            MenuItem(
                icon = Icons.Outlined.History,
                title = "История платежей",
                subtitle = "Все транзакции",
                onClick = { }
            )
        }

        item {
            MenuItem(
                icon = Icons.Outlined.FavoriteBorder,
                title = "Мои пожертвования",
                subtitle = "История пожертвований",
                onClick = onDonateClick
            )
        }

        item {
            SectionTitle("Настройки")
        }

        item {
            MenuItemWithToggle(
                icon = Icons.Outlined.Notifications,
                title = "Уведомления",
                subtitle = "Push-уведомления",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
        }

        item {
            MenuItem(
                icon = Icons.Outlined.Language,
                title = "Язык",
                subtitle = "Русский",
                onClick = { }
            )
        }

        item {
            MenuItem(
                icon = Icons.Outlined.Palette,
                title = "Тема",
                subtitle = "Тёмная",
                onClick = { }
            )
        }

        item {
            SectionTitle("Поддержка")
        }

        item {
            MenuItem(
                icon = Icons.Outlined.Help,
                title = "Помощь",
                subtitle = "FAQ и поддержка",
                onClick = { }
            )
        }

        item {
            MenuItem(
                icon = Icons.Outlined.Info,
                title = "О приложении",
                subtitle = "Версия 1.0.0",
                onClick = { }
            )
        }

        item {
            MenuItem(
                icon = Icons.Outlined.Policy,
                title = "Политика конфиденциальности",
                onClick = { }
            )
        }

        // Logout button
        item {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Выйти из аккаунта")
            }
        }
    }
}

@Composable
private fun ProfileHeader() {
    val colors = LocalAppColors.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Accent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "А",
                    style = MaterialTheme.typography.headlineLarge,
                    color = PrimaryDark,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Алексей Иванов",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextOnPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "user@example.com",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextOnPrimary.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Accent.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "☦ Православие",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Accent
                    )
                }
            }

            IconButton(onClick = { }) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Edit",
                    tint = TextOnPrimary
                )
            }
        }
    }
}

@Composable
private fun QuickStats() {
    val colors = LocalAppColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            value = "12",
            label = "Записей",
            icon = Icons.Filled.EventNote,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "5",
            label = "Избранное",
            icon = Icons.Filled.Favorite,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            value = "8 500 ₽",
            label = "Пожертвования",
            icon = Icons.Filled.Favorite,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    val colors = LocalAppColors.current

    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        style = MaterialTheme.typography.titleSmall,
        color = colors.textSecondary,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun MenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    val colors = LocalAppColors.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = colors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = colors.textSecondary
            )
        }
    }
}

@Composable
private fun MenuItemWithToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalAppColors.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
